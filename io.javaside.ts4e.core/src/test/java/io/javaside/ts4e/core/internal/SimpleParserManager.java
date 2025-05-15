package io.javaside.ts4e.core.internal;

import io.javaside.treesitter4j.BuiltinLanguageLibrary;
import io.javaside.treesitter4j.Language;
import io.javaside.treesitter4j.Parser;
import io.javaside.ts4e.core.ParserDescriptor;
import io.javaside.ts4e.core.ParserManager;
import io.javaside.ts4e.core.SafeParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleParserManager implements ParserManager {

    private static final Logger logger = LoggerFactory.getLogger(SimpleParserManager.class);

    private static final Map<ParserDescriptor, SafeParser> parsers = new HashMap<>();

    private static final SimpleParserManager INSTANCE = new SimpleParserManager();

    private SimpleParserManager() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException();
        }
    }

    public static SimpleParserManager getInstance() {
        return INSTANCE;
    }

    @Override
    public SafeParser getParser(ParserDescriptor pDesc) {
        return parsers.computeIfAbsent(pDesc, SimpleParserManager::createParser);
    }

    @Override
    public List<ParserDescriptor> getDescriptorsByLanguage(String language) {
        return switch (language) {
            case "device-tree" -> List.of(new ParserDescriptor(language, BuiltinLanguageLibrary.TS_DEVICETREE, null));
            case "java" -> List.of(new ParserDescriptor(language, BuiltinLanguageLibrary.TS_JAVA, null));
            case "kconfig" -> List.of(new ParserDescriptor(language, BuiltinLanguageLibrary.TS_KCONFIG, null));
            default -> throw new IllegalArgumentException("Unsupported language: " + language);
        };
    }

    private static SafeParser createParser(ParserDescriptor pDesc) {
        try {
            var langLib = BuiltinLanguageLibrary.getLibrary(pDesc.library());
            var language = new Language(langLib.loadLanguage());
            return SafeParserImpl.wrap(new Parser(language));
        }
        catch (Exception e) {
            logger.error("Cannot load builtin library", e);
            return null;
        }
    }
}
