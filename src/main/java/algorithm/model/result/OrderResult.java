package algorithm.model.result;

import algorithm.model.order.Order;
import parse.adapter.DateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Класс для Алгоритма</b>
 * <b>Результат работы алгоритма для конкретного заказа</b>
 */
public class OrderResult {

    /**
     * ID заказа
     */
    private long orderId;

    /**
     * Время раннего начала выполнения заказа
     */
    private LocalDateTime earlyStartTime;

    /**
     * Время и дата начала выполнения заказа
     */
    private LocalDateTime startTime;

    /**
     * Время и дата окончания выполнения заказа
     */
    private LocalDateTime endTime;

    /**
     * Результаты работы по каждой детали
     */
    private ArrayList<ProductResult> productResults;

    /**
     * Результат, к которому относится заказ
     */
    private Result result;

    /**
     * Входные данные для заказа
     */
    private Order order;

    private boolean inProgress;

    public OrderResult() {

    }

    public OrderResult(long orderId, LocalDateTime startTime, LocalDateTime endTime, ArrayList<ProductResult> productResults) {
        this.orderId = orderId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.productResults = productResults;
        this.inProgress = false;
    }

    public OrderResult(long orderId, LocalDateTime earlyStartTime,
                       LocalDateTime startTime, LocalDateTime endTime, ArrayList<ProductResult> productResults) {
        this.orderId = orderId;
        this.earlyStartTime = earlyStartTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.productResults = productResults;
        this.inProgress = false;
    }

    public long getOrderId() {
        return orderId;
    }

    public LocalDateTime getEarlyStartTime() {
        return earlyStartTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ArrayList<ProductResult> getProductResults() {
        return productResults;
    }

    public Result getResult() {
        return result;
    }

    public Order getOrder() {
        return order;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    @Override
    public String toString() {
        return "OrderResult{" +
                "orderId=" + orderId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", productResults=" + productResults +
                '}';
    }
}
