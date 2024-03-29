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
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import jobs.FetchDocumentThumbnailJob;
import jobs.IncrementDocumentCopyCountJob;
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

    @NoBinding
    @JsonProperty
    public String alternateLink;

    @Required
    @ManyToOne
    @JsonProperty
    @CheckWith(CategoryCheck.class)
    public Category category;

    @NoBinding
    @JsonProperty
    public int copyCount;

    @NoBinding
    @JsonProperty
    public int commentCount;

    @JsonProperty
    @Required
    @MaxSize(5000)
    public String description;

    @JsonProperty
    @NoBinding
    public int discussionCount;

    @NoBinding
    @JsonProperty
    public int downloadCount;

    @NoBinding
    @JsonProperty
    public String embedLink;

    @Required
    @NoBinding
    @JsonProperty
    public long fileSize;

    @NoBinding
    @JsonProperty
    public String googleDriveFileId;

    @NoBinding
    @JsonProperty
    public int voteCount;

    @CheckWith(MimeTypeCheck.class)
    @NoBinding
    @JsonProperty
    private String mimeType;

    @NoBinding
    @JsonProperty
    public String modifiedDate;

    @ManyToOne
    @NoBinding
    @JsonProperty
    public User owner;

    @NoBinding
    @JsonProperty
    public int viewCount;

    @NoBinding
    public String slug;

    @JsonProperty
    public String source;

    @Required
    @MaxSize(1000)
    public String tags;

    @OneToOne(cascade = CascadeType.REMOVE)
    @NoBinding
    public Thumbnail thumbnail;

    @Required
    @JsonProperty
    public String title;

    public boolean belongsToUser(User user) {
        if (user != null) {
            if (this.owner != null) {
                if (this.owner.id == user.id) {
                    return true;
                }
            }
        }
        return false;
    }

    public File copyForUser(User user) throws IOException {

        if (this.owner != user) {
            Drive driveService = GoogleOAuth.buildDriveServiceForUser(user);

            File copiedFile = new File();
            copiedFile.setTitle(this.title);
            copiedFile = driveService.files().copy(this.googleDriveFileId, copiedFile).setConvert(true).execute();
            new IncrementDocumentCopyCountJob(this.id).in(30);
            return copiedFile;
        }
        return null;
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

    @JsonGetter(value="tags")
    public String[] getTagsAsList() {
        String[] tagList = {}; 
        if (this.tags != null) {
            tagList = tags.split(",");
            for (int i = 0; i < tagList.length; i++) {
                tagList[i] = tagList[i].trim();
            }
        }
        return tagList;
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
    public int updateCommentCountAndSave(boolean increase) {
        if (increase) {
            this.commentCount++;
        } else {
            this.commentCount--;
        }
        this.save();
        return this.commentCount;
    }

    @Transactional
    public int updateDiscussionCountAndSave(boolean increase) {
        if (increase) {
            this.discussionCount++;
        } else {
            this.discussionCount--;
        }
        this.save();
        return this.discussionCount;
    }

    @Transactional
    public int updateVoteCountAndSave(boolean increment) {
        if (increment) {
            this.voteCount++;
        } else {
            this.voteCount--;
        }
        this.save();
        return this.voteCount;
    }

    @Transactional
    public void incrementCopyCountAndSave() {
        this.copyCount++;
        this.save();
    }

    @Transactional
    public void incrementDownloadCountAndSave() {
        this.downloadCount++;
        this.save();
    }

    @Transactional
    public void incrementViewCountAndSave() {
        this.viewCount++;
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

    @Override
    public String toString() {
        return this.title;
    }

    public void updateOnGoogleDrive() throws IOException {
        File driveFile = new File();
        driveFile.setTitle(this.title);
        Drive driveService = GoogleOAuth.buildDriveServiceForUser(this.owner);
        driveService.files().update(this.googleDriveFileId, driveFile);
    }

    public void uploadToGoogleDriveAndSave(Upload file) throws IOException {
        if (file != null) {
            File driveFile = new File();
            driveFile.setTitle(this.title);
            driveFile.setIndexableText(new File.IndexableText().setText(this.description));
            driveFile.setMimeType(file.getContentType());
            FileContent driveFileContent = new FileContent(file.getContentType(), file.asFile());
            Drive driveService = GoogleOAuth.buildDriveServiceForUser(this.owner);

            File insertedDriveFile = driveService.files().insert(driveFile, driveFileContent).setConvert(true).execute();

            Permission permission = new Permission();
            permission.setType("anyone");
            permission.setRole("reader");
            driveService.permissions().insert(insertedDriveFile.getId(), permission).execute();

            this.alternateLink = insertedDriveFile.getAlternateLink();

            this.googleDriveFileId = insertedDriveFile.getId();
            this.modifiedDate = insertedDriveFile.getModifiedDate().toString();
            this.fileSize = file.getSize();

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
        } else {
            throw new RuntimeException("File is null.");
        }

    }

    public static List<Document> findByCategory(Category category, String order, Integer page) {
        return search(null, category.id, order, page);
    }

    public static List<Document> search(String keyword, long categoryId, String order, Integer page) {
        List<Document> documents = new ArrayList<Document>();
        StringBuilder sb = new StringBuilder();

        sb.append("googleDriveFileId != null");

        if ((keyword != null) && (keyword.length() > 0)) {
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
        } else if (order.equals("views")) {
            sb.append(" order by viewCount desc ");
        } else if (order.equals("downloads")) {
            sb.append(" order by downloadCount desc ");
        } else if (order.equals("copies")) {
            sb.append(" order by copyCount desc ");
        } else if (order.equals("comments")) {
            sb.append(" order by commentCount desc ");
        } else if (order.equals("votes")) {
            sb.append(" order by voteCount desc ");
        } else if (order.equals("discussions")){
            sb.append(" order by discussionCount desc ");
        } else {
            throw new RuntimeException("Invalid Sort Order");
        }

        if (sb.toString() != "") {
            JPAQuery query = Document.find(sb.toString());

            if ((keyword != null) && (keyword.length() > 0)) {
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

    public static List<Document> findByUser(User user) {
        List<Document> documents = new ArrayList<Document>();
        if (user != null) {
            documents = Document.find("owner is ? order by created desc", user).fetch(DEFAULT_PAGINATE_COUNT);
        }
        return documents;
    }

    public static class MimeTypeCheck extends Check {

        @Override
        public boolean isSatisfied(Object document, Object mimeTypeName) {
            this.setMessage("validation.mimetype.invalid", (String) mimeTypeName);
            Mime mimeType = Mime.parseName((String) mimeTypeName);
            return (mimeType != null);
        }

    }

    public static class CategoryCheck extends Check {

        @Override
        public boolean isSatisfied(Object document, Object category) {
            this.setMessage("Invalid Category");
            return (category.toString().contains("document"));
        }

    }
}
