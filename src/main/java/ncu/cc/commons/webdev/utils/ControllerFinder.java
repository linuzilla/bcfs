package ncu.cc.commons.webdev.utils;

import java.lang.reflect.Method;

public interface ControllerFinder {
    @FunctionalInterface
    interface Callback {
        void doCallback(String path, Method method);
    }
    void componentScan(String packageName, Callback callback);
}
