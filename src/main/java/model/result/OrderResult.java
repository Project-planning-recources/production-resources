package model.result;

import model.order.Order;
import parse.adapter.DateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Результат работы алгоритма для конкретного заказа</b>
 */
@XmlType(name = "Order")
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderResult {

    /**
     * ID заказа
     */
    @XmlAttribute(name = "order_id")
    private long orderId;

    /**
     * Время и дата начала выполнения заказа
     */
    @XmlAttribute(name = "order_start_date_time")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private LocalDateTime startTime;

    /**
     * Время и дата окончания выполнения заказа
     */
    @XmlAttribute(name = "order_end_date_time")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private LocalDateTime endTime;

    /**
     * Результаты работы по каждой детали
     */
    @XmlElement(name = "Product")
    private ArrayList<ProductResult> productResults;

    /**
     * Результат, к которому относится заказ
     */
    @XmlTransient
    private Result result;

    /**
     * Входные данные для заказа
     */
    @XmlTransient
    private Order order;

    public OrderResult() {

    }

    public OrderResult(long orderId, LocalDateTime startTime, LocalDateTime endTime, ArrayList<ProductResult> productResults) {
        this.orderId = orderId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.productResults = productResults;
    }

    public long getOrderId() {
        return orderId;
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
