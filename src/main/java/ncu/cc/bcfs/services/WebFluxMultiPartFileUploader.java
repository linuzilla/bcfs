package ncu.cc.bcfs.services;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WebFluxMultiPartFileUploader {
    class FileUploadProgress {
        private boolean completed;
        private boolean error;
        private String message;
        private String filename;

        public static FileUploadProgress fromMessage(String message) {
            FileUploadProgress progress = new FileUploadProgress();
            progress.message = message;
            return progress;
        }

        public static FileUploadProgress fromError(String message) {
            FileUploadProgress progress = new FileUploadProgress();
            progress.message = message;
            progress.error = true;
            return progress;
        }

        public static FileUploadProgress completed(String filename) {
            FileUploadProgress progress = new FileUploadProgress();
            progress.filename = filename;
            progress.completed = true;
            return progress;
        }

        public boolean isCompleted() {
            return completed;
        }

        public boolean isError() {
            return error;
        }

        public String getMessage() {
            return message;
        }

        public String getFilename() {
            return filename;
        }
    }

    Mono<String> saveAsFile(String address, FilePart filePart);

    Mono<String> saveFile(String address, FilePart filePart);
}
