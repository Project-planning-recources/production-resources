package algorithm.parallel;

import algorithm.model.production.EquipmentGroup;
import algorithm.model.result.OperationResult;
import algorithm.parallel.threads.GettingRecordThread;
import algorithm.parallel.threads.TreeFillingThread;
import algorithm.parallel.util.EquipmentFinder;
import org.apache.directory.server.core.avltree.AvlTree;
import org.apache.directory.server.core.avltree.AvlTreeImpl;
import org.apache.directory.server.core.avltree.LinkedAvlNode;
import util.WaitingCounter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompositeRecord implements Record {

    private final List<AvlTree<OperationResult>> avlTrees;
    private final AvlTree<OperationResult> records;
    private final EquipmentFinder equipmentFinder;

    public CompositeRecord(List<EquipmentGroup> groups, List<AvlTree<OperationResult>> avlTrees) {
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
            records.remove(result);
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
                    records.remove(prevRecord);
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
                records.insert(avlTrees.get(i).remove(newRecord));
            }
        }
    }
}
