package ncu.cc.commons.webdev.utils;

import java.io.IOException;

public interface ClassFinder {
    Class<?>[]	findClassesByPackage(String packageName) throws IOException;
    boolean		isa(Class<?> myclazz, Class<?> clazz);
}
