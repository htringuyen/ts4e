package io.javaside.ts4e.core;

import io.javaside.treesitter4j.*;
import io.javaside.ts4e.core.util.MiscUtils;
import io.javaside.ts4e.core.util.TreeUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class UpdateTreeTest {

    private static final Logger logger = LoggerFactory.getLogger(UpdateTreeTest.class);

    @Test
    void testUpdateSyntaxTree() {

        var lib = BuiltinLanguageLibrary.getLibrary(BuiltinLanguageLibrary.TS_DEVICETREE);
        var language = new Language(lib.loadLanguage());
        try (var parser = new Parser(language)) {

            var source = MiscUtils.readTextFromClasspath("/sample.dts");
            var tree = parser.parse(source).orElseThrow();

            //logger.info("\n" + tree.getText());

            var inputEdit = new InputEdit(88, 123, 96,
                    new Point(1, 3), new Point(1, 38), new Point(1, 14));

            var editedSource = MiscUtils.readTextFromClasspath("/edited-sample.dts.txt");
            tree.edit(inputEdit);
            var hitCountCallback = new HitCountParserCallback(editedSource);

            var parseTimes = 10_000;
            logger.info("Start parsing {} times", parseTimes);
            var startTime = Instant.now().toEpochMilli();
            Tree editedTree = null;
            for (int i = 0; i < parseTimes; i++) {
                editedTree = parser.parse(editedSource, tree).orElseThrow();
            }
            logger.info("Parsing completed after {} ms", Instant.now().toEpochMilli() - startTime);

            //logger.info("\n" + editedTree.getText());

            logger.info("Tree edited, buffer hit count: {}", hitCountCallback.getHitCount());

            logger.info("old_node_count={}, new_node_count={}",
                    TreeUtils.countNodes(tree), TreeUtils.countNodes(editedTree));

            logger.info("old_error_count={}, new_error_count={}",
                    TreeUtils.countErrors(tree), TreeUtils.countErrors(editedTree));
        }
    }

    @Test
    void testGetNodePosition() {
        var lib = BuiltinLanguageLibrary.getLibrary(BuiltinLanguageLibrary.TS_DEVICETREE);
        var language = new Language(lib.loadLanguage());
        try (var parser = new Parser(language)) {
            var source = MiscUtils.readTextFromClasspath("/sample.dts");
            var tree = parser.parse(source).orElseThrow();
            for (var i = 0; i < tree.getRootNode().getChildren().size(); i++) {
                var childNode = tree.getRootNode().getChild(i).orElseThrow();
                logger.info("Node {} text: {}", i, childNode.getText());
                logger.info("start_bytes={}, end_bytes={}, start_point={}, end_point={}",
                        childNode.getStartByte(), childNode.getEndByte(),
                        childNode.getStartPoint(), childNode.getEndPoint());
            }
        }
    }

    @Test
    void testRemoveFirstNode() {
        var lib = BuiltinLanguageLibrary.getLibrary(BuiltinLanguageLibrary.TS_DEVICETREE);
        var language = new Language(lib.loadLanguage());
        try (var parser = new Parser(language)) {
            var source = MiscUtils.readTextFromClasspath("/sample.dts");
            var tree = parser.parse(source).orElseThrow();

            var firstNode = tree.getRootNode().getChild(0).orElseThrow();

            var inputEdit = new InputEdit(firstNode.getStartByte(), firstNode.getEndByte(), 0,
                    firstNode.getStartPoint(), firstNode.getEndPoint(), new Point(0, 0));
            tree.edit(inputEdit);
            var editedSource = MiscUtils.readTextFromClasspath("/edited-sample2.dts.txt");

            var parseCallback = new HitCountParserCallback(editedSource);

            var parseTimes = 1;
            logger.info("Start parsing {} times", parseTimes);
            var startTime = Instant.now().toEpochMilli();
            Tree editedTree = null;
            for (int i = 0; i < parseTimes; i++) {
                //editedTree = parser.parse(editedSource, tree).orElseThrow();
                //editedTree = parser.parse(parseCallback, InputEncoding.UTF_8, tree, null).orElseThrow();
                editedTree = parser.parse(parseCallback, InputEncoding.UTF_8).orElseThrow();
            }
            logger.info("Parsing completed after {} ms", Instant.now().toEpochMilli() - startTime);
            logger.info("Edited tree:\n{}", editedTree.getRootNode().getText());
            logger.info("New node count: {}", TreeUtils.countNodes(editedTree));
        }
    }
}











