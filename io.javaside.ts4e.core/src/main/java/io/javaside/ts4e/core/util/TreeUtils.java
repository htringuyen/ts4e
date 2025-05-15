package io.javaside.ts4e.core.util;

import io.javaside.treesitter4j.Node;
import io.javaside.treesitter4j.Tree;

public final class TreeUtils {

    public static int countNodes(Node root) {
        if (root == null) {
            return 0;
        }
        var counter = 1;
        for (var childNode : root.getChildren()) {
            counter += countNodes(childNode);
        }
        return counter;
    }

    public static int countNodes(Tree tree) {
        return countNodes(tree.getRootNode());
    }

    public static int countErrors(Node root) {
        if (root == null) {
            return 0;
        }
        var counter = root.hasError() ? 1 : 0;
        for (var childNode : root.getChildren()) {
            counter += countErrors(childNode);
        }
        return counter;
    }

    public static int countErrors(Tree tree) {
        return countErrors(tree.getRootNode());
    }

}
