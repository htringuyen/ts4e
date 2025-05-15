package io.javaside.ts4e.core;

import io.javaside.treesitter4j.Point;

public record BufferChange(long modificationStamp,
                           int startByte, int oldEndByte, int newEndByte,
                           Point startPoint, Point oldEndPoint, Point newEndPoint) {

}
