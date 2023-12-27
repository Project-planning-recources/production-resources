package algorithm.records.record;

import algorithm.model.production.EquipmentGroup;
import algorithm.model.result.OperationResult;
import algorithm.records.util.EquipmentFinder;
import org.apache.directory.server.core.avltree.AvlTree;
import org.apache.directory.server.core.avltree.AvlTreeImpl;
import org.apache.directory.server.core.avltree.LinkedAvlNode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompositeRecord implements Record {

    private AvlTree<OperationResult> avlTree;

    private final EquipmentFinder equipmentFinder;

    public CompositeRecord(List<EquipmentGroup> groups, AvlTree<OperationResult> avlTree) {
        this.avlTree = avlTree;
        Map<Long, EquipmentGroup> equipmentGroups = groups.stream()
                .collect(Collectors.toMap(EquipmentGroup::getId, Function.identity()));
        this.equipmentFinder = new EquipmentFinder(equipmentGroups);
    }

    @Override
    public OperationResult getRecord(LocalDateTime timeTick) {
        OperationResult result = equipmentFinder.findAvailableEquipmentByTimeTick(avlTree, timeTick);
        if (Objects.nonNull(result)) {
            int size = avlTree.getSize();
            avlTree.remove(result);
            if (size == avlTree.getSize()) {
                List<OperationResult> operationResults = avlTree.getKeys();
                operationResults.remove(result);
                badFillTree(operationResults);
            }
        }
        return result;
    }

    public AvlTree<OperationResult> getAvlTree() {
        return avlTree;
    }

    public void fillTree(List<OperationResult> readyOperations, LocalDateTime timeTick) {
        if (Objects.isNull(readyOperations) || readyOperations.isEmpty()) {
            return;
        }
        readyOperations.forEach(avlTree::insert);
    }

    /**
     * Вынужденная операция из-за плохой реализации дерева
     */
    private void badFillTree(List<OperationResult> operations) {
        AvlTree<OperationResult> newTree = new AvlTreeImpl<>(OperationResult::compareTo);
        operations.forEach(newTree::insert);
        avlTree = newTree;
    }
}
