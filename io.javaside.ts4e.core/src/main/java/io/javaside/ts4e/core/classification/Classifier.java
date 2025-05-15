package io.javaside.ts4e.core.classification;

import java.util.List;

public interface Classifier {

    List<ClassificationSpan> getClassificationSpans();

    void addClassificationListener(ClassificationListener listener);
}
