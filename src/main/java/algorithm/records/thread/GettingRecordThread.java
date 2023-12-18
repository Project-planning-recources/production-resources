package algorithm.records.thread;

import algorithm.model.result.OperationResult;
import org.apache.directory.server.core.avltree.AvlTree;
import org.apache.directory.server.core.avltree.LinkedAvlNode;

import java.util.Objects;

public class GettingRecordThread extends Thread {

    private OperationResult prevRecord;
    private OperationResult newRecord;
    private final AvlTree<OperationResult> operations;

    public GettingRecordThread(AvlTree<OperationResult> operations, OperationResult prevRecord) {
        this.operations = operations;
        this.prevRecord = prevRecord;
    }

    public OperationResult getPrevRecord() {
        return prevRecord;
    }

    public OperationResult getNewRecord() {
        return newRecord;
    }

    @Override
    public void run() {
        LinkedAvlNode<OperationResult> operationNode = operations.getFirst();
        if (Objects.nonNull(operationNode)) {
            OperationResult value = operationNode.getKey();
            if (Objects.isNull(prevRecord)) {
                newRecord = value;
            } else {
                newRecord = value.compareTo(prevRecord) < 0 ? value : prevRecord;
            }
        } else {
            if (Objects.nonNull(prevRecord)) {
                newRecord = prevRecord;
            }
        }
    }
}
