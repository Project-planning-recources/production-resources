package algorithm.records.util;

import algorithm.model.production.Equipment;
import algorithm.model.production.EquipmentGroup;
import algorithm.model.result.OperationResult;
import org.apache.directory.server.core.avltree.AvlTree;
import org.apache.directory.server.core.avltree.LinkedAvlNode;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class EquipmentFinder {

    private final Map<Long, EquipmentGroup> equipmentGroups;

    public static long operationsCount = 0;

    public EquipmentFinder(Map<Long, EquipmentGroup> equipmentGroups) {
        this.equipmentGroups = equipmentGroups;
    }

    public boolean findAvailableEquipmentByTimeTick(OperationResult operation, LocalDateTime timeTick) {
        if (Objects.nonNull(operation) && (operation.getPrevOperationId() == 0 ||
                (operation.getPrevOperation().isDone() && !timeTick.isBefore(operation.getPrevOperation().getEndTime())))) {
            EquipmentGroup requiredGroup = equipmentGroups.get(operation.getEquipmentGroupId());
            Equipment chosenEquipment = requiredGroup.getEquipment()
                    .stream()
                    .filter(equipment -> !equipment.isBusy(timeTick))
                    .findFirst()
                    .orElse(null);
            if (Objects.nonNull(chosenEquipment)) {
                operation.setEquipmentId(chosenEquipment.getId());
                return true;
            }
        }
        return false;
    }

    public OperationResult findAvailableEquipmentByTimeTick(AvlTree<OperationResult> operations, LocalDateTime timeTick) {
        LinkedAvlNode<OperationResult> operationNode = operations.getFirst();
        OperationResult operation = Objects.nonNull(operationNode) ? operationNode.getKey() : null;
        int c = 0;
        while (Objects.nonNull(operationNode)){
            c++;
            //System.out.println("Из EquipmentFinder.findAvailableEquipmentByTimeTick");

            if(c > operationsCount) {
                System.out.println("Выход по счётчику");
                return operation;
            }
            if (findAvailableEquipmentByTimeTick(operation, timeTick)) {
                break;
            } else {
                operationNode = operations.findGreater(operation);
                operation = Objects.nonNull(operationNode) ? operationNode.getKey() : null;
            }

        }

        return operation;
    }
}
