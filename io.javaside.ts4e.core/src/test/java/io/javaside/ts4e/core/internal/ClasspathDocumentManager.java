package io.javaside.ts4e.core.internal;

import io.javaside.ts4e.core.DocumentDescriptor;
import io.javaside.ts4e.core.DocumentManager;
import io.javaside.ts4e.core.ParserManager;
import io.javaside.ts4e.core.TSDocument;

public class ClasspathDocumentManager implements DocumentManager {

    private ParserManager parserManager = SimpleParserManager.getInstance();

    public TSDocument getDocument(DocumentDescriptor descriptor) {
        return null;
    }

    public TSDocument loadDocumentFromClasspath(String pathStr) {
        return new TSDocumentImpl();
    }
}
