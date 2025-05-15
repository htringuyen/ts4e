package io.javaside.ts4e.core;

import java.util.List;

public interface ParserManager {

    SafeParser getParser(ParserDescriptor pDesc);

    List<ParserDescriptor> getDescriptorsByLanguage(String language);
}
