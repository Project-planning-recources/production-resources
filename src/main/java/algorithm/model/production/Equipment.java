package algorithm.model.production;

import parse.input.production.InputEquipment;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * <b>Класс для Алгоритма</b>
 * <b>Данные о конкретной единице оборудования</b>
 */
public class Equipment implements Serializable {

    /**
     * ID единицы оборудования
     */
    private long id;

    /**
     * Название оборудования
     */
    private String name;

    /**
     * Используется ли данное оборудование в текущий момент (пока не уверен, нужно ли это поле)
     */
    private boolean isUsing;

    public Equipment() {

    }

    public Equipment(InputEquipment inputEquipment) {
        this.id = inputEquipment.getId();
        this.name = inputEquipment.getName();
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

    @Override
    public String toString() {
        return "Equipment{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isUsing=" + isUsing +
                '}';
    }
}
