package models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import jobs.FetchDocumentThumbnailJob;
import jobs.IncrementDocumentCloneCountJob;
import models.enums.Mime;
import play.data.Upload;
import play.data.binding.NoBinding;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.db.jpa.Transactional;
import play.templates.JavaExtensions;
import services.googleoauth.GoogleOAuth;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.persistence.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
public class Document extends BaseModel {

    @Required
    @JsonProperty
    public String title;

    @NoBinding
    public String slug;

    @JsonProperty
    public String description;

    @Required
    @ManyToOne
    @JsonProperty
    public Category category;


    @ManyToOne
    @NoBinding
    @JsonProperty
    public User owner;

    @NoBinding
    @JsonProperty
    public String googleDriveFileId;

    @CheckWith(MimeTypeCheck.class)
    @NoBinding
    @JsonProperty
    private String mimeType;

    @NoBinding
    @JsonProperty
    public String modifiedDate;

    @NoBinding
    @JsonProperty
    public String embedLink;

    @NoBinding
    @JsonProperty
    public String alternateLink;

    @NoBinding
    @JsonProperty
    public int downloadCount;

    @NoBinding
    @JsonProperty
    public int readCount;

    @NoBinding
    @JsonProperty
    public int commentCount;

    @NoBinding
    @JsonProperty
    public int cloneCount;

    @Required
    public transient Upload file;

    @Required
    @NoBinding
    @JsonProperty
    public long fileSize;

    @JsonProperty
    public String source;

    @OneToOne
    @NoBinding
    public Thumbnail thumbnail;

    @ManyToOne
    public Document originalDocument;

    public boolean isArchived;

    public Mime getMime() {
        return Mime.parseName(this.mimeType);
    }

    public void setMime(Mime mime) {
        if (mime != null) {
            this.mimeType = mime.getType();
        }
    }

    @JsonGetter
    public List<ExportLink> getExportLinks() {
        if (this.id != null) {
            return ExportLink.find("document is ?", this).fetch();
        } else {
            return null;
        }
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
    public void incrementCloneCount() {
        this.cloneCount++;
        this.save();
    }

    @Transactional
    public void decreaseCloneCount() {
        this.cloneCount--;
        this.save();
    }

    @Transactional
    public void incrementReadCount() {
        this.readCount++;
        this.save();
    }

    @Transactional
    public void incrementDownloadCount() {
        this.downloadCount++;
        this.save();
    }

    public ExportLink getExportLinkForMime(Mime mime) {
        return ExportLink.find("document is ? and mimeType is ?", this, mime.getType()).first();
    }

    public void updateOnGoogleDrive() throws IOException {
        File driveFile = new File();
        driveFile.setTitle(this.title);
        Drive driveService = GoogleOAuth.buildDriveServiceForUser(this.owner);
        driveService.files().update(this.googleDriveFileId, driveFile);
    }
    public void fetchThumbnailFromGoogleDrive() throws IOException {
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

    public Document copyToGoogleDrive(User user) throws IOException {
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

    public void uploadToGoogleDrive() throws IOException {
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

        new FetchDocumentThumbnailJob(this.id).in(30);

        //this.category.documentCount = this.category.documentCount + 1;
        //this.category.save();

    }

    @PrePersist
    @PreUpdate
    protected void preSave() {
        this.slug = JavaExtensions.slugify(this.title);
    }

    public static List<Document> search(long categoryId, String order, String keyword, int start) {
        return null;
    }

    public File fetchFileFromGoogleDrive() throws IOException {
        Drive driveService = GoogleOAuth.buildDriveServiceForUser(this.owner);
        File driveFile = driveService.files().get(this.id.toString()).execute();
        return driveFile;
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

        if (page == null || page < 1) {
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

            if (page == null || page < 1) {
                page = 1;
            }


            return Document.find(queryString, category, false).fetch(page, DEFAULT_PAGINATE_COUNT);
        } else {
            return null;
        }

    }


    public static class MimeTypeCheck extends Check {

        public boolean isSatisfied(Object document, Object mimeTypeName) {
            setMessage("validation.mimetype.invalid", (String) mimeTypeName);
            Mime mimeType = Mime.parseName((String) mimeTypeName);
            return (mimeType != null);
        }

    }
}
