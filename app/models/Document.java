package models;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import jobs.FetchDocumentThumbnailJob;
import jobs.IncrementDocumentCloneCountJob;
import models.enums.Mime;
import play.data.Upload;
import play.data.binding.NoBinding;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Transactional;
import play.templates.JavaExtensions;
import services.googleoauth.GoogleOAuth;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;

@Entity(name = "document")
public class Document extends BaseModel {

    public static class MimeTypeCheck extends Check {

        @Override
        public boolean isSatisfied(Object document, Object mimeTypeName) {
            this.setMessage("validation.mimetype.invalid", (String) mimeTypeName);
            Mime mimeType = Mime.parseName((String) mimeTypeName);
            return (mimeType != null);
        }

    }

    @NoBinding
    @JsonProperty
    public String alternateLink;

    @Required
    @ManyToOne
    @JsonProperty
    public Category category;

    @NoBinding
    @JsonProperty
    public int cloneCount;

    @NoBinding
    @JsonProperty
    public int commentCount;

    @Lob
    @JsonProperty
    @MaxSize(5000)
    @Required        
    public String description;

    @NoBinding
    @JsonProperty
    public int downloadCount;

    @NoBinding
    @JsonProperty
    public String embedLink;

    @Required
    public transient Upload file;

    @Required
    @NoBinding
    @JsonProperty
    public long fileSize;

    @NoBinding
    @JsonProperty
    public String googleDriveFileId;

    public boolean isArchived;

    @CheckWith(MimeTypeCheck.class)
    @NoBinding
    @JsonProperty
    private String mimeType;

    @NoBinding
    @JsonProperty
    public String modifiedDate;

    @ManyToOne
    public Document originalDocument;

    @ManyToOne
    @NoBinding
    @JsonProperty
    public User owner;

    @NoBinding
    @JsonProperty
    public int readCount;

    @NoBinding
    public String slug;

    @JsonProperty
    public String source;

    @OneToOne
    @NoBinding
    public Thumbnail thumbnail;

    @Required
    @JsonProperty
    public String title;

    public Document cloneForUserAndSave(User user) throws IOException {
        if (this.owner != user) {
            Drive driveService = GoogleOAuth.buildDriveServiceForUser(user);

            File copiedFile = new File();
            copiedFile.setTitle(this.title);
            copiedFile = driveService.files().copy(this.googleDriveFileId, copiedFile).setConvert(true).execute();
            Permission permission = new Permission();
            permission.setType("anyone");
            permission.setRole("reader");
            driveService.permissions().insert(copiedFile.getId(), permission).execute();
            Document copiedDocument = new Document();
            copiedDocument.title = copiedFile.getTitle();
            copiedDocument.category = this.category;
            copiedDocument.originalDocument = this;
            copiedDocument.owner = user;
            copiedDocument.alternateLink = copiedFile.getAlternateLink();
            copiedDocument.embedLink = copiedFile.getEmbedLink();
            copiedDocument.googleDriveFileId = copiedFile.getId();
            copiedDocument.modifiedDate = copiedFile.getModifiedDate().toString();
            copiedDocument.fileSize = this.fileSize;
            copiedDocument.mimeType = this.mimeType;
            if (copiedFile.getEmbedLink() != null) {
                copiedDocument.embedLink = copiedFile.getEmbedLink();
            } else {
                copiedDocument.embedLink = "https://docs.google.com/file/d/" + copiedDocument.googleDriveFileId + "/preview";
            }

            copiedDocument.save();

            if (copiedFile.getExportLinks() != null) {
                for (Map.Entry<String, String> link : copiedFile.getExportLinks().entrySet()) {
                    new ExportLink(copiedDocument, link.getKey(), link.getValue()).save();
                }
            } else {
                ExportLink exportLink = new ExportLink(copiedDocument, copiedFile.getMimeType(), "https://docs.google.com/uc?id=" + copiedFile.getId() + "&export=download");
                exportLink.save();
            }

            if (this.thumbnail != null) {
                Thumbnail copiedDocumentThumbnail = new Thumbnail();
                copiedDocumentThumbnail.image = this.thumbnail.image;
                copiedDocumentThumbnail.mimeType = this.thumbnail.mimeType;
                copiedDocumentThumbnail.save();
                copiedDocument.thumbnail = copiedDocumentThumbnail;
                copiedDocument.save();
            } else {
                new FetchDocumentThumbnailJob(copiedDocument.id).in(30);
            }

            new IncrementDocumentCloneCountJob(this.id).in(30);
            return copiedDocument;
        }
        return null;
    }

