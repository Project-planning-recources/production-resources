package model.production;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * <b>Данные о конкретной единице оборудования</b>
 */
@XmlType(name = "Equipment")
@XmlAccessorType(XmlAccessType.FIELD)
public class Equipment implements Serializable {

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

    /**
     * Используется ли данное оборудование в текущий момент (пока не уверен, нужно ли это поле)
     */
    @XmlTransient
    private boolean isUsing;

    public Equipment() {

    }

    public Equipment(long id, String name, boolean isUsing) {
        this.id = id;
        this.name = name;
        this.isUsing = isUsing;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isUsing() {
        return isUsing;
    }

    public void setUsing(boolean using) {
        isUsing = using;
    }

}
