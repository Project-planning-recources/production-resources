package algorithm.backpack;

import algorithm.AbstractVariatorAlgorithm;
import algorithm.FrontAlgorithmFactory;
import algorithm.candidates.CandidatesBaseAlgorithm;
import algorithm.candidates.CandidatesOwnAlgorithm;
import algorithm.model.order.Operation;
import algorithm.model.order.Order;
import algorithm.model.order.Product;
import algorithm.model.order.TechProcess;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputProductResult;
import parse.output.result.OutputResult;
import util.Criterion;
import util.Hash;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class BackpackAlgorithm extends AbstractVariatorAlgorithm {

    protected String frontAlgorithmType;

    protected int frontThreadsCount;
    protected HashMap<Long, Long> productionPower;

    protected HashMap<Long, HashMap<Long, Integer>> techProcessPower;

    public BackpackAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, String frontAlgorithmType, int frontThreadsCount, int variatorBudget) {
        super(inputProduction, inputOrders, startTime, variatorBudget);
        this.frontAlgorithmType = frontAlgorithmType;
        this.frontThreadsCount = frontThreadsCount;
        this.productionPower = initProductionPower();
        this.techProcessPower = initTechProcessPower();
    }

    @Override
    public OutputResult start() throws Exception {

        HashMap<Long, Integer> equalVariant = new HashMap<>();

        this.orders.forEach(order -> {
            for (Product product : order.getProducts()) {
                int make = product.getCount() / product.getTechProcesses().size();
                ArrayList<TechProcess> techProcesses = product.getTechProcesses();
                for (int i = 0; i < techProcesses.size(); i++) {
                    TechProcess techProcess = techProcesses.get(i);
                    if (i == 0) {
                        equalVariant.put(Hash.hash(order.getId(), product.getId(), techProcess.getId()), product.getCount() % techProcesses.size() + make);
                    } else {
                        equalVariant.put(Hash.hash(order.getId(), product.getId(), techProcess.getId()), make);
                    }
                }
            }
        });
        Criterion.getBackpackCriterion(this.orders, this.productionPower, techProcessPower, equalVariant);

        //todo: реализовать ранцевый алгоритм, убрать затычку
        return new CandidatesBaseAlgorithm(production, orders, startTime).start();
    }

    protected HashMap<Long, Long> initProductionPower() {
        HashMap<Long, Integer> equalVariant = new HashMap<>();
        HashMap<Long, Long> productionPower = new HashMap<>();

        this.orders.forEach(order -> {
            for (Product product : order.getProducts()) {
                int make = product.getCount() / product.getTechProcesses().size();
                ArrayList<TechProcess> techProcesses = product.getTechProcesses();
                for (int i = 0; i < techProcesses.size(); i++) {
                    TechProcess techProcess = techProcesses.get(i);
                    if (i == 0) {
                        equalVariant.put(Hash.hash(order.getId(), product.getId(), techProcess.getId()), product.getCount() % techProcesses.size() + make);
                    } else {
                        equalVariant.put(Hash.hash(order.getId(), product.getId(), techProcess.getId()), make);
                    }
                }
            }
        });

        if (checkVariantAvailability(equalVariant)) {
            CandidatesOwnAlgorithm algorithm = (CandidatesOwnAlgorithm) FrontAlgorithmFactory.getOwnFrontAlgorithm(this.production, this.orders, this.startTime, equalVariant, this.frontAlgorithmType, this.frontThreadsCount);
            try {
                OutputResult result = algorithm.start();
                HashMap<Long, Operation> operationsHashMap = algorithm.getOperationsHashMap();
                result.getOrderResults().forEach(outputOrderResult -> {
                    LocalDateTime deadline = null;
                    for (Order order : this.orders) {
                        if (order.getId() == outputOrderResult.getOrderId()) {
                            deadline = order.getDeadline();
                            break;
                        }
                    }

                    for (OutputProductResult outputProductResult : outputOrderResult.getProductResults()) {
                        if (outputProductResult.getEndTime().isBefore(deadline)) {
                            outputProductResult.getPerformedOperations().forEach(outputOperationResult -> {
                                int duration = operationsHashMap.get(outputOperationResult.getOperationId()).getDuration();
                                Long equipmentGroupId = this.production.getEquipmentGroupIdByEquipmentId(outputOperationResult.getEquipmentId());
                                if (productionPower.containsKey(equipmentGroupId)) {
                                    productionPower.replace(equipmentGroupId, duration + productionPower.get(equipmentGroupId));
                                } else {
                                    productionPower.put(equipmentGroupId, (long) duration);
                                }
                            });
                        }
                    }
                });

            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        } else {
            throw new RuntimeException("Неправильная генерация варианта с равномерным распределением альтернативностей");
        }

        return productionPower;
    }

    protected HashMap<Long, HashMap<Long, Integer>> initTechProcessPower() {
        HashMap<Long, HashMap<Long, Integer>> techProcessPower = new HashMap<>();
        for (Order order : orders) {
            for (Product product : order.getProducts()) {
                for (TechProcess techProcess : product.getTechProcesses()) {
                    long hash = Hash.hash(order.getId(), product.getId(), techProcess.getId());

                    HashMap<Long, Integer> equipPower = new HashMap<>();
                    for (Operation operation : techProcess.getOperations()) {
                        if(equipPower.containsKey(operation.getRequiredEquipment())) {
                            equipPower.replace(operation.getRequiredEquipment(), equipPower.get(operation.getRequiredEquipment()) + operation.getDuration());
                        } else {
                            equipPower.put(operation.getRequiredEquipment(), operation.getDuration());
                        }
                    }
                    techProcessPower.put(hash, equipPower);
                }
            }
        }

        return techProcessPower;
    }
}
