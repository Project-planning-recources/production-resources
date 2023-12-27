package parse.input.production;

import parse.input.order.*;
import parse.output.result.*;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * <b>Класс для IO</b>
 * <b>Данные о производстве</b>
 */
@XmlRootElement(name = "SystemInformation")
@XmlType(name = "SystemInformation")
@XmlAccessorType(XmlAccessType.FIELD)
public class InputProduction implements Serializable {

    /**
     * Расписание работы производства
     */
    @XmlElement(name = "CalendarInformation")
    private InputSchedule inputSchedule;

    /**
     * Группы оборудования, которые есть на производстве
     */
    @XmlElementWrapper(name="EquipmentInformation")
    @XmlElement(name="EquipmentGroup")
    private List<InputEquipmentGroup> inputEquipmentGroups;

    public InputProduction() {
    }

    public InputProduction(InputSchedule inputSchedule, List<InputEquipmentGroup> inputEquipmentGroups) {
        this.inputSchedule = inputSchedule;
        this.inputEquipmentGroups = inputEquipmentGroups;
    }

    public InputSchedule getSchedule() {
        return inputSchedule;
    }

    public List<InputEquipmentGroup> getEquipmentGroups() {
        return inputEquipmentGroups;
    }

    public void setSchedule(InputSchedule inputSchedule) {
        this.inputSchedule = inputSchedule;
    }

    public void setEquipmentGroups(List<InputEquipmentGroup> inputEquipmentGroups) {
        this.inputEquipmentGroups = inputEquipmentGroups;
    }

    @Override
    public String toString() {
        return "InputProduction{" +
                "inputSchedule=" + inputSchedule +
                ", inputEquipmentGroups=" + inputEquipmentGroups +
                '}';
    }
}
