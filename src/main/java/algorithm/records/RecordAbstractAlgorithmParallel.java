package algorithm.records;

import algorithm.AbstractAlgorithm;
import algorithm.alternativeness.AlternativeElector;
import algorithm.model.order.Order;
import algorithm.model.order.TechProcess;
import algorithm.model.production.Equipment;
import algorithm.model.production.Production;
import algorithm.model.result.*;
import algorithm.operationchooser.OperationChooser;
import algorithm.records.record.CompositeRecordParallel;
import algorithm.records.util.EquipmentFinder;
import org.apache.directory.server.core.avltree.AvlTree;
import org.apache.directory.server.core.avltree.AvlTreeImpl;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import util.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public abstract class RecordAbstractAlgorithmParallel extends AbstractAlgorithm {

    protected CompositeRecordParallel record;
    protected int threadCount;

    public RecordAbstractAlgorithmParallel(Production production, ArrayList<Order> orders, LocalDateTime startTime, OperationChooser operationChooser, AlternativeElector alternativeElector, int threadCount) {
        super(production, orders, startTime);
        EquipmentFinder.operationsCount = Data.getOperationsCount(this.orders);
        this.threadCount = threadCount;
        this.operationChooser = operationChooser;
        this.alternativeElector = alternativeElector;
        initEquipmentHashMap();
        initTimeline();
        ArrayList<AvlTree<OperationResult>> trees = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            trees.add(new AvlTreeImpl<>(OperationResult::compareTo));
        }
        this.record = new CompositeRecordParallel(production.getEquipmentGroups(), trees);
        initResult();
    }

    public RecordAbstractAlgorithmParallel(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, OperationChooser operationChooser, AlternativeElector alternativeElector,  int threadCount) {
        super(inputProduction, inputOrders, startTime);
        EquipmentFinder.operationsCount = Data.getOperationsCount(this.orders);
        this.threadCount = threadCount;
        this.operationChooser = operationChooser;
        this.alternativeElector = alternativeElector;
        initEquipmentHashMap();
        initTimeline();
        ArrayList<AvlTree<OperationResult>> trees = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            trees.add(new AvlTreeImpl<>(OperationResult::compareTo));
        }
        this.record = new CompositeRecordParallel(production.getEquipmentGroups(), trees);
        initResult();
    }

    protected void initResult() {
        this.result = new Result(null, null, new ArrayList<>());
        ArrayList<OrderResult> orderResults = new ArrayList<>();
        AtomicLong addingOrder = new AtomicLong(0);
        this.orders.forEach(order -> {

            ArrayList<ProductResult> productResults = new ArrayList<>();
            OrderResult orderResult = new OrderResult(order.getId(), order.getStartTime(), null, null, productResults);
            orderResult.setResult(this.result);

            order.getProducts().forEach(product -> {

                for (int i = 0; i < product.getCount(); i++) {
                    long techProcessId = chooseAlternativeness(this.concreteProductId, product);
                    TechProcess techProcess = product.getTechProcessByTechProcessId(techProcessId);
                    LinkedList<OperationResult> operationResults = new LinkedList<>();
                    ProductResult productResult = new ProductResult(this.concreteProductId++, product.getId(),
                            techProcessId, null, null, operationResults, orderResult);

                    AtomicLong orderInTechProcess = new AtomicLong(1);

                    AtomicReference<OperationResult> prevOperation = new AtomicReference<>();

                    techProcess.getOperations().forEach(operation -> {

                        OperationPriorities priorities = new OperationPriorities(operation.getId(), operation.getDuration(),
                                orderInTechProcess.get(), order.getDeadline(), addingOrder.get());
                        OperationResult operationResult = new OperationResult(operation.getId(), operation.getPrevOperationId(),
                                operation.getNextOperationId(), operation.getRequiredEquipment(),
                                0, order.getStartTime(), null, null, productResult, priorities);
                        operationResult.setPrevOperation(prevOperation.get());
                        operationResults.add(operationResult);
                        orderInTechProcess.getAndIncrement();
                        addingOrder.getAndIncrement();
                        prevOperation.set(operationResult);
                    });

                    productResults.add(productResult);
                }
            });

            orderResults.add(orderResult);
        });

        this.result.getOrderResults().addAll(orderResults);
    }

    @Override
    protected void tickOfTime(LocalDateTime timeTick) throws Exception {
        if(isWeekend(timeTick)) {
            moveTimeTickFromWeekend(timeTick);
        } else {

            List<OperationResult> readyOperations = fillReadyOperationsByTimeTick(timeTick);
            record.fillTrees(readyOperations, timeTick);
            //Начинаем выполнять новые операции, если это возможно
            startOperations(timeTick);
        }
    }

    @Override
    protected void startOperations(LocalDateTime timeTick) {
        OperationResult choose;
        while (true) {
            //System.out.println("Из AbstractAlgorithm.startOperations");
            choose = record.getRecord(timeTick);
            if (Objects.isNull(choose)) {
                break;
            }

            choose.setStartTime(timeTick);
            LocalDateTime endTime = addOperationTimeToTimeline(timeTick, choose.getOperationPriorities().getDuration());
            choose.setEndTime(endTime);
            choose.setDone(true);

            if (choose.getPrevOperationId() == 0) {
                choose.getProductResult().setStartTime(timeTick);
            }

            Equipment equipment = allEquipment.get(choose.getEquipmentId());
            equipment.setIsBusyTo(endTime);

            if(choose.getNextOperationId() == 0) {
                choose.getProductResult().setEndTime(endTime);
            }
        }
    }

    @Override
    protected void addNewOrderOperations(Order order) {

    }

    protected List<OperationResult> fillReadyOperationsByTimeTick(LocalDateTime timeTick) {
        AvlTree<OperationResult> readyOperations = new AvlTreeImpl<>(OperationResult::compareTo);
        this.result.getOrderResults()
                .stream()
                .filter(order -> !timeTick.isBefore(order.getEarlyStartTime()) && !order.isInProgress())
                .forEach(order -> {
                    order.getProductResults().forEach(product -> {
                        product.getPerformedOperations().forEach(readyOperations::insert);
                    });

                    order.setInProgress(true);
                });

        return readyOperations.getKeys();
    }



}
