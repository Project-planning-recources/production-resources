package model.order;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Objects;

/**
 * <b>Данные о детали</b>
 * <p>Тут не конкретная деталь, а просто её "тип". Конкретные детали появляются на этапе формирования результата, когда мы присваиваем им id (класс ProductionResult)</p>
 */
@XmlType(name = "Detail")
public class Product {

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
    private ArrayList<TechProcess> techProcesses;

    public Product() {

    }

    public Product(long id, String name, Integer count, ArrayList<TechProcess> techProcesses) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.techProcesses = techProcesses;
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

    public ArrayList<TechProcess> getTechProcesses() {
        return techProcesses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public TechProcess getTechProcessByTechProcessId(long techProcessId) {
        for(TechProcess techProcess : techProcesses) {
            if(techProcess.getId() == techProcessId) {
                return techProcess;
            }
        }
        return null;
    }
}
