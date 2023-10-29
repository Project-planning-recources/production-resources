package algorithm.parallel;

import algorithm.model.production.Equipment;
import algorithm.model.production.EquipmentGroup;
import algorithm.model.result.OperationResult;

import java.util.*;

public class CompositeRecord implements Record {

    private final HashMap<Long, EquipmentGroup> equipmentGroups;
    private final List<OperationResult> records;

    public CompositeRecord(List<EquipmentGroup> groups) {
        this.records = new ArrayList<>();
        this.equipmentGroups = new HashMap<>();
        groups.stream()
                .map(group -> {
                    this.equipmentGroups.put(group.getId(), group);
                    return group;
                });
    }

    public OperationResult getRecord(List<OperationResult> operations) {
        List<OperationResult> records = getRecordByDistanceBetweenTime(operations);
        return records.stream().findFirst().get();
    }

    public void add(OperationResult record) {
        records.add(record);
    }

    public void remove(OperationResult record) {
        records.remove(record);
    }

    private List<OperationResult> getRecordByDistanceBetweenTime(List<OperationResult> operations) {
        List<OperationResult> result = new ArrayList<>();
        for (OperationResult operation : operations) {
            EquipmentGroup requiredGroup = equipmentGroups.get(operation.getEquipmentGroupId());
            Equipment chosenEquipment = requiredGroup.getEquipment()
                    .stream()
                    .filter(equipment -> equipment.isBusy(operation.getStartTime()))
                    .findFirst()
                    .orElse(null);

            if (Objects.nonNull(chosenEquipment)) {
                result.add(operation);
                operation.setEquipmentId(chosenEquipment.getId());
            }
        }

        return result;
    }
}
