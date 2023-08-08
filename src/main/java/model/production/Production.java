package model.production;

import model.order.Operation;
import model.result.OperationResult;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * <b>Данные о производстве</b>
 */
@XmlRootElement(name = "SystemInformation")
@XmlType(name = "SystemInformation")
@XmlAccessorType(XmlAccessType.FIELD)
public class Production implements Serializable {

    /**
     * Расписание работы производства
     */
    @XmlElement(name = "CalendarInformation")
    private Schedule schedule;

    /**
     * Группы оборудования, которые есть на производстве
     */
    @XmlElementWrapper(name="EquipmentInformation")
    @XmlElement(name="EquipmentGroup")
    private List<EquipmentGroup> equipmentGroups;

    public Production() {
    }

    public Production(Schedule schedule, List<EquipmentGroup> equipmentGroups) {
        this.schedule = schedule;
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

    public Equipment getEquipmentForOperation(OperationResult choose, Long requirement) throws Exception {
        for (EquipmentGroup equipmentGroup :
                equipmentGroups) {
            if(equipmentGroup.getId() == requirement) {
                for (Equipment e :
                        equipmentGroup.getEquipment()) {
                    if(!e.isUsing()) {
                        return e;
                    }
                }
            }
        }
        throw new Exception("No equipment for operation");
    }
}
