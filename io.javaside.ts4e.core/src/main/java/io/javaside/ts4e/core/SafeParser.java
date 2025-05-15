package io.javaside.ts4e.core;

import io.javaside.treesitter4j.Parser;
import io.javaside.treesitter4j.Range;
import io.javaside.treesitter4j.Tree;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public interface SafeParser extends AutoCloseable {

    Parser unwrap();

    Optional<Tree> parse(@NonNull String source, @Nullable Tree oldTree, @Nullable Range includeRange);

    @Override
    void close();

    void cancelParsing();
}