    @Transactional
    public void decreaseCloneCountAndSave() {
        this.cloneCount--;
        this.save();
    }

    public File fetchFileFromGoogleDrive() throws IOException {
        Drive driveService = GoogleOAuth.buildDriveServiceForUser(this.owner);
        File driveFile = driveService.files().get(this.id.toString()).execute();
        return driveFile;
    }

    public void fetchThumbnailFromGoogleDriveAndSave() throws IOException {
        if (this.id != null) {
            URL url = new URL(String.format("https://docs.google.com/viewer?url=%s&export=download&a=bi&pagenumber=1&w=300", URLEncoder.encode(this.getExportLinkForMime(Mime.ADOBE_PDF).link, "UTF-8")));
            ImageReader reader = ImageIO.getImageReadersBySuffix("png").next();
            reader.setInput(ImageIO.createImageInputStream(url.openStream()));
            int i = reader.getMinIndex();
            try {
                while (true) {
                    BufferedImage thumbnailImage = reader.read(i++);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(thumbnailImage, "png", outputStream);

                    Thumbnail documentThumbnail = new Thumbnail();
                    documentThumbnail.image = outputStream.toByteArray();
                    documentThumbnail.mimeType = "image/png";
                    documentThumbnail.save();

                    this.thumbnail = documentThumbnail;
                    this.save();
                }
            } catch (IndexOutOfBoundsException ex) {
                // do nothing
            }
        }
    }

    public ExportLink getExportLinkForMime(Mime mime) {
        return ExportLink.find("document is ? and mimeType is ?", this, mime.getType()).first();
    }

    @JsonGetter
    public List<ExportLink> getExportLinks() {
        if (this.id != null) {
            return ExportLink.find("document is ?", this).fetch();
        } else {
            return null;
        }
    }

    public Mime getMime() {
        return Mime.parseName(this.mimeType);
    }

    @JsonGetter
    public String getPermalink() {
        return String.format("/documents/%s-%s", this.id, this.slug);
    }

    @JsonGetter
    public String getThumbnailUrl() {
        if (this.id != null) {
            return String.format("/api/documents/%s/thumbnail", this.id);
        } else {
            return null;
        }
    }

    @Transactional
    public void incrementCommentCountAndSave() {
        this.commentCount++;
        this.save();
    }
    
    @Transactional
    public void incrementCloneCountAndSave() {
        this.cloneCount++;
        this.save();
    }

    @Transactional
    public void incrementDownloadCountAndSave() {
        this.downloadCount++;
        this.save();
    }

    @Transactional
    public void incrementReadCountAndSave() {
        this.readCount++;
        this.save();
    }

    @PrePersist
    @PreUpdate
    protected void preSave() {
        this.slug = JavaExtensions.slugify(this.title);
    }

    public void setMime(Mime mime) {
        if (mime != null) {
            this.mimeType = mime.getType();
        }
    }

    public void updateOnGoogleDrive() throws IOException {
        File driveFile = new File();
        driveFile.setTitle(this.title);
        Drive driveService = GoogleOAuth.buildDriveServiceForUser(this.owner);
        driveService.files().update(this.googleDriveFileId, driveFile);
    }

