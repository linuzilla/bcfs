package ncu.cc.commons.utils;

import javafx.util.Pair;
import org.springframework.http.MediaType;

public class ContentTypeUtil {

    private static final Pair<String,MediaType>[] mediaMappings = new Pair[] {
        new Pair(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_JPEG),
        new Pair(MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_GIF),
        new Pair(MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_PNG)
    };

    private static final Pair<String,MediaType>[] extensions = new Pair[] {
        new Pair("jpeg", MediaType.IMAGE_JPEG),
        new Pair("gif", MediaType.IMAGE_GIF),
        new Pair("png", MediaType.IMAGE_PNG)
    };

    public static MediaType toMediaType(String contentType) {
        for (Pair<String,MediaType> item: mediaMappings) {
            if (contentType.equalsIgnoreCase(item.getKey())) {
                return item.getValue();
            }
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    public static MediaType fromExtension(String ext) {
        for (Pair<String,MediaType> item: extensions) {
            if (ext.equalsIgnoreCase(item.getKey())) {
                return item.getValue();
            }
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
