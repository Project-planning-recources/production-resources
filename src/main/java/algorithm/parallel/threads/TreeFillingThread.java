package algorithm.parallel.threads;

import algorithm.model.result.OperationResult;
import org.apache.directory.server.core.avltree.AvlTree;

import java.util.List;

public class TreeFillingThread extends Thread {

    List<OperationResult> allOperations;
    AvlTree<OperationResult> avlTree;

    public TreeFillingThread(List<OperationResult> allOperations, AvlTree<OperationResult> avlTree) {
        this.allOperations = allOperations;
        this.avlTree = avlTree;
    }

    public AvlTree<OperationResult> getAvlTree() {
        return avlTree;
    }

    @Override
    public void run() {
        for (OperationResult operation : allOperations) {
            avlTree.insert(operation);
        }
    }


}