    public void uploadToGoogleDriveAndSave() throws IOException {
        File driveFile = new File();
        driveFile.setTitle(this.title);
        driveFile.setIndexableText(new File.IndexableText().setText(this.description));
        driveFile.setMimeType(this.file.getContentType());
        FileContent driveFileContent = new FileContent(this.file.getContentType(), this.file.asFile());
        Drive driveService = GoogleOAuth.buildDriveServiceForUser(this.owner);

        File insertedDriveFile = driveService.files().insert(driveFile, driveFileContent).setConvert(true).execute();

        Permission permission = new Permission();
        permission.setType("anyone");
        permission.setRole("reader");
        driveService.permissions().insert(insertedDriveFile.getId(), permission).execute();

        this.alternateLink = insertedDriveFile.getAlternateLink();

        this.googleDriveFileId = insertedDriveFile.getId();
        this.modifiedDate = insertedDriveFile.getModifiedDate().toString();
        this.fileSize = this.file.getSize();
        this.isArchived = false;

        // For some reasons the embedLink property for PDFs is null
        if (insertedDriveFile.getEmbedLink() != null) {
            this.embedLink = insertedDriveFile.getEmbedLink();
        } else {
            this.embedLink = "https://docs.google.com/file/d/" + this.googleDriveFileId + "/preview";
        }

        this.save();

        if (insertedDriveFile.getExportLinks() != null) {
            for (Map.Entry<String, String> link : insertedDriveFile.getExportLinks().entrySet()) {
                new ExportLink(this, link.getKey(), link.getValue()).save();
            }
        } else {
            ExportLink exportLink = new ExportLink(this, insertedDriveFile.getMimeType(), "https://docs.google.com/uc?id=" + insertedDriveFile.getId() + "&export=download");
            exportLink.save();
        }

        // We can fetch the thumbnail later on
        new FetchDocumentThumbnailJob(this.id).in(30);

        // this.category.documentCount = this.category.documentCount + 1;
        // this.category.save();

    }

    public static List<Document> find(String order, Integer page) {
        String queryString = "originalDocument.id is null and isArchived is ?";

        if (order == null) {
            order = "recent";
        }

        if (order.equals("recent")) {
            queryString += " order by created desc ";
        } else if (order.equals("reads")) {
            queryString += " order by readCount desc ";
        } else if (order.equals("downloads")) {
            queryString += " order by downloadCount desc ";
        } else if (order.equals("clones")) {
            queryString += " order by cloneCount desc ";
        }

        if ((page == null) || (page < 1)) {
            page = 1;
        }

        JPAQuery query = Document.find(queryString, false);

        return query.fetch(page, DEFAULT_PAGINATE_COUNT);
    }

    public static List<Document> findByCategory(Category category, String order, Integer page) {
        if (category != null) {
            String queryString = "originalDocument.id is null and category is ? and isArchived is ?";

            if (order == null) {
                order = "recent";
            }

            if (order.equals("recent")) {
                queryString += " order by created desc ";
            } else if (order.equals("reads")) {
                queryString += " order by readCount desc ";
            } else if (order.equals("downloads")) {
                queryString += " order by downloadCount desc ";
            } else if (order.equals("clones")) {
                queryString += " order by cloneCount desc ";
            }

            if ((page == null) || (page < 1)) {
                page = 1;
            }

            return Document.find(queryString, category, false).fetch(page, DEFAULT_PAGINATE_COUNT);
        } else {
            return null;
        }

    }

    public static List<Document> search(String keyword, long categoryId, String order, Integer page) {
    	List<Document>documents = new ArrayList<Document>();
    	StringBuilder sb = new StringBuilder();
    	
    	sb.append("originalDocument is null and isArchived is false");

    	if (keyword != null) {
    		sb.append(" and fts(:keyword) = true");
    	}
    	
    	if (categoryId > 0) {
    		sb.append(" and category.id is :categoryId");
    	}
    	
    	if (order == null) {
            order = "recent";
        }

        if (order.equals("recent")) {
            sb.append(" order by created desc ");
        } else if (order.equals("reads")) {
            sb.append(" order by readCount desc ");
        } else if (order.equals("downloads")) {
            sb.append(" order by downloadCount desc ");
        } else if (order.equals("clones")) {
            sb.append(" order by cloneCount desc ");
        }
        
    	
    	if (sb.toString() != "") {
    		JPAQuery query = Document.find(sb.toString()); 
    	
    		if (keyword != null) {
    			query.setParameter("keyword", keyword);
    		}

    		if (categoryId > 0) {
    			query.setParameter("categoryId", categoryId);
    		}

    		if ((page == null) || (page < 1)) {
                page = 1;
            }
    		
    		documents = query.fetch(page, DEFAULT_PAGINATE_COUNT);
    	}
    	return documents;
    }
}
