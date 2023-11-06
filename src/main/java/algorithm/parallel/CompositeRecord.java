package algorithm.parallel;

import algorithm.model.production.Equipment;
import algorithm.model.production.EquipmentGroup;
import algorithm.model.result.OperationResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompositeRecord implements Record {

    private final Map<Long, EquipmentGroup> equipmentGroups;
    private final List<OperationResult> records;

    public CompositeRecord(List<EquipmentGroup> groups) {
        this.records = new ArrayList<>();
        this.equipmentGroups = groups.stream()
                .collect(Collectors.toMap(EquipmentGroup::getId, Function.identity()));
    }

    @Override
    public OperationResult getRecord(List<OperationResult> operations, LocalDateTime timeTick) {
        List<OperationResult> records = getRecordByDistanceBetweenTime(operations, timeTick);
        this.records.addAll(records);
        OperationResult result = this.records.stream().findFirst().orElse(null);
        remove(result);
        removeRecordsAfterAcceptingDecision();
        return result;
    }

    public void add(OperationResult record) {
        records.add(record);
    }

    public void remove(OperationResult record) {
        records.remove(record);
    }

    private void removeRecordsAfterAcceptingDecision() {
        records.forEach(record ->{
            record.setEquipmentId(0);
        });
        records.clear();
    }

    private List<OperationResult> getRecordByDistanceBetweenTime(List<OperationResult> operations, LocalDateTime timeTick) {
        List<OperationResult> result = new ArrayList<>();
        for (OperationResult operation : operations) {
            EquipmentGroup requiredGroup = equipmentGroups.get(operation.getEquipmentGroupId());
            Equipment chosenEquipment = requiredGroup.getEquipment()
                    .stream()
                    .filter(equipment -> !equipment.isBusy(timeTick))
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
