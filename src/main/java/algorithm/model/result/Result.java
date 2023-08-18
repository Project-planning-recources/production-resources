package algorithm.model.result;

import parse.adapter.DateAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Класс для Алгоритма</b>
 * <b>Результат работы алгоритма</b>
 * <p>Содержит информацию о времени работы и распределении всех заказанных деталей по производству</p>
 */
public class Result {

    /**
     * Время и дата начала всей работы
     */
    private LocalDateTime allStartTime;

    /**
     * Время и дата окончания всей работы
     */
    private LocalDateTime allEndTime;

    /**
     * Результаты работы по каждому заказу
     */
    private ArrayList<OrderResult> orderResults;

    public Result() {

    }

    public Result(LocalDateTime allStartTime, LocalDateTime allEndTime, ArrayList<OrderResult> orderResults) {
        this.allStartTime = allStartTime;
        this.allEndTime = allEndTime;
        this.orderResults = orderResults;
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

    public ArrayList<OrderResult> getOrderResults() {
        return orderResults;
    }

    @Override
    public String toString() {
        return "Result{" +
                "allStartTime=" + allStartTime +
                ", allEndTime=" + allEndTime +
                ", orderResults=" + orderResults +
                '}';
    }
}
