package parse.input.order;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedList;
import java.util.Objects;

/**
 * <b>Класс для IO</b>
 * <b>Данные о техпроцессе</b>
 */
@XmlType(name = "TechProcess")
public class InputTechProcess {

    /**
     * ID техпроцесса
     */
    @XmlAttribute(name = "id")
    private long id;

    /**
     * Последовательный список операций, которые включены в техпроцесс
     */
    @XmlElement(name = "Operation")
    private LinkedList<InputOperation> inputOperations;

    public InputTechProcess() {

    }

    public InputTechProcess(long id, LinkedList<InputOperation> inputOperations) {
        this.id = id;
        this.inputOperations = inputOperations;
    }

    public long getId() {
        return id;
    }

    public LinkedList<InputOperation> getOperations() {
        return inputOperations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputTechProcess that = (InputTechProcess) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public InputOperation getOperationByOperationId(long operationId) {
        for(InputOperation inputOperation : inputOperations) {
            if(inputOperation.getId() == operationId) {
                return inputOperation;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "InputTechProcess{" +
                "id=" + id +
                ", inputOperations=" + inputOperations +
                '}';
    }
}
