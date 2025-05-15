package io.javaside.ts4e.core.internal;

import io.javaside.treesitter4j.Point;
import io.javaside.ts4e.core.BufferChange;
import io.javaside.ts4e.core.BufferChangeConsumer;
import io.javaside.ts4e.core.SerialScheduler;
import io.javaside.ts4e.core.TextBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InMemoryTextBuffer implements TextBuffer {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryTextBuffer.class);

    private int[] indexedOffsets;

    private long modificationStamp;

    private String text;

    private final List<BufferChangeConsumer> consumers = new LinkedList<>();

    public InMemoryTextBuffer(Path path) {
        try {
            setText(Files.readString(path));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InMemoryTextBuffer(String text) {
        setText(text);
    }

    public void setText(String newText) {
        var lines = newText.split("\\R");
        var oldText = text;
        this.text = newText;
        this.modificationStamp = Instant.now().toEpochMilli();
        this.indexedOffsets = buildIndex(lines);
        var change = detachTextChange(oldText, newText);
        consumers.forEach(c -> c.accept(change));
    }

    private static int[] buildIndex(String[] lines) {
        var indexes = new int[lines.length];
        var offset = 0;
        for (var i = 0; i < lines.length; i++) {
            indexes[i] = offset;
            offset += lines[i].length() + System.lineSeparator().length(); // plus offset for each stripped newline
        }
        return indexes;
    }

    public int[] getIndexedOffsets() {
        return indexedOffsets;
    }

    @Override
    public String getText(int offset, Point point) {
        try {
            var endOffset = indexedOffsets[point.row()] + point.column();
            return endOffset < offset ? "" : text.substring(offset, endOffset + 1);
        }
        catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public long getModificationStamp() {
        return modificationStamp;
    }

    public void addChangeConsumer(BufferChangeConsumer consumer) {
        consumers.add(consumer);
    }

    private BufferChange detachTextChange(String oldText, String newText) {

        if (oldText == null || newText == null) {
            return null;
        }

        var commonPrefix = 0;
        while (commonPrefix < oldText.length() && commonPrefix < newText.length()
                && oldText.charAt(commonPrefix) == newText.charAt(commonPrefix)) {
            commonPrefix++;
        }

        var oldSuffix = oldText.length();
        var newSuffix = newText.length();
        while (oldSuffix > commonPrefix && newSuffix > commonPrefix
                && oldText.charAt(oldSuffix - 1) == newText.charAt(newSuffix -1)) {
            oldSuffix--;
            newSuffix--;
        }

        return new BufferChange(Instant.now().toEpochMilli(), commonPrefix, oldSuffix, newSuffix,
                calculcatePoint(commonPrefix), calculcatePoint(oldSuffix), calculcatePoint(newSuffix));
    }

    private Point calculcatePoint(int offset) {
        var row = Arrays.binarySearch(indexedOffsets, offset);
        row = row < 0 ? - row - 2 : row;
        var column = offset - indexedOffsets[row];
        return new Point(row, column);
    }

    public SerialScheduler getChangeScheduler() {
        return null;
    }
}
























