package algorithm;

import algorithm.alternativeness.FromMapAlternativeElector;
import algorithm.candidates.CandidatesOwnAlgorithm;
import algorithm.model.order.Operation;
import algorithm.model.order.Order;
import algorithm.model.order.Product;
import algorithm.model.order.TechProcess;
import algorithm.operationchooser.FirstElementChooser;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputProductResult;
import parse.output.result.OutputResult;
import testing.RealityTester;
import util.Hash;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BackpackAlgorithm extends AbstractVariatorAlgorithm {

    protected HashMap<Long, Integer> productionPower;

    public BackpackAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, int variatorBudget) {
        super(inputProduction, inputOrders, startTime, variatorBudget);

        initProductionPower();
    }

    @Override
    public OutputResult start() throws Exception {


        return null;
    }

    protected void initProductionPower() {
        HashMap<Long, Integer> equalVariant = new HashMap<>();
        this.productionPower = new HashMap<>();

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
            CandidatesOwnAlgorithm algorithm = new CandidatesOwnAlgorithm(this.production, this.orders, this.startTime, new FirstElementChooser(), new FromMapAlternativeElector(equalVariant), equalVariant);
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
                                if (this.productionPower.containsKey(equipmentGroupId)) {
                                    this.productionPower.replace(equipmentGroupId, duration + this.productionPower.get(equipmentGroupId));
                                } else {
                                    this.productionPower.put(equipmentGroupId, duration);
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

    }
}
