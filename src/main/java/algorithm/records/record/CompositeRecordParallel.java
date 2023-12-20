package algorithm.records.record;

import algorithm.model.production.EquipmentGroup;
import algorithm.model.result.OperationResult;
import algorithm.records.thread.GettingRecordThread;
import algorithm.records.thread.TreeFillingThread;
import algorithm.records.util.EquipmentFinder;
import algorithm.records.util.WaitingCounter;
import org.apache.directory.server.core.avltree.AvlTree;
import org.apache.directory.server.core.avltree.AvlTreeImpl;
import org.apache.directory.server.core.avltree.LinkedAvlNode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompositeRecordParallel implements Record {

    private final List<AvlTree<OperationResult>> avlTrees;
    private AvlTree<OperationResult> records;
    private final EquipmentFinder equipmentFinder;

    public CompositeRecordParallel(List<EquipmentGroup> groups, List<AvlTree<OperationResult>> avlTrees) {
        this.avlTrees = avlTrees;
        Map<Long, EquipmentGroup> equipmentGroups = groups.stream()
                .collect(Collectors.toMap(EquipmentGroup::getId, Function.identity()));
        this.records = new AvlTreeImpl<>(OperationResult::compareTo);
        this.equipmentFinder = new EquipmentFinder(equipmentGroups);
    }

    @Override
    public OperationResult getRecord(LocalDateTime timeTick) {
        OperationResult result = equipmentFinder.findAvailableEquipmentByTimeTick(records, timeTick);
        if (Objects.nonNull(result)) {
            int size = records.getSize();
            records.remove(result);
            if (size == records.getSize()) {
                List<OperationResult> operationResults = records.getKeys();
                operationResults.remove(result);
                badFillRecordTree(operationResults);
            }
        } else {
            fillRecords();
        }
        return result;
    }

    public List<AvlTree<OperationResult>> getAvlTrees() {
        return avlTrees;
    }

    public void fillTrees(List<OperationResult> readyOperations, LocalDateTime timeTick) {
        if (Objects.isNull(readyOperations) || readyOperations.isEmpty()) {
            return;
        }
        List<TreeFillingThread> runnableThread = new ArrayList<>();
        int avlTreeSize = avlTrees.size();
        LinkedAvlNode<OperationResult> recordNode = records.getFirst();
        OperationResult record = Objects.nonNull(recordNode) ? recordNode.getKey() : null;
        for (int i = 0; i < avlTreeSize; i++) {
            int start = i == 0 ? 0 : (readyOperations.size() * i) / avlTreeSize;
            int end = i == avlTreeSize - 1 ? readyOperations.size() : (readyOperations.size() * (i + 1)) / avlTreeSize;
            TreeFillingThread thread = new TreeFillingThread(
                    i, readyOperations.subList(start, end), record, this, timeTick, equipmentFinder);
            if (Objects.nonNull(record)) {
                recordNode = records.findGreater(record);
                record = Objects.nonNull(recordNode) ? recordNode.getKey() : null;
            }
            runnableThread.add(thread);
            thread.start();
        }
        WaitingCounter.waitingCounter(runnableThread);
        runnableThread.forEach(thread -> {
            OperationResult newRecord = thread.getNewRecord();
            OperationResult prevRecord = thread.getPrevRecord();
            if (Objects.nonNull(newRecord)) {
                if (Objects.nonNull(prevRecord)) {
                    int size = records.getSize();
                    records.remove(prevRecord);
                    if (size == records.getSize()) {
                        List<OperationResult> operationResults = records.getKeys();
                        operationResults.remove(prevRecord);
                        badFillRecordTree(operationResults);
                    }
                }
                records.insert(newRecord);
            }
        });
    }

    public void fillRecords() {
        List<GettingRecordThread> runnableThread = new ArrayList<>();
        LinkedAvlNode<OperationResult> recordNode = records.getFirst();
        OperationResult record = Objects.nonNull(recordNode) ? recordNode.getKey() : null;
        for (int i = 0; i < avlTrees.size(); i++) {
            GettingRecordThread thread = new GettingRecordThread(avlTrees.get(i), record);
            runnableThread.add(thread);
            thread.start();
            if (Objects.nonNull(record)) {
                recordNode = records.findGreater(record);
                record = Objects.nonNull(recordNode) ? recordNode.getKey() : null;
            }
        }
        WaitingCounter.waitingCounter(runnableThread);

        for (int i = 0; i < runnableThread.size(); i++) {
            OperationResult newRecord = runnableThread.get(i).getNewRecord();
            OperationResult prevRecord = runnableThread.get(i).getPrevRecord();
            if (Objects.nonNull(newRecord) && !newRecord.equals(prevRecord)) {
                int size = avlTrees.get(i).getSize();
                avlTrees.get(i).remove(newRecord);
                if (size == avlTrees.get(i).getSize()) {
                    List<OperationResult> operationResults = avlTrees.get(i).getKeys();
                    operationResults.remove(prevRecord);
                    badFillTree(operationResults, i);
                }
                records.insert(newRecord);
            }
        }
    }

    /**
     * Вынужденная операция из-за плохой реализации дерева
     */
    private void badFillTree(List<OperationResult> operations, int i) {
        AvlTree<OperationResult> newTree = new AvlTreeImpl<>(OperationResult::compareTo);
        operations.forEach(newTree::insert);
        avlTrees.set(i, newTree);
    }

    /**
     * Вынужденная операция из-за плохой реализации дерева
     */
    private void badFillRecordTree(List<OperationResult> operations) {
        AvlTree<OperationResult> newTree = new AvlTreeImpl<>(OperationResult::compareTo);
        operations.forEach(newTree::insert);
        records = newTree;
    }
}
