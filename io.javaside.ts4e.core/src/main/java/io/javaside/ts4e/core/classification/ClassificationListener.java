package io.javaside.ts4e.core.classification;

import java.util.List;

public interface ClassificationListener {

    void initialize(List<ClassificationSpan> initSpans);

    void classificationUpdated(List<ClassificationSpan> updatedSpans);
}
