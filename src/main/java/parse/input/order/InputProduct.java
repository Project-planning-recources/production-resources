package parse.input.order;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Objects;

/**
 * <b>Класс для IO</b>
 * <b>Данные о детали</b>
 * <p>Тут не конкретная деталь, а просто её "тип". Конкретные детали появляются на этапе формирования результата, когда мы присваиваем им id (класс ProductionResult)</p>
 */
@XmlType(name = "Detail")
public class InputProduct {

    /**
     * ID детали
     */
    @XmlAttribute(name = "id")
    private long id;

    /**
     * Название детали
     */
    @XmlAttribute(name = "name")
    private String name;

    /**
     * Количество деталей в заказе
     */
    @XmlAttribute(name = "count")
    private Integer count;

    /**
     * Список возможных альтернативных техпроцессов для детали
     */
    @XmlElement(name = "TechProcess")
    private ArrayList<InputTechProcess> inputTechProcesses;

    public InputProduct() {

    }

    public InputProduct(long id, String name, Integer count, ArrayList<InputTechProcess> inputTechProcesses) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.inputTechProcesses = inputTechProcesses;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return count;
    }

    public ArrayList<InputTechProcess> getTechProcesses() {
        return inputTechProcesses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputProduct inputProduct = (InputProduct) o;
        return id == inputProduct.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public InputTechProcess getTechProcessByTechProcessId(long techProcessId) {
        for(InputTechProcess inputTechProcess : inputTechProcesses) {
            if(inputTechProcess.getId() == techProcessId) {
                return inputTechProcess;
            }
        }
        return null;
    }
}
