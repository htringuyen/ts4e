package io.javaside.ts4e.core;

import io.javaside.treesitter4j.Tree;
import io.javaside.ts4e.core.internal.SyntaxTreeBuilderImpl;

import java.util.List;

public interface SyntaxTree {

    Tree getUnderlyingTree();

    String getLanguage();

    List<SyntaxTree> getInjectedTrees();

    static Builder builder() {
        return new SyntaxTreeBuilderImpl();
    }

    interface Builder {
        Builder language(String language);
        Builder underlyingTree(Tree tree);
        Builder addInjectedTree(SyntaxTree syntaxTree);
        SyntaxTree build();
    }
}
