package io.javaside.ts4e.core.internal;

import io.javaside.treesitter4j.Tree;
import io.javaside.ts4e.core.SyntaxTree;

import java.util.ArrayList;
import java.util.List;

public class SyntaxTreeBuilderImpl implements SyntaxTree.Builder{

    private String language;

    private Tree underlyingTree;

    private List<SyntaxTree> injectedSyntaxTrees = new ArrayList<>();

    @Override
    public SyntaxTree.Builder language(String language) {
        this.language = language;
        return this;
    }

    @Override
    public SyntaxTree.Builder underlyingTree(Tree tree) {
        this.underlyingTree = tree;
        return this;
    }

    @Override
    public SyntaxTree.Builder addInjectedTree(SyntaxTree tree) {
        this.injectedSyntaxTrees.add(tree);
        return this;
    }

    @Override
    public SyntaxTree build() {
        return new SyntaxTreeImpl(language, underlyingTree, injectedSyntaxTrees);
    }



}
