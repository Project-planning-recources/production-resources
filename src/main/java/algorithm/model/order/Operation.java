package algorithm.model.order;

import parse.input.order.InputOperation;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <b>Класс для Алгоритма</b>
 * <b>Данные об операции</b>
 */
public class Operation {

    /**
     * ID операции
     */
    private long id;

    /**
     * Название операции (не знаю, нужно ли оно нам, может пригодиться)
     */
    private String name;

    /**
     * Длительность в секундах
     */
    private int duration;

    /**
     * Необходимое для выполнения операции оборудование
     */
    private long requiredEquipment;

    /**
     * ID предыдущей операции
     */
    private long prevOperationId;

    /**
     * ID следующей операции
     */
    private long nextOperationId;

    public Operation() {

    }

    public Operation(InputOperation inputOperation) {
        this.id = inputOperation.getId();
        this.name = inputOperation.getName();
        this.duration = inputOperation.getDuration();
        this.requiredEquipment = inputOperation.getRequiredEquipment();
        this.prevOperationId = inputOperation.getPrevOperationId();
        this.nextOperationId = inputOperation.getNextOperationId();
    }

    public Operation(long id, String name, int duration, long requiredEquipment, long prevOperationId, long nextOperationId) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.requiredEquipment = requiredEquipment;
        this.prevOperationId = prevOperationId;
        this.nextOperationId = nextOperationId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public Long getRequiredEquipment() {
        return requiredEquipment;
    }

    public long getPrevOperationId() {
        return prevOperationId;
    }

    public long getNextOperationId() {
        return nextOperationId;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", requiredEquipment=" + requiredEquipment +
                '}';
    }
}
