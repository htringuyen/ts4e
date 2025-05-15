package io.javaside.ts4e.core;

import io.javaside.treesitter4j.Point;

import io.javaside.ts4e.core.internal.TSDocumentImpl;

public sealed interface TSDocument permits TSDocumentImpl {

    TextBuffer getTextBuffer();

    DocumentDescriptor getDescriptor();

    TSDocument addTreeUpdateListener(TreeUpdateListener listener);

    TSDocument addTreeUpdateStrategy(TreeUpdateStrategy strategy);

    String getText();

    String getText(int offset, Point point);
}
