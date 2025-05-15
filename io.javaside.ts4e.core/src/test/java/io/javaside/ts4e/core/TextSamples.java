package io.javaside.ts4e.core;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TextSamples {

    public static String dtsSource;

    public static Path dtsPath;

    static {
        configDtsSample();
    }

    private static void configDtsSample() {
        try {
            var uri = TextSamples.class.getResource("/sample.dts").toURI();
            dtsPath = Paths.get(uri);
            dtsSource = Files.readString(dtsPath);
        }
        catch (IOException | URISyntaxException e) {
            throw new RuntimeException();
        }
    }
}
