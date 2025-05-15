package io.javaside.ts4e.core.classification;

import io.javaside.ts4e.core.TSDocument;

import java.util.List;

public interface Classifier {

    void connect(TSDocument document);

    void addClassificationListener(ClassificationListener listener);
}
