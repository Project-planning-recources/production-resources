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
import util.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class BackpackAlgorithm extends AbstractVariatorAlgorithm {

    static class BackpackException extends RuntimeException{
        public BackpackException() {
            super();
        }
    }

    protected String frontAlgorithmType;

    protected int frontThreadsCount;

    protected int repeatBudget;
    protected HashMap<Long, Long> productionPower;

    protected HashMap<Long, HashMap<Long, Integer>> techProcessPowerRequirement;

    protected ArrayList<Pair<HashMap<Long, Integer>, Double>> backpackVariation;

    public BackpackAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, String frontAlgorithmType, int frontThreadsCount, int variatorBudget, int repeatBudget) {
        super(inputProduction, inputOrders, startTime, variatorBudget);
        this.frontAlgorithmType = frontAlgorithmType;
        this.frontThreadsCount = frontThreadsCount;
        this.backpackVariation = new ArrayList<>();
        this.techProcessPowerRequirement = initTechProcessPowerRequirement();
        HashMap<Long, Integer> equalVariant = getEqualVariant();
        this.productionPower = getProductionPowerAndRememberCriterion(equalVariant);
        this.repeatBudget = repeatBudget;

    }

    @Override
    public OutputResult start() throws Exception {
        for (int i = 0; i < this.variatorBudget; i++) {
            HashMap<Long, Integer> randomVariant = this.generateRandomAlternativesDistribution();
            if(checkVariantAvailability(randomVariant)) {
                this.backpackVariation.add(new Pair<>(randomVariant, Criterion.getBackpackCriterion(this.orders, this.productionPower, this.techProcessPowerRequirement, randomVariant)));
            } else {
                i--;
            }
        }

        for (int i = 0; i < this.repeatBudget; i++) {

            backpackVariation.sort((o1, o2) -> {
                if(o1.getValue() - o2.getValue() > 0) {
                    return 1;
                } else if(o1.getValue() - o2.getValue() < 0) {
                    return -1;
                } else {
                    return 0;
                }
            });

            try {
                this.productionPower = getProductionPowerAndRememberCriterion(null);
            } catch (BackpackException e) {
                break;
            }

            for (int j = 0; j < this.backpackVariation.size(); j++) {
                Pair<HashMap<Long, Integer>, Double> pair = this.backpackVariation.get(j);
                pair.setValue(Criterion.getBackpackCriterion(this.orders, this.productionPower, this.techProcessPowerRequirement, pair.getKey()));
            }
        }

        return FrontAlgorithmFactory.getOwnFrontAlgorithm(this.production, this.orders, this.startTime, returnRecordVariantPair(this.variation).getKey(), this.frontAlgorithmType, this.frontThreadsCount).start();
    }

    protected HashMap<Long, Integer> getEqualVariant() {
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
        return equalVariant;
    }

    protected HashMap<Long, Long> getProductionPowerAndRememberCriterion(HashMap<Long, Integer> useVariant) {
        HashMap<Long, Long> productionPower = new HashMap<>();

        int variantIndex = 0;
        HashMap<Long, Integer> variant = useVariant;
        while (true) {
            if(useVariant != null) {
                variantIndex--;
            } else {
                variant = this.backpackVariation.get(variantIndex).getKey();
            }
            if (checkVariantAvailability(variant)) {
                CandidatesOwnAlgorithm algorithm = (CandidatesOwnAlgorithm) FrontAlgorithmFactory.getOwnFrontAlgorithm(this.production, this.orders, this.startTime, variant, this.frontAlgorithmType, this.frontThreadsCount);
                try {
                    OutputResult result = algorithm.start();
                    this.variation.add(new Pair<>(variant, Criterion.getCriterion(this.orders, result)));
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
                this.production.getEquipmentGroups().forEach(equipmentGroup -> productionPower.putIfAbsent(equipmentGroup.getId(), 0L));
                return productionPower;
            } else {
                if (variantIndex == this.backpackVariation.size() - 1) {
                    throw new BackpackException();
                }
                variantIndex++;
            }
        }
    }

    protected HashMap<Long, HashMap<Long, Integer>> initTechProcessPowerRequirement() {
        HashMap<Long, HashMap<Long, Integer>> techProcessPowerRequirement = new HashMap<>();
        for (Order order : orders) {
            for (Product product : order.getProducts()) {
                for (TechProcess techProcess : product.getTechProcesses()) {
                    long hash = Hash.hash(order.getId(), product.getId(), techProcess.getId());

                    HashMap<Long, Integer> equipPowerRequirement = new HashMap<>();
                    for (Operation operation : techProcess.getOperations()) {
                        if(equipPowerRequirement.containsKey(operation.getRequiredEquipment())) {
                            equipPowerRequirement.replace(operation.getRequiredEquipment(), equipPowerRequirement.get(operation.getRequiredEquipment()) + operation.getDuration());
                        } else {
                            equipPowerRequirement.put(operation.getRequiredEquipment(), operation.getDuration());
                        }
                    }
                    techProcessPowerRequirement.put(hash, equipPowerRequirement);
                }
            }
        }
        return techProcessPowerRequirement;
    }
}
