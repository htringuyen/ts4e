package io.javaside.ts4e.core.internal;

import io.javaside.treesitter4j.Tree;
import io.javaside.ts4e.core.SyntaxTree;

import java.util.List;

public class SyntaxTreeImpl implements SyntaxTree {

    private final String language;

    private final Tree underlyingTree;

    private final List<SyntaxTree> injectedTrees;

    SyntaxTreeImpl(String language, Tree underlyingTree, List<SyntaxTree> injectedTrees) {
        this.language = language;
        this.underlyingTree = underlyingTree;
        this.injectedTrees = injectedTrees;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public Tree getUnderlyingTree() {
        return underlyingTree;
    }

    @Override
    public List<SyntaxTree> getInjectedTrees() {
        return injectedTrees;
    }
}
