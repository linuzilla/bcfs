package ncu.cc.iota.impl;

import ncu.cc.iota.api.IotaStoreBackend;
import ncu.cc.iota.utils.TrytesUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.Null;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FileStoreBackendImpl implements IotaStoreBackend {
    private static final Logger logger = LoggerFactory.getLogger(FileStoreBackendImpl.class);
    private final String basedir;

    public FileStoreBackendImpl(String basedir) {
        this.basedir = basedir;

        new File(basedir).mkdirs();

        logger.info("File Store Backend: basedir at {}", basedir);
    }

    @Override
    public List<String> find(String... bundles) {
        return Arrays.asList(bundles).stream()
                .map(s -> {
                    try {
                        File f = new File(FilenameUtils.concat(this.basedir, s));

                        if (f.isFile() && f.canRead()) {
                            return IOUtils.toString(new FileInputStream(f), Charset.defaultCharset());
                        }
                    } catch (Exception e) {
                    }
                    return "";
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<String> save(@Null String address, String message) {
        String bundle = TrytesUtil.randomBundle();
        File f = new File(FilenameUtils.concat(this.basedir, bundle));
        try {
            OutputStream os = new FileOutputStream(f);
            os.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Arrays.asList(bundle);
    }
}
