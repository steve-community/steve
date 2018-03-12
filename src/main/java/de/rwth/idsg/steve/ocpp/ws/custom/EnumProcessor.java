package de.rwth.idsg.steve.ocpp.ws.custom;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 12.03.2018
 */
public final class EnumProcessor {

    private EnumProcessor() { }

    public static void apply(List<String> packageNames, Consumer<Class<?>> clazzConsumer) {
        packageNames.forEach(p -> apply(p, clazzConsumer));
    }

    public static void apply(String packageName, Consumer<Class<?>> clazzConsumer) {
        try {
            ImmutableSet<ClassPath.ClassInfo> classInfos =
                    ClassPath.from(Thread.currentThread().getContextClassLoader())
                             .getTopLevelClasses(packageName);

            for (ClassPath.ClassInfo classInfo : classInfos) {
                Class<?> clazz = classInfo.load();
                if (clazz.isEnum()) {
                    clazzConsumer.accept(clazz);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
