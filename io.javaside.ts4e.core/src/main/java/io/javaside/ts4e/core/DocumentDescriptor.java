package io.javaside.ts4e.core;

public record DocumentDescriptor(String project, String name,
                                 String mainLanguage, boolean shouldIncludeInjectedLanguages) {

}
