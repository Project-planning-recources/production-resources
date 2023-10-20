package parse.input.production;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * <b>Класс для IO</b>
 * <b>Данные о конкретной единице оборудования</b>
 */
@XmlType(name = "Equipment")
@XmlAccessorType(XmlAccessType.FIELD)
public class InputEquipment implements Serializable {

    /**
     * ID единицы оборудования
     */
    @XmlAttribute(name = "id")
    private long id;

    /**
     * Название оборудования
     */
    @XmlAttribute(name = "name")
    private String name;


    public InputEquipment() {

    }

    public InputEquipment(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


}
