package io.javaside.ts4e.core.classification;

import java.util.List;

public interface ClassificationType {

    String getName();

    List<ClassificationType> getBaseTypes();

    boolean isOfType(ClassificationType type);

}
