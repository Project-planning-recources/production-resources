package algorithm.model.production;

import parse.input.production.InputEquipmentGroup;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <b>Класс для Алгоритма</b>
 * <b>Данные о группе оборудования</b>
 */
public class EquipmentGroup implements Serializable {

    /**
     * ID группы оборудования (он же id типа оборудования)
     */
    private long id;

    /**
     * Название оборудования
     */
    private String name;

    /**
     * Список с оборудованием группы (в одной группе одинаковое оборудование)
     */
    private List<Equipment> equipment;

    public EquipmentGroup() {

    }

    public EquipmentGroup(InputEquipmentGroup inputEquipmentGroup) {
        this.id = inputEquipmentGroup.getId();
        this.name = inputEquipmentGroup.getName();

        ArrayList<Equipment> equipment = new ArrayList<>();
        inputEquipmentGroup.getEquipment().forEach(inputEquipment -> {
            equipment.add(new Equipment(inputEquipment));
        });
        this.equipment = equipment;
    }

    public EquipmentGroup(long id, String name, List<Equipment> equipment) {
        this.id = id;
        this.name = name;
        this.equipment = equipment;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    public Integer getFreeCount() {
        int counter = 0;
        for (Equipment e :
                equipment) {
            if (!e.isUsing()) {
                counter++;
            }
        }
        return counter;
    }

    public boolean thereAreFree() {
        for (Equipment e :
                equipment) {
            if (!e.isUsing()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "EquipmentGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", equipment=" + equipment +
                '}';
    }
}
