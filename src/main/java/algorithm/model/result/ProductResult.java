package algorithm.model.result;

import parse.adapter.DateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Objects;

/**
 * <b>Класс для Алгоритма</b>
 * <b>Результат работы алгоритма для конкретной детали</b>
 */
public class ProductResult {

    /**
     * ID конкретной детали (например, если просят изготовить 100 шестерёнок, у каждой будет один productionId, но разные id)
     */
    private long id;

    /**
     * общий ID детали(совпадает с id в классе Product)
     */
    private long productId;

    /**
     * ID выбранного для детали техпроцесса
     */
    private long techProcessId;

    /**
     * Время и дата начала выполнения детали
     */
    private LocalDateTime startTime;

    /**
     * Время и дата окончания выполнения детали
     */
    private LocalDateTime endTime;

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Список выполненных операций для создания детали
     */
    private LinkedList<OperationResult> performedOperations;

    /**
     * Заказ, частью которого является деталь
     */
    private OrderResult orderResult;

    /**
     * Готовность детали
     */
    private boolean isDone;

    public ProductResult() {

    }

    public ProductResult(long id, long productId, long techProcessId, LocalDateTime startTime, LocalDateTime endTime,
                         LinkedList<OperationResult> performedOperations, OrderResult orderResult) {
        this.id = id;
        this.productId = productId;
        this.techProcessId = techProcessId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.performedOperations = performedOperations;
        this.orderResult = orderResult;
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

    public LinkedList<OperationResult> getPerformedOperations() {
        return performedOperations;
    }

    public OrderResult getOrderResult() {
        return orderResult;
    }

    public boolean isDone() {
        return isDone;
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
                ", isDone=" + isDone +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductResult that = (ProductResult) o;
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
