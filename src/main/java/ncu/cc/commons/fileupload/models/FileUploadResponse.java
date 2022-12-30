package ncu.cc.commons.fileupload.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class FileUploadResponse {
    public static class File {
        private String name;
        private String type;
        private long size;
        private String url;
        private String thumbnailUrl;
        private String deleteUrl;
        private String deleteType;
        @JsonIgnore
        private String thumbnailFileName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @JsonProperty("thumbnailUrl")
        public String getThumbnailUrl() {
            return thumbnailUrl;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        @JsonProperty("deleteUrl")
        public String getDeleteUrl() {
            return deleteUrl;
        }

        public void setDeleteUrl(String deleteUrl) {
            this.deleteUrl = deleteUrl;
        }

        @JsonProperty("deleteType")
        public String getDeleteType() {
            return deleteType;
        }

        public void setDeleteType(String deleteType) {
            this.deleteType = deleteType;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getThumbnailFileName() {
            return thumbnailFileName;
        }

        public void setThumbnailFileName(String thumbnailFileName) {
            this.thumbnailFileName = thumbnailFileName;
        }
    }

    private final List<File> files;

    public FileUploadResponse() {
        files = new ArrayList<>();
    }

    public List<File> getFiles() {
        return files;
    }

    public void add(File file) {
        files.add(file);
    }
}
