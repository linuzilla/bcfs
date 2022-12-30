package ncu.cc.bcfs.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WebFluxMultiPartFileUploaderImpl implements WebFluxMultiPartFileUploader {
    private final static Logger logger = LoggerFactory.getLogger(WebFluxMultiPartFileUploaderImpl.class);

    @Override
    public Mono<String> saveAsFile(String address, FilePart filePart) {
        logger.info("upload file {}", filePart.filename());
        try {
            final File tempFile = File.createTempFile("bcfs-", ".tmp");
            return filePart.transferTo(tempFile)
                    .flatMap(aVoid -> {
                        logger.info("file {}", tempFile.getAbsolutePath());
                        return Mono.just(tempFile.getAbsolutePath());
                    });
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return Mono.empty();
        }
    }

    @Override
    public Mono<String> saveFile(String address, FilePart filePart) {
        logger.info("upload file {}", filePart.filename());

        try {
            File tempFile = File.createTempFile("bcfs-", ".tmp");

            final String filename = tempFile.getAbsolutePath();

            logger.info("write file to {}", filename);

            final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(tempFile.toPath(), StandardOpenOption.WRITE);
            // pointer to the end of file offset
            final AtomicInteger fileWriteOffset = new AtomicInteger(0);
            final AtomicBoolean errorFlag = new AtomicBoolean(false);
            final CloseCondition closeCondition = new CloseCondition();

            return filePart.content()
                    .doOnEach(dataBufferSignal -> {
                        DataBuffer dataBuffer = dataBufferSignal.get();

                        if (dataBuffer != null && dataBuffer.readableByteCount() > 0) {
                            int count = dataBuffer.readableByteCount();
                            byte[] bytes = new byte[count];
                            dataBuffer.read(bytes);

                            final ByteBuffer byteBuffer = ByteBuffer.allocate(count);
                            byteBuffer.put(bytes);
                            byteBuffer.flip();

                            // get the current write offset and increment by the buffer size
                            final int filePartOffset = fileWriteOffset.getAndAdd(count);
                            logger.info("processing file part at offset {}", filePartOffset);

                            fileChannel.write(byteBuffer, filePartOffset, null, new CompletionHandler<Integer, ByteBuffer>() {
                                @Override
                                public void completed(Integer result, ByteBuffer attachment) {
                                    // file part successfuly written to disk, clean up
                                    logger.info("done saving file part {}", filePartOffset);
                                    byteBuffer.clear();

                                    if (closeCondition.onTaskCompleted())
                                        try {
                                            logger.info("closing after last part");
                                            fileChannel.close();
                                        } catch (IOException ignored) {
                                            ignored.printStackTrace();
                                        }
                                }

                                @Override
                                public void failed(Throwable exc, ByteBuffer attachment) {
                                    // there as an error while writing to disk, set an error flag
                                    errorFlag.set(true);
                                    logger.info("error saving file part {}", filePartOffset);
                                }
                            });
                        }
                    })
                    .doOnComplete(() -> {
                        // all done, close the file channel
                        logger.info("done processing file parts");
                        closeCondition.setCompleted(true);
                        if (closeCondition.canCloseOnComplete()) {
                            try {
                                logger.info("closing after complete");
                                fileChannel.close();
                            } catch (IOException ignored) {
                            }
                        }
                    })
                    .doOnError(t -> {
                        // ooops there was an error
                        logger.info("error processing file parts");
                        try {
                            fileChannel.close();
                        } catch (IOException ignored) {
                        }
                        // take last, map to a status string
                    })
                    .then(Mono.just(filename));
//                    .last()
//                    .map(dataBuffer -> filename);
        } catch (IOException e) {
            logger.warn(e.getMessage());
            return Mono.empty();
        }
    }

    public static class CloseCondition {
        AtomicInteger tasksSubmitted = new AtomicInteger(0);
        AtomicInteger tasksCompleted = new AtomicInteger(0);
        AtomicBoolean allTaskssubmitted = new AtomicBoolean(false);
        AtomicBoolean completed = new AtomicBoolean(false);

        /**
         * notify all tasks have been subitted, determine of the file channel can be closed
         *
         * @return true if the asynchronous file stream can be closed
         */
        public boolean canCloseOnComplete() {
            allTaskssubmitted.set(true);
            return tasksCompleted.get() == tasksSubmitted.get();
        }

        /**
         * notify a task has been submitted
         */
        public void onTaskSubmitted() {
            tasksSubmitted.incrementAndGet();
        }

        /**
         * notify a task has been completed
         *
         * @return true if the asynchronous file stream can be closed
         */
        public boolean onTaskCompleted() {
            boolean allSubmittedClosed = tasksSubmitted.get() == tasksCompleted.incrementAndGet();
            return allSubmittedClosed && allTaskssubmitted.get();
        }

        public void setCompleted(boolean completed) {
            this.completed.set(completed);
        }

        public boolean isCompleted() {
            return this.completed.get();
        }
    }
}
