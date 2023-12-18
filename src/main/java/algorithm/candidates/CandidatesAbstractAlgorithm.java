package algorithm.candidates;

import algorithm.AbstractAlgorithm;
import algorithm.alternativeness.AlternativeElector;
import algorithm.model.order.Operation;
import algorithm.model.order.Order;
import algorithm.model.production.Equipment;
import algorithm.model.production.Production;
import algorithm.model.result.OperationResult;
import algorithm.model.result.OrderResult;
import algorithm.model.result.ProductResult;
import algorithm.operationchooser.OperationChooser;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public abstract class CandidatesAbstractAlgorithm extends AbstractAlgorithm {


    protected HashMap<Long, Operation> allOperations;
    protected ArrayList<OperationResult> waitingOperations;
    protected ArrayList<OperationResult> ongoingOperations;

    public CandidatesAbstractAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime, OperationChooser operationChooser, AlternativeElector alternativeElector) {
        super(production, orders, startTime);
        this.operationChooser = operationChooser;
        this.alternativeElector = alternativeElector;
        this.ongoingOperations = new ArrayList<>();
        this.waitingOperations = new ArrayList<>();
        initOperationsHashMap();
        initEquipmentHashMap();
        initTimeline();
        initResult();
    }

    public CandidatesAbstractAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, OperationChooser operationChooser, AlternativeElector alternativeElector) {
        super(inputProduction, inputOrders, startTime);
        this.operationChooser = operationChooser;
        this.alternativeElector = alternativeElector;
        this.ongoingOperations = new ArrayList<>();
        this.waitingOperations = new ArrayList<>();
        initOperationsHashMap();
        initEquipmentHashMap();
        initTimeline();
        initResult();
    }

    protected void initOperationsHashMap () {
        this.allOperations = new HashMap<>();

        this.orders.forEach(order -> {
            order.getProducts().forEach(product -> {
                product.getTechProcesses().forEach(techProcess -> {
                    techProcess.getOperations().forEach(operation -> {
                        allOperations.put(operation.getId(), operation);
                    });
                });
            });
        });
    }

    public HashMap<Long, Operation> getOperationsHashMap() {
        return this.allOperations;
    }

    @Override
    protected void startOperations(LocalDateTime timeTick){
        ArrayList<OperationResult> candidates = new ArrayList<>();
        this.waitingOperations.forEach(waitingOperation -> {
            try {
                if(this.production.isOperationCanBePerformed(waitingOperation.getEquipmentGroupId())) {
                    candidates.add(waitingOperation);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        while(!candidates.isEmpty()) {
            OperationResult choose = operationChooser.choose(candidates);

            choose.setStartTime(timeTick);
            if(choose.getPrevOperationId() == 0) {
                choose.getProductResult().setStartTime(timeTick);
            }
            LocalDateTime endTime = addOperationTimeToTimeline(timeTick, choose.getDuration());
            choose.setEndTime(endTime);

            Equipment equipment = null;
            try {
                equipment = production.getEquipmentForOperation(choose);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            equipment.setUsing(true);
            choose.setEquipmentId(equipment.getId());

            this.waitingOperations.remove(choose);
            this.ongoingOperations.add(choose);

            candidates.clear();
            this.waitingOperations.forEach(waitingOperation -> {
                try {
                    if(this.production.isOperationCanBePerformed(waitingOperation.getEquipmentGroupId())) {
                        candidates.add(waitingOperation);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Override
    protected void tickOfTime(LocalDateTime timeTick) throws Exception {
        /**
         * Добавляем операции заказа, для которого наступило время раннего начала
         */
        this.orders.forEach(order -> {
            if(order.getStartTime().isEqual(timeTick)) {
                addNewOrderOperations(order);
            }
        });


        if(isWeekend(timeTick)) {
            moveTimeTickFromWeekend(timeTick);
        } else {
            /**
             * Обрабатываем операции, которые завершились в данный момент
             */
            ArrayList<OperationResult> finishOperations = new ArrayList<>();
            this.ongoingOperations.forEach(ongoingOperation -> {
                if(ongoingOperation.getEndTime().isEqual(timeTick)) {
                    finishOperations.add(ongoingOperation);
                }
            });
            finishOperations.forEach(finishOperation -> {
                releaseEquipmentAndNextOperation(finishOperation, timeTick);
            });


            /**
             * Начинаем выполнять новые операции, если это возможно
             */
            startOperations(timeTick);
        }

    }

    protected void releaseEquipmentAndNextOperation(OperationResult ongoingOperation, LocalDateTime timeTick) {

        if(ongoingOperation.getNextOperation() != null) {
            this.waitingOperations.add(ongoingOperation.getNextOperation());
        } else {
            ongoingOperation.getProductResult().setEndTime(timeTick);
        }
        this.ongoingOperations.remove(ongoingOperation);
        this.allEquipment.get(ongoingOperation.getEquipmentId()).setUsing(false);

    }

    protected void addNewOrderOperations(Order order) {

        ArrayList<ProductResult> productResults = new ArrayList<>();
        OrderResult orderResult = new OrderResult(order.getId(), null, null, productResults);
        orderResult.setResult(this.result);
        order.getProducts().forEach(product -> {

            for (int i = 0; i < product.getCount(); i++) {

                /**
                 * Выбираем техпроцесс
                 */
                long techProcessId = this.alternativeElector.chooseTechProcess(product);
                LinkedList<Operation> operations = product.getTechProcessByTechProcessId(techProcessId).getOperations();

                LinkedList<OperationResult> operationResults = new LinkedList<>();
                ProductResult productResult = new ProductResult(this.concreteProductId++, product.getId(), techProcessId,
                        null, null, operationResults, orderResult);
                OperationResult prevOperation = null;
                for (int j = 0; j < operations.size(); j++) {
                    Operation operation = operations.get(j);
                    OperationResult operationResult = new OperationResult(operation.getId(), operation.getPrevOperationId(),
                            operation.getNextOperationId(), operation.getDuration(), operation.getRequiredEquipment(),
                            0, null, null, productResult);

                    if(prevOperation != null) {
                        prevOperation.setNextOperation(operationResult);
                    }
                    prevOperation = operationResult;

                    if(j == 0) {
                        this.waitingOperations.add(operationResult);
                    }
                    operationResults.add(operationResult);
                }



                productResults.add(productResult);
            }
        });


        this.result.getOrderResults().add(orderResult);
    }
}
