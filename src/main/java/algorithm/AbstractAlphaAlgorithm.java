package algorithm;

import algorithm.model.order.Order;
import algorithm.model.production.Production;
import util.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractAlphaAlgorithm implements Algorithm {
    /**
     * Предприятие
     */
    protected Production production;

    /**
     * Все заказы
     */
    protected ArrayList<Order> orders;

    /**
     * Время начала работы
     */
    protected LocalDateTime startTime;
    protected int startVariatorCount = 10;
    protected int variatorBudget = 100;

    protected ArrayList<Pair<HashMap<Long, Integer>, Double>> variation;

    protected HashMap<Long, Boolean> variantPairs;
}
