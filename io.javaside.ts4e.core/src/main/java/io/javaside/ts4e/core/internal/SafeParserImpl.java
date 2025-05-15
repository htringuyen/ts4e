package io.javaside.ts4e.core.internal;

import io.javaside.treesitter4j.Parser;
import io.javaside.treesitter4j.Range;
import io.javaside.treesitter4j.Tree;
import io.javaside.ts4e.core.SafeParser;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class SafeParserImpl implements SafeParser {

    private final Parser parser;

    private SafeParserImpl(Parser parser) {
        this.parser = parser;
    }

    @Override
    public Parser unwrap() {
        return parser;
    }

    @Override
    public Optional<Tree> parse(@NonNull String source, @Nullable Tree oldTree, @Nullable Range includeRange) {
        if (includeRange != null) {
            parser.setIncludedRanges(List.of(includeRange));
        }
        return parser.parse(source, oldTree);
    }

    @Override
    public void cancelParsing() {
        var flag = new Parser.CancellationFlag();
        flag.set(Instant.now().toEpochMilli());
        parser.setCancellationFlag(flag);
    }

    @Override
    public void close() {

    }

    public static SafeParser wrap(Parser parser) {
        return new SafeParserImpl(parser);
    }


}
