package io.javaside.ts4e.core;

import io.javaside.ts4e.core.internal.InMemoryTextBuffer;
import io.javaside.ts4e.core.old.TSDocumentImpl;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

final class ClasspathDocumentLoader {

    private final Map<DocumentDescriptor, TSDocument> documents = new HashMap<>();

    TSDocument loadDocument(DocumentDescriptor descriptor) {

    }

    TSDocument registerDocumentFromClassPath(String pathStr, String projectId,
                                                    String documentName, String language) {

        var descriptor = new DocumentDescriptor(projectId, documentName, language);
        if (documents.containsKey(descriptor)) {
            return documents.get(descriptor);
        }
        try {
            var uri = ClasspathDocumentLoader.class.getResource(pathStr).toURI();
            var path = Paths.get(uri);
            var buffer = new InMemoryTextBuffer(path);
            var document = new TSDocumentImpl(descriptor, buffer);
            documents.put(descriptor, document);
            return document;
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("Cannot load file data from classpath " + pathStr);
        }
    }

}
