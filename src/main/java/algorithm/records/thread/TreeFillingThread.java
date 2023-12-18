package algorithm.records.thread;

import algorithm.model.result.OperationResult;
import algorithm.records.record.CompositeRecordParallel;
import algorithm.records.util.EquipmentFinder;
import org.apache.directory.server.core.avltree.AvlTree;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class TreeFillingThread extends Thread {

    private final int index;
    private final List<OperationResult> allOperations;
    private final OperationResult prevRecord;
    private OperationResult newRecord;
    private final CompositeRecordParallel compositeRecordParallel;
    private final LocalDateTime timeTick;
    private final EquipmentFinder equipmentFinder;

    public TreeFillingThread(int index, List<OperationResult> allOperations,
                             OperationResult record, CompositeRecordParallel compositeRecordParallel, LocalDateTime timeTick,
                             EquipmentFinder equipmentFinder) {
        this.index = index;
        this.allOperations = allOperations;
        this.prevRecord = record;
        this.newRecord = null;
        this.compositeRecordParallel = compositeRecordParallel;
        this.timeTick = timeTick;
        this.equipmentFinder = equipmentFinder;
    }

    public OperationResult getPrevRecord() {
        return prevRecord;
    }

    public OperationResult getNewRecord() {
        return newRecord;
    }

    @Override
    public void run() {
        fillTree();
    }

    private void fillTree() {
        AvlTree<OperationResult> tree = compositeRecordParallel.getAvlTrees().get(index);
        allOperations.forEach(operation -> {
            if (Objects.isNull(prevRecord)) {
                if (Objects.isNull(newRecord)) {
                    newRecord = operation;
                } else {
                    if (operation.compareTo(newRecord) < 0
                            && equipmentFinder.findAvailableEquipmentByTimeTick(operation, timeTick)) {
                        tree.insert(newRecord);
                        newRecord = operation;
                    } else {
                        tree.insert(operation);
                    }
                }
            } else {
                if (operation.compareTo(prevRecord) < 0
                        && equipmentFinder.findAvailableEquipmentByTimeTick(operation, timeTick)) {
                    tree.insert(prevRecord);
                    newRecord = operation;
                } else {
                    tree.insert(operation);
                }
            }
        });
    }
}
