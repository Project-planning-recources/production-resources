package algorithm;

import algorithm.model.order.Order;
import algorithm.model.order.Product;
import algorithm.model.order.TechProcess;
import algorithm.model.production.Production;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import util.Data;
import util.Hash;
import util.Pair;
import util.Random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class AbstractVariatorAlgorithm implements Algorithm {
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

    public AbstractVariatorAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, int variatorBudget) {

        this.production = new Production(inputProduction);
        ArrayList<Order> orders = new ArrayList<>();
        inputOrders.forEach(inputOrder -> {
            orders.add(new Order(inputOrder));
        });
        this.orders = orders;

        this.startTime = startTime;
        this.variatorBudget = variatorBudget;

        this.variation = new ArrayList<>();
        this.variantPairs = new HashMap<>();
    }

    public AbstractVariatorAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime, int variatorBudget) {
        this.production = production;
        this.orders = orders;

        this.startTime = startTime;
        this.variatorBudget = variatorBudget;

        this.variation = new ArrayList<>();
        this.variantPairs = new HashMap<>();
    }

    protected HashMap<Long, Integer> generateRandomAlternativesDistribution() {
        HashMap<Long, Integer> variant = new HashMap<>();

        this.orders.forEach(order -> {
            order.getProducts().forEach(product -> {
                ArrayList<Long> techProcesses = new ArrayList<>(product.getTechProcesses().size());
                product.getTechProcesses().forEach(techProcess -> techProcesses.add(techProcess.getId()));

                Collections.shuffle(techProcesses);

                int remain = product.getCount();
                for (int i = 0; i < techProcesses.size(); i++) {
                    if (i == techProcesses.size() - 1) {
                        variant.put(Hash.hash(order.getId(), product.getId(), techProcesses.get(i)), remain);
                    } else {
                        int deal = Random.randomInt(remain + 1);
                        variant.put(Hash.hash(order.getId(), product.getId(), techProcesses.get(i)), deal);
                        remain -= deal;
                    }
                }
            });
        });

        return variant;
    }

    protected Pair<HashMap<Long, Integer>, Double> returnRecordVariantPair(ArrayList<Pair<HashMap<Long, Integer>, Double>> variation) throws Exception {
        Pair<HashMap<Long, Integer>, Double> recordPair = null;
        double recordCriterion = Double.MAX_VALUE;


        for (Pair<HashMap<Long, Integer>, Double> variantPair : variation) {
            if (variantPair.getValue() < recordCriterion) {
                recordPair = variantPair;
                recordCriterion = variantPair.getValue();
            }
        }

        if (recordPair == null) {
            throw new Exception("Unreachable code");
        }

        return recordPair;
    }

    protected Boolean checkNegativeAndDeal(HashMap<Long, Integer> variant) {
        for (Order order : this.orders) {
            for (Product product : order.getProducts()) {
                int deal = product.getCount();
                for (TechProcess techProcess : product.getTechProcesses()) {
                    int value = variant.get(Hash.hash(order.getId(), product.getId(), techProcess.getId()));
                    if (value < 0) {
                        System.out.println("||||||||||||Значение < 0   " + value);
                        return false;
                    }
                    deal -= value;
                }
                if (deal != 0) {
                    System.out.println("||||||||||||Несоответствие количества деталей   " + deal);
                    return false;
                }
            }
        }
        return true;
    }

    protected Boolean checkExistence(HashMap<Long, Integer> variant) {
        for (Pair<HashMap<Long, Integer>, Double> v : this.variation) {
            if (v.getKey().equals(variant)) {
                return false;
            }
        }
        return true;
    }
    protected Boolean checkVariantAvailability(HashMap<Long, Integer> variant) {
        return checkNegativeAndDeal(variant) && checkExistence(variant);
    }
}
