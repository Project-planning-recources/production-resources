package model.production;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * <b>Данные о группе оборудования</b>
 */
@XmlType(name = "EquipmentGroup")
@XmlAccessorType(XmlAccessType.FIELD)
public class EquipmentGroup implements Serializable {

    /**
     * ID группы оборудования (он же id типа оборудования)
     */
    @XmlAttribute(name = "id")
    private long id;

    /**
     * Название оборудования
     */
    @XmlAttribute(name = "name")
    private String name;

    /**
     * Список с оборудованием группы (в одной группе одинаковое оборудование)
     */
    @XmlElement(name = "Equipment")
    private List<Equipment> equipment;

    public EquipmentGroup() {

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
}
