package parse.output.result;

import algorithm.model.result.OrderResult;
import parse.adapter.DateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <b>Класс для IO</b>
 * <b>Результат работы алгоритма для конкретного заказа</b>
 */
@XmlType(name = "Order")
@XmlAccessorType(XmlAccessType.FIELD)
public class OutputOrderResult {

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
    private ArrayList<OutputProductResult> outputProductResults;

    public OutputOrderResult() {

    }


    public OutputOrderResult(OrderResult orderResult, HashMap<Long, ArrayList<OutputOperationResult>> performedOperationsOnEquipments) {
        this.orderId = orderResult.getOrderId();
        this.startTime = orderResult.getStartTime();
        this.endTime = orderResult.getEndTime();

        ArrayList<OutputProductResult> outputProductResults = new ArrayList<>();
        orderResult.getProductResults().forEach(productResult -> {
            outputProductResults.add(new OutputProductResult(productResult, performedOperationsOnEquipments));
        });
        this.outputProductResults = outputProductResults;
    }

    public OutputOrderResult(long orderId, LocalDateTime startTime, LocalDateTime endTime, ArrayList<OutputProductResult> outputProductResults) {
        this.orderId = orderId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.outputProductResults = outputProductResults;
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

    public ArrayList<OutputProductResult> getProductResults() {
        return outputProductResults;
    }

    @Override
    public String toString() {
        return "OrderResult{" +
                "orderId=" + orderId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", productResults=" + outputProductResults +
                '}';
    }
}
