package parse.output.result;

import algorithm.model.result.ProductResult;
import parse.adapter.DateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

/**
 * <b>Класс для IO</b>
 * <b>Результат работы алгоритма для конкретной детали</b>
 */
@XmlType(name = "Product")
@XmlAccessorType(XmlAccessType.FIELD)
public class OutputProductResult {

    /**
     * ID конкретной детали (например, если просят изготовить 100 шестерёнок, у каждой будет один productionId, но разные id)
     */
    @XmlAttribute(name = "id")
    private long id;

    /**
     * общий ID детали(совпадает с id в классе Product)
     */
    @XmlAttribute(name = "product_id")
    private long productId;

    /**
     * ID выбранного для детали техпроцесса
     */
    @XmlAttribute(name = "tech_process_id")
    private long techProcessId;

    /**
     * Время и дата начала выполнения детали
     */
    @XmlAttribute(name = "product_start_date_time")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private LocalDateTime startTime;

    /**
     * Время и дата окончания выполнения детали
     */
    @XmlAttribute(name = "product_end_date_time")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private LocalDateTime endTime;

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Список выполненных операций для создания детали
     */
    @XmlElement(name = "Operation")
    private LinkedList<OutputOperationResult> performedOperations;


    public OutputProductResult() {

    }

    public OutputProductResult(ProductResult productResult, HashMap<Long, ArrayList<OutputOperationResult>> performedOperationsOnEquipments) {
        this.id = productResult.getId();
        this.productId = productResult.getProductId();
        this.techProcessId = productResult.getTechProcessId();
        this.startTime = productResult.getStartTime();
        this.endTime = productResult.getEndTime();

        LinkedList<OutputOperationResult> performedOperations = new LinkedList<>();
        productResult.getPerformedOperations().forEach(operationResult -> {
            performedOperations.add(new OutputOperationResult(operationResult, performedOperationsOnEquipments));
        });
        this.performedOperations = performedOperations;
    }

    public OutputProductResult(long id, long productId, long techProcessId, LocalDateTime startTime, LocalDateTime endTime,
                               LinkedList<OutputOperationResult> performedOperations) {
        this.id = id;
        this.productId = productId;
        this.techProcessId = techProcessId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.performedOperations = performedOperations;
    }

    public long getId() {
        return id;
    }

    public long getProductId() {
        return productId;
    }

    public long getTechProcessId() {
        return techProcessId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LinkedList<OutputOperationResult> getPerformedOperations() {
        return performedOperations;
    }


    @Override
    public String toString() {
        return "ProductResult{" +
                "id=" + id +
                ", productId=" + productId +
                ", techProcessId=" + techProcessId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", performedOperations=" + performedOperations +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OutputProductResult that = (OutputProductResult) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
