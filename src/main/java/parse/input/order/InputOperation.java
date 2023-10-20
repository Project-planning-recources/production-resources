package parse.input.order;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <b>Класс для IO</b>
 * <b>Данные об операции</b>
 */
@XmlType(name = "Operation")
@XmlAccessorType(XmlAccessType.FIELD)
public class InputOperation {

    /**
     * ID операции
     */
    @XmlAttribute(name = "id")
    private long id;

    /**
     * Название операции (не знаю, нужно ли оно нам, может пригодиться)
     */
    @XmlAttribute(name = "name")
    private String name;

    /**
     * Длительность в секундах
     */
    @XmlAttribute(name = "duration")
    private int duration;

    /**
     * Необходимое для выполнения операции оборудование
     */
    @XmlAttribute(name = "equipment_group")
    private long requiredEquipment;

    /**
     * ID предыдущей операции
     */
    @XmlAttribute(name = "prev_operation_id")
    private long prevOperationId;

    /**
     * ID следующей операции
     */
    @XmlAttribute(name = "next_operation_id")
    private long nextOperationId;

    public InputOperation() {

    }

    public InputOperation(long id, String name, int duration, long requiredEquipment, long prevOperationId, long nextOperationId) {
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

    public void setPrevOperationId(long prevOperationId) {
        this.prevOperationId = prevOperationId;
    }

    public void setNextOperationId(long nextOperationId) {
        this.nextOperationId = nextOperationId;
    }
}
