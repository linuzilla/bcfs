package ncu.cc.commons.webdev.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ClassFinderImpl implements ClassFinder {
    private static final Logger logger = LoggerFactory.getLogger(ClassFinderImpl.class);

    public Class<?>[] findClassesByPackage(String packageName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(URLDecoder.decode(resource.getFile(), "latin1")));
        }
        logger.info(dirs.size() + " dirs");
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private static List<Class<?>> findClasses(File directory, String packageName) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            logger.warn("Directory:" + directory.getAbsolutePath() + " not exists");
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            logger.debug(file.getName());
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "."
                        + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    classes.add(Class.forName(packageName
                            + '.'
                            + file.getName().substring(0,
                            file.getName().length() - 6)));
                } catch (ClassNotFoundException e) {
                }
            }
        }
        return classes;
    }

    public boolean isa(Class<?> myclazz, Class<?> clazz) {
        if (myclazz == null) {
            return false;
        } if (myclazz == clazz) {
            return true;
        } else {
            if (isa(myclazz.getSuperclass(), clazz)) {
                return true;
            }
            Class<?>[]	intfs = myclazz.getInterfaces();

            if (intfs != null) {
                for (int i = 0; i < intfs.length; i++) {
                    if (isa (intfs[i], clazz)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
