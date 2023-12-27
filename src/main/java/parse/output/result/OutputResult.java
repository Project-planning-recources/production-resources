package parse.output.result;

import algorithm.model.result.Result;
import parse.adapter.DateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <b>Класс для IO</b>
 * <b>Результат работы алгоритма</b>
 * <p>Содержит информацию о времени работы и распределении всех заказанных деталей по производству</p>
 */
@XmlRootElement(name = "Result")
@XmlType(name = "Result")
@XmlAccessorType(XmlAccessType.FIELD)
public class OutputResult {

    /**
     * Время и дата начала всей работы
     */
    @XmlAttribute(name = "all_start_date_time")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private LocalDateTime allStartTime;

    /**
     * Время и дата окончания всей работы
     */
    @XmlAttribute(name = "all_end_date_time")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private LocalDateTime allEndTime;

    /**
     * Результаты работы по каждому заказу
     */
    @XmlElement(name = "Order")
    private ArrayList<OutputOrderResult> outputOrderResults;

    @XmlTransient
    private HashMap<Long, ArrayList<OutputOperationResult>> performedOperationsOnEquipments;

    public OutputResult() {
        performedOperationsOnEquipments = new HashMap<>();
    }

    public OutputResult(Result result) {
        this.allStartTime = result.getAllStartTime();
        this.allEndTime = result.getAllEndTime();
        performedOperationsOnEquipments = new HashMap<>();

        ArrayList<OutputOrderResult> outputOrderResults = new ArrayList<>();
        result.getOrderResults().forEach(orderResult -> {
            outputOrderResults.add(new OutputOrderResult(orderResult, performedOperationsOnEquipments));
        });
        this.outputOrderResults = outputOrderResults;
    }

    public OutputResult(LocalDateTime allStartTime, LocalDateTime allEndTime, ArrayList<OutputOrderResult> outputOrderResults) {
        this.allStartTime = allStartTime;
        this.allEndTime = allEndTime;
        this.outputOrderResults = outputOrderResults;
    }

    public LocalDateTime getAllStartTime() {
        return allStartTime;
    }

    public LocalDateTime getAllEndTime() {
        return allEndTime;
    }

    public void setAllStartTime(LocalDateTime allStartTime) {
        this.allStartTime = allStartTime;
    }

    public void setAllEndTime(LocalDateTime allEndTime) {
        this.allEndTime = allEndTime;
    }

    public ArrayList<OutputOrderResult> getOrderResults() {
        return outputOrderResults;
    }

    public HashMap<Long, Integer> getAlternativeness() {

        return null;
    }

    public HashMap<Long, ArrayList<OutputOperationResult>> getPerformedOperationsOnEquipments() {
        return performedOperationsOnEquipments;
    }

    public HashMap<Long, ArrayList<OutputOperationResult>> fillPerformedOperationsOnEquipments() {
        for (OutputOrderResult order : outputOrderResults) {
            for (OutputProductResult product : order.getProductResults()) {
                for (OutputOperationResult operation : product.getPerformedOperations()) {
                    if (performedOperationsOnEquipments.containsKey(operation.getEquipmentId())) {
                        performedOperationsOnEquipments.get(operation.getEquipmentId()).add(operation);
                    } else {
                        ArrayList<OutputOperationResult> operations = new ArrayList<>();
                        operations.add(operation);
                        performedOperationsOnEquipments.put(operation.getEquipmentId(), operations);
                    }
                }
            }
        }
        return this.performedOperationsOnEquipments;
    }

    @Override
    public String toString() {
        return "Result{" +
                "allStartTime=" + allStartTime +
                ", allEndTime=" + allEndTime +
                ", orderResults=" + outputOrderResults +
                '}';
    }
}
