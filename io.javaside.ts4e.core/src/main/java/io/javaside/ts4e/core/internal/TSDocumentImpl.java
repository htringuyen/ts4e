package io.javaside.ts4e.core.internal;

import io.javaside.treesitter4j.*;
import io.javaside.ts4e.core.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TSDocumentImpl implements TSDocument {

    private final DocumentDescriptor docDesc;

    private final ParserManager parserManager;

    private final TextBuffer buffer;

    private final CheckpointManager checkpointManager;

    private SyntaxTree syntaxTree;

    private final List<TreeUpdateStrategy> treeStrategies = Collections.synchronizedList(new ArrayList<>());

    private final List<TreeUpdateListener> treeListeners = Collections.synchronizedList(new ArrayList<>());

    private final Queue<BufferChange> pendingChanges = new ConcurrentLinkedQueue<>();

    private final InjectionParser injectionParser = new InjectionParser();

    public TSDocumentImpl(DocumentDescriptor docDesc, ParserManager parserManager,
                          TextBuffer buffer, CheckpointManager checkpointManager) {
        this.docDesc = docDesc;
        this.parserManager = parserManager;
        this.buffer = buffer;
        this.checkpointManager = checkpointManager;
        buffer.addChangeConsumer(this::onBufferChanged);
    }

    private void setSyntaxTree(SyntaxTree syntaxTree) {
        this.syntaxTree = syntaxTree;
    }

    private DocumentCheckpoint createCheckpoint() {
        return checkpointManager.newCheckpointFor(this);
    }

    private SyntaxTree parseDocument() {
        return injectionParser.parse();
    }

    @Override
    public TextBuffer getTextBuffer() {
        return buffer;
    }

    @Override
    public String getText() {
        return buffer.getText();
    }

    @Override
    public String getText(int offset, Point point) {
        return buffer.getText(offset, point);
    }

    @Override
    public DocumentDescriptor getDescriptor() {
        return docDesc;
    }

    @Override
    public TSDocument addTreeUpdateStrategy(TreeUpdateStrategy strategy) {
        treeStrategies.add(strategy);
        return this;
    }

    @Override
    public TSDocument addTreeUpdateListener(TreeUpdateListener listener) {
        createCheckpoint().executeAsync(() -> syntaxTree == null ? this.parseDocument() : syntaxTree)
                .thenConsumeSerially(v -> addTreeListener(listener, v));
        treeListeners.add(listener);
        return this;
    }

    private void addTreeListener(TreeUpdateListener listener, SyntaxTree initValue) {
        treeListeners.add(listener);
        listener.initialize(initValue);
    }

    private void onBufferChanged(BufferChange change) {
        var handler = new BufferChangeHandler();
        createCheckpoint().execute(handler);
    }

    final class BufferChangeHandler implements Runnable {

        private final AtomicBoolean updatePerformed = new AtomicBoolean(false);

        private final DocumentCheckpoint checkpoint = DocumentCheckpoint.boundCheckpoint();

        @Override
        public void run() {
            for (var stg : treeStrategies) {
                checkpoint.executeAsync(() -> stg.shouldUpdate(pendingChanges))
                        .thenConsume(this::processStrategyResult);
            }
        }

        private void processStrategyResult(boolean shouldUpdate) {
            if (!shouldUpdate) {
                return;
            }
            if (!updatePerformed.getAndSet(true)) {
                checkpoint.executeAsync(TSDocumentImpl.this::parseDocument)
                        .thenConsumeSerially(this::onParsingCompleted);
            }
        }

        private void onParsingCompleted(SyntaxTree newTree) {
            var oldTree = syntaxTree;
            syntaxTree = newTree;
            treeListeners.forEach(l -> {
                checkpoint.executeAsync(() -> l.treeUpdated(oldTree, newTree));
            });
        }
    }

    final class InjectionParser {

        private final HashMap<String, ParserDescriptor> pDescList = loadParserDescriptors();

        // ignore language injection at current implementation
        private final Query injectionQuery = null;

        private SyntaxTree parse() {

            var tree = syntaxTree.getUnderlyingTree();

            var changeCount = pendingChanges.size();

            for (var i = 0; i < changeCount; i++) {
                var c = pendingChanges.poll();
                var inputEdit = new InputEdit(c.startByte(), c.oldEndByte(), c.newEndByte(),
                        c.startPoint(), c.newEndPoint(), c.oldEndPoint());
                tree.edit(inputEdit);
            }
            return parse(docDesc.mainLanguage(), null, tree);
        }

        private SyntaxTree parse(String language, Range range, Tree editedTree) {
            var checkpoint = DocumentCheckpoint.boundCheckpoint();
            var result = SyntaxTree.builder();
            result.language(language);

            var parsingTask = new ParsingTask(parserManager,
                    pDescList.get(language), buffer.getText(), range,editedTree);

            Tree tree = parsingTask.call();
            if (!checkpoint.isValid()) {
                return null;
            }

            result.underlyingTree(tree);
            for (var e : findInjectionsIn(tree).entrySet()) {
                result.addInjectedTree(
                        parse(e.getKey(), e.getValue().getRange(), null));
            }
            return result.build();
        }


        // ignore language injection in current implementation
        private Map<String, Node> findInjectionsIn(Tree tree) {
            return Map.of();
        }

        // ignore language injection at current implementation
        private HashMap<String, ParserDescriptor> loadParserDescriptors() {
            var result = new HashMap<String, ParserDescriptor>();
            result.put(docDesc.mainLanguage(),
                    parserManager.getDescriptorsByLanguage(docDesc.mainLanguage()).getFirst());
            return result;
        }
    }


    static final class ParsingTask implements Callable<Tree> {

        private final ParserManager parserManager;

        private final ParserDescriptor parserDesc;

        private final Range includeRange;

        private final String text;

        private final Tree editedTree;

        ParsingTask(ParserManager parserManager, ParserDescriptor parserDesc,
                    String text, Range includeRange, Tree editedTree) {
            this.parserManager = parserManager;
            this.parserDesc = parserDesc;
            this.includeRange = includeRange;
            this.text = text;
            this.editedTree = editedTree;
        }

        @Override
        public Tree call() {
            var checkpoint = DocumentCheckpoint.boundCheckpoint();
            try (var parser = parserManager.getParser(parserDesc)) {
                checkpoint.addInvalidationCallback(parser::cancelParsing);
                return parser.parse(text, editedTree, includeRange).orElse(null);
            }
        }
    }
}
























