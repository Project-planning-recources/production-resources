package algorithm.parallel;

import algorithm.model.production.Equipment;
import algorithm.model.production.EquipmentGroup;
import algorithm.model.result.OperationResult;
import org.apache.directory.server.core.avltree.AvlTree;
import util.WaitingCounter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompositeRecord implements Record, Runnable {

    private final Map<Long, EquipmentGroup> equipmentGroups;
    private final List<AvlTree<OperationResult>> avlTrees;

    private final List<OperationResult> records;

    private final List<Thread> threads;

    public CompositeRecord(List<EquipmentGroup> groups, List<AvlTree<OperationResult>> avlTrees) {
        this.avlTrees = avlTrees;
        this.equipmentGroups = groups.stream()
                .collect(Collectors.toMap(EquipmentGroup::getId, Function.identity()));
        this.records = new ArrayList<>();
        this.threads = new ArrayList<>();
        for (int i = 0; i < avlTrees.size(); i++) {
            records.add(null);
            threads.add(new Thread(this, String.valueOf(i)));
        }
        fillRecords();
    }

    @Override
    public OperationResult getRecord(LocalDateTime timeTick) {
        int resultIndex = findAvailableOperationOnEquipmentByTimeTick(timeTick);
        OperationResult result = null;
        if (0 <= resultIndex && resultIndex < records.size()) {
            result = records.get(resultIndex);
            records.set(resultIndex, null);
        }
        fillRecords();
        return result;
    }

    private int findAvailableOperationOnEquipmentByTimeTick(LocalDateTime timeTick) {
        int resultIndex = -1;
        for (int i = 0; i < records.size(); i++) {
            OperationResult record = records.get(i);
            if (Objects.nonNull(record)) {
                if (Objects.isNull(record.getPrevOperation()) ||
                        Objects.nonNull(record.getPrevOperation()) && record.getPrevOperation().isDone() &&
                                !timeTick.isBefore(record.getPrevOperation().getEndTime())) {
                    EquipmentGroup requiredGroup = equipmentGroups.get(record.getEquipmentGroupId());
                    Equipment chosenEquipment = requiredGroup.getEquipment()
                            .stream()
                            .filter(equipment -> !equipment.isBusy(timeTick))
                            .findFirst()
                            .orElse(null);
                    if (Objects.nonNull(chosenEquipment)) {
                        record.setEquipmentId(chosenEquipment.getId());
                        resultIndex = i;
                        break;
                    }
                }
            }

        }

        return resultIndex;
    }

    private void fillRecords() {
        List<Thread> runnableThread = new ArrayList<>();
        for (int i = 0; i < records.size(); i++) {
            if (Objects.isNull(records.get(i)) && avlTrees.get(i).getSize() > 0) {
                runnableThread.add(threads.get(i));
            }
        }
        runnableThread.forEach(Thread::start);

        WaitingCounter.waitingCounter(runnableThread);

        for (int i = 0; i < records.size(); i++) {
            Thread currentThread = threads.get(i);
            if (Thread.State.TERMINATED.equals(currentThread.getState())) {
                Thread newThread = new Thread(this, currentThread.getName());
                threads.set(i, newThread);
            }
        }
    }

    @Override
    public void run() {
        int threadIndex = Integer.parseInt(Thread.currentThread().getName());
        OperationResult result = getFirstOperation(threadIndex);
        records.set(threadIndex, result);
    }

    private OperationResult getFirstOperation(int threadIndex) {
        AvlTree<OperationResult> tree = avlTrees.get(threadIndex);
        if(tree.getSize() == 0) {
            return null;
        }
        return tree.remove(tree.getFirst().getKey());
    }
}
