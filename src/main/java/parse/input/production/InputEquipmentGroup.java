package parse.input.production;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * <b>Класс для IO</b>
 * <b>Данные о группе оборудования</b>
 */
@XmlType(name = "EquipmentGroup")
@XmlAccessorType(XmlAccessType.FIELD)
public class InputEquipmentGroup implements Serializable {

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
    private List<InputEquipment> inputEquipments;

    public InputEquipmentGroup() {

    }

    public InputEquipmentGroup(long id, String name, List<InputEquipment> inputEquipments) {
        this.id = id;
        this.name = name;
        this.inputEquipments = inputEquipments;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<InputEquipment> getEquipment() {
        return inputEquipments;
    }

    @Override
    public String toString() {
        return "InputEquipmentGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", inputEquipments=" + inputEquipments +
                '}';
    }
}
