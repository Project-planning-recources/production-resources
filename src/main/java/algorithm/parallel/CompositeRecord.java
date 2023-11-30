package algorithm.parallel;

import algorithm.model.production.Equipment;
import algorithm.model.production.EquipmentGroup;
import algorithm.model.result.OperationResult;
import org.apache.directory.server.core.avltree.AvlTree;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompositeRecord implements Record {

    private final Map<Long, EquipmentGroup> equipmentGroups;
    private final AvlTree<OperationResult> operationAvlTree;

    public CompositeRecord(List<EquipmentGroup> groups, AvlTree<OperationResult> operationAvlTree) {
        this.operationAvlTree = operationAvlTree;
        this.equipmentGroups = groups.stream()
                .collect(Collectors.toMap(EquipmentGroup::getId, Function.identity()));
    }

    @Override
    public OperationResult getRecord(LocalDateTime timeTick) {
        OperationResult result = findAvailableOperationOnEquipmentByTimeTick(timeTick);
        if (Objects.nonNull(result)) {
            this.operationAvlTree.remove(result);
        }
        return result;
    }

    private OperationResult findAvailableOperationOnEquipmentByTimeTick(LocalDateTime timeTick) {
        if(operationAvlTree.getSize() == 0) {
            return null;
        }
        OperationResult result = operationAvlTree.getFirst().getKey();
        long size = 0;
        while(true) {
            size++;
            if (Objects.isNull(result.getPrevOperation()) ||
                    Objects.nonNull(result.getPrevOperation()) && result.getPrevOperation().isDone()) {
                EquipmentGroup requiredGroup = equipmentGroups.get(result.getEquipmentGroupId());
                Equipment chosenEquipment = requiredGroup.getEquipment()
                        .stream()
                        .filter(equipment -> !equipment.isBusy(timeTick))
                        .findFirst()
                        .orElse(null);
                if (Objects.nonNull(chosenEquipment)) {
                    result.setEquipmentId(chosenEquipment.getId());
                    break;
                }
            }

            if (size == operationAvlTree.getSize()) {
                result = null;
                break;
            } else {
                result = operationAvlTree.findGreater(result).getKey();
            }
        }

        return result;
    }
}
