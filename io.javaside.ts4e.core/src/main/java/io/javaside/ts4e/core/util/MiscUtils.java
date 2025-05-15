package io.javaside.ts4e.core.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class MiscUtils {

    public static String readTextFromClasspath(String classpath) {
        try {
            var uri = MiscUtils.class.getResource(classpath).toURI();
            var path = Paths.get(uri);
            return Files.readString(path);
        }
        catch (URISyntaxException | IOException e) {
            throw new IllegalArgumentException();
        }
    }
}
