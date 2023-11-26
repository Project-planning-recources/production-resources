package algorithm.model.production;

import algorithm.model.order.Operation;
import algorithm.model.result.OperationResult;
import parse.input.production.InputEquipment;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <b>Класс для Алгоритма</b>
 * <b>Данные о конкретной единице оборудования</b>
 */
public class Equipment implements Serializable {

    /**
     * ID единицы оборудования
     */
    private long id;

    /**
     * Название оборудования
     */
    private String name;

    /**
     * Используется ли данное оборудование в текущий момент (пока не уверен, нужно ли это поле)
     */
    private boolean isUsing;

    private final LinkedList<OperationResult> performedOperations;

    public Equipment() {
        performedOperations = new LinkedList<>();
    }

    public Equipment(InputEquipment inputEquipment) {
        this.id = inputEquipment.getId();
        this.name = inputEquipment.getName();
        performedOperations = new LinkedList<>();
    }

    public Equipment(long id, String name, boolean isUsing) {
        this.id = id;
        this.name = name;
        this.isUsing = isUsing;
        performedOperations = new LinkedList<>();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isUsing() {
        return isUsing;
    }

    public void setUsing(boolean using) {
        isUsing = using;
    }

    public void addOperation(OperationResult operation) {
        performedOperations.add(operation);
    }

    public boolean isBusy(LocalDateTime date) {
        return Objects.nonNull(getBusyTime(date));

    }

    private LocalDateTime getBusyTime(LocalDateTime date) {
        return performedOperations
                .stream()
                .filter(operation -> Objects.nonNull(operation.getEndTime()))
                .filter(operation -> operation.getEndTime().isAfter(date))
                .map(OperationResult::getEndTime)
                .collect(Collectors.toList())
                .stream()
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

}
