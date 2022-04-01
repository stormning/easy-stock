package com.slyak.es.util;

import com.google.common.collect.Lists;
import com.slyak.es.config.WebMvcConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ConfigUtils {
    @SneakyThrows
    public static List<String> getSourceCodeLocations() {
        List<String> locations = Lists.newArrayList();
        String mainCodeLocation = getSourceCodeLocation(Class.forName(getMainClass()));
        if (mainCodeLocation != null) {
            System.out.println("main code location is : " + mainCodeLocation);
            locations.add(mainCodeLocation);
        }
        String coreCodeLocation = getSourceCodeLocation(ConfigUtils.class);
        if (coreCodeLocation != null) {
            System.out.println("core code location is : " + coreCodeLocation);
            if (!locations.contains(coreCodeLocation)) {
                locations.add(coreCodeLocation);
            }
        }
        return locations;
    }

    @SneakyThrows
    private static String getSourceCodeLocation(Class classToCheck) {
        String classesPath = classToCheck.getProtectionDomain().getCodeSource().getLocation().toString();
        int index = classesPath.indexOf("/target/classes");
        if (index > 0) {
            return classesPath.substring(0, index);
        }
        return null;
    }


    private static String getMainClass() {
        StackTraceElement trace[] = Thread.currentThread().getStackTrace();
        for (int i = trace.length - 1; i >= 0; i--) {
            StackTraceElement ste = trace[i];
            if (ste.getMethodName().equals("main")) {
                return ste.getClassName();
            }
        }
        throw new NoSuchMethodError("main class not found!");
    }

    public static void main(String[] args) {
        System.out.println(getSourceCodeLocation(WebMvcConfig.class));
    }
}
