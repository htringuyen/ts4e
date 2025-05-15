package io.javaside.ts4e.core;

import io.javaside.treesitter4j.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TrackingTSListener implements TSListener {

    private static final Logger logger = LoggerFactory.getLogger(TrackingTSListener.class);

    private Tree initTree;

    private boolean gotInitTree = false;

    private final List<Tree> updateTrees = new LinkedList<>();

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition initializedCond = lock.newCondition();

    @Override
    public void initialized(Tree syntaxTree) {
        try {
            lock.lock();
            initTree = syntaxTree;
            gotInitTree = true;
            initializedCond.signalAll();
        }
        finally {
            lock.unlock();
        }
    }

    @Override
    public void syntaxTreeUpdated(Tree oldTree, Tree newTree) {
        updateTrees.add(newTree);
    }

    public Tree awaitGetInitTree() {
        try {
            lock.lock();
            if (!gotInitTree) {
                initializedCond.await();
            }
        }
        catch (InterruptedException e) {
            logger.error("Error waiting for listener initialized", e);
        }
        finally {
            lock.unlock();
        }
        return initTree;
    }

    public List<Tree> getUpdateTrees() {
        return updateTrees;
    }
}
