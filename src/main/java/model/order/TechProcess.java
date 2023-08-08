package model.order;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedList;
import java.util.Objects;

/**
 * <b>Данные о техпроцессе</b>
 */
@XmlType(name = "TechProcess")
public class TechProcess {

    /**
     * ID техпроцесса
     */
    @XmlAttribute(name = "id")
    private long id;

    /**
     * Последовательный список операций, которые включены в техпроцесс
     */
    @XmlElement(name = "Operation")
    private LinkedList<Operation> operations;

    public TechProcess() {

    }

    public TechProcess(long id, LinkedList<Operation> operations) {
        this.id = id;
        this.operations = operations;
    }

    public long getId() {
        return id;
    }

    public LinkedList<Operation> getOperations() {
        return operations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TechProcess that = (TechProcess) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Operation getOperationByOperationId(long operationId) {
        for(Operation operation : operations) {
            if(operation.getId() == operationId) {
                return operation;
            }
        }
        return null;
    }
}
