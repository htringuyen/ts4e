package io.javaside.ts4e.core;

public interface TreeUpdateListener {

    void initialize(SyntaxTree tree);

    void treeUpdated(SyntaxTree oldTree, SyntaxTree newTree);
}
