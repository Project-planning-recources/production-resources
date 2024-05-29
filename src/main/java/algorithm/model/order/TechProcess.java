package algorithm.model.order;

import parse.input.order.InputTechProcess;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;

/**
 * <b>Класс для Алгоритма</b>
 * <b>Данные о техпроцессе</b>
 */
public class TechProcess {

    /**
     * ID техпроцесса
     */
    private long id;

    /**
     * Последовательный список операций, которые включены в техпроцесс
     */
    private LinkedList<Operation> operations;

    public TechProcess() {

    }

    public TechProcess(InputTechProcess inputTechProcess) {
        this.id = inputTechProcess.getId();

        LinkedList<Operation> operations = new LinkedList<>();
        inputTechProcess.getOperations().forEach(inputOperation -> {
            operations.add(new Operation(inputOperation));
        });
        this.operations = operations;
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
