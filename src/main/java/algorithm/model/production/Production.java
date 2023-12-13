package algorithm.model.production;

import algorithm.model.order.Operation;
import algorithm.model.result.OperationResult;
import parse.input.production.InputProduction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <b>Класс для Алгоритма</b>
 * <b>Данные о производстве</b>
 */
public class Production implements Serializable {

    /**
     * Расписание работы производства
     */
    private Schedule schedule;

    /**
     * Группы оборудования, которые есть на производстве
     */
    private List<EquipmentGroup> equipmentGroups;

    public Production() {
    }

    public Production(Schedule schedule, List<EquipmentGroup> equipmentGroups) {
        this.schedule = schedule;
        this.equipmentGroups = equipmentGroups;
    }

    public Production(InputProduction inputProduction) {
        this.schedule = new Schedule(inputProduction.getSchedule());

        ArrayList<EquipmentGroup> equipmentGroups = new ArrayList<>();
        inputProduction.getEquipmentGroups().forEach(inputEquipmentGroup -> {
            equipmentGroups.add(new EquipmentGroup(inputEquipmentGroup));
        });
        this.equipmentGroups = equipmentGroups;
    }

    /**
     * Функция для проверки возможности осуществления операции на данном предприятии
     * @param operation - операция
     * @return true/false
     */
    public boolean isPossibleToMake(Operation operation) {
        for (EquipmentGroup e :
                equipmentGroups) {
            if (e.getId() == operation.getRequiredEquipment()) {
                return true;
            }
        }
        return false;
    }

    public HashMap<Long, Integer> getFreeCountEquipment() {
        HashMap<Long, Integer> freeCountEquipment = new HashMap<>();

        equipmentGroups.forEach(equipmentGroup -> {
            freeCountEquipment.put(equipmentGroup.getId(), equipmentGroup.getFreeCount());
        });

        return freeCountEquipment;
    }

    public boolean isOperationCanBePerformed(Long requirement) throws Exception {
        for (EquipmentGroup equipmentGroup :
                equipmentGroups) {
            if(equipmentGroup.getId() == requirement) {
                return equipmentGroup.thereAreFree();
            }
        }
        throw new Exception("Wrong equipment group id");
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public List<EquipmentGroup> getEquipmentGroups() {
        return equipmentGroups;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void setEquipmentGroups(List<EquipmentGroup> equipmentGroups) {
        this.equipmentGroups = equipmentGroups;
    }

    public Equipment getEquipmentForOperation(OperationResult choose) throws Exception {
        for (EquipmentGroup equipmentGroup :
                equipmentGroups) {
            if(equipmentGroup.getId() == choose.getEquipmentGroupId()) {
                for (Equipment e :
                        equipmentGroup.getEquipment()) {

                    if(!e.isUsing()) {
                        return e;
                    }
                }
            }
        }
        System.out.println(equipmentGroups);
        System.out.println(choose);
        throw new Exception("No equipment for operation");
    }

    public Long getEquipmentGroupIdByEquipmentId(Long equipmentId) {
        for (EquipmentGroup equipmentGroup : this.equipmentGroups) {
            for (Equipment equipment : equipmentGroup.getEquipment()) {
                if (equipmentId == equipment.getId()) {
                    return equipmentGroup.getId();
                }
            }
        }
        throw new RuntimeException("Unreachable code in <public Long getEquipmentGroupIdByEquipmentId(Long equipmentId)>");
    }

    @Override
    public String toString() {
        return "Production{" +
                "schedule=" + schedule +
                ", equipmentGroups=" + equipmentGroups +
                '}';
    }
}
