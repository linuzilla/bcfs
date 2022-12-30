package ncu.cc.commons.webdev.utils;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.lang.reflect.Method;

public class ControllerFinderImpl implements ControllerFinder {
    private ClassFinder classFinder;

    public ControllerFinderImpl(ClassFinder classFinder) {
        this.classFinder = classFinder;
    }

    private String[] findRequestByAnnotations(Method method) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            return method.getAnnotation(RequestMapping.class).value();
        } else if (method.isAnnotationPresent(GetMapping.class)) {
                return method.getAnnotation(GetMapping.class).value();
        } else {
            return null;
        }
    }

    private void findByMethod(Method method, String parentUrl, Callback callback) {
        String[] paths = findRequestByAnnotations(method);

        if (paths != null && paths.length > 0) {
            for (String path: paths) {
                callback.doCallback(parentUrl + path, method);
                // StackTraceUtil.print1(parentUrl + path + " [" + method.getDeclaringClass().getName() + " : " + method.getName() + "]");
            }
        } else if (parentUrl.length() > 0) {
            callback.doCallback(parentUrl, method);
        }
    }

    private void findByClass(Class<?> clazz, Callback callback) {
        String[]	urls;

        if (clazz.isAnnotationPresent(Controller.class)) {
            urls = null;
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                urls = clazz.getAnnotation(RequestMapping.class).value();
            }

            for (Method method: clazz.getDeclaredMethods()) {
                if (urls == null || urls.length == 0) {
                    findByMethod(method, "", callback);
                } else {
                    for (String url: urls) {
                        findByMethod(method, url, callback);
                    }
                }
            }

        }
    }

    @Override
    public void componentScan(String packageName, Callback callback) {
        try {
            Class<?>[] classes = classFinder.findClassesByPackage(packageName);

            for (Class<?> clazz: classes) {
                findByClass(clazz, callback);
            }
        } catch (IOException e) {
        }
    }
}
