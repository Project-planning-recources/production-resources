package algorithm.alpha;

import algorithm.AbstractVariatorAlgorithm;
import algorithm.Algorithm;
import algorithm.FrontAlgorithmFactory;
import algorithm.model.order.Order;
import algorithm.model.order.Product;
import algorithm.model.order.TechProcess;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import util.Criterion;
import util.Hash;
import util.Pair;
import util.Random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class AlphaVariatorAlgorithm extends AbstractVariatorAlgorithm {

    protected String frontAlgorithmType;
    protected int frontThreadsCount;
    protected int startVariatorCount;
    public AlphaVariatorAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, String frontAlgorithmType, int frontThreadsCount, int startVariatorCount, int variatorBudget) {
        super(inputProduction, inputOrders, startTime, variatorBudget);
        this.frontAlgorithmType = frontAlgorithmType;
        this.frontThreadsCount = frontThreadsCount;
        this.startVariatorCount = startVariatorCount;
    }

    @Override
    public OutputResult start() throws Exception {



        for (int i = 0; i < this.startVariatorCount; i++) {
            HashMap<Long, Integer> variant = generateRandomAlternativesDistribution();
            if (checkVariantAvailability(variant)) {

                Algorithm algorithm = FrontAlgorithmFactory.getOwnFrontAlgorithm(this.production, this.orders, this.startTime, variant, this.frontAlgorithmType, this.frontThreadsCount);
                OutputResult result = algorithm.start();

                this.variation.add(new Pair<>(variant, Criterion.getCriterion(this.orders, result)));
                loading();
            } else {
                i--;
            }

        }

        for (int i = this.startVariatorCount; i <= this.variatorBudget;) {
            loading();
            int addCount = generateAndAddNewVariants();
            i += addCount;
        }

        return FrontAlgorithmFactory.getOwnFrontAlgorithm(this.production, this.orders, this.startTime, returnRecordVariant(this.variation).getKey(), this.frontAlgorithmType, this.frontThreadsCount).start();
    }

    protected void loading() {
        System.out.println("Вычисление " + (this.variation.size() - 1) + "/" + this.variatorBudget);
    }

    protected double getCriterionForVariant(HashMap<Long, Integer> variant) throws Exception {
        Algorithm algorithm = FrontAlgorithmFactory.getOwnFrontAlgorithm(this.production, this.orders, this.startTime, variant, this.frontAlgorithmType, this.frontThreadsCount);
        OutputResult result = algorithm.start();

        return Criterion.getCriterion(this.orders, result);
    }

    protected boolean putPairIfAbsent(long pairsHash) {
        if (!this.variantPairs.containsKey(pairsHash)) {
            this.variantPairs.put(pairsHash, true);
            return true;
        }
        return false;
    }

    protected int generateAndAddNewVariants() throws Exception {

        boolean generationFlag = true;
        int addCount = 0;

        while (generationFlag) {
            HashMap<Long, Integer> firstVariant = null;
            HashMap<Long, Integer> secondVariant = null;

            long pairsHash = 0;
            double criterionForFirstVariant = 0;
            double criterionForSecondVariant = 0;

            boolean pairsFlag = true;
            while (pairsFlag) {

                Pair<HashMap<Long, Integer>, Double> firstPair = this.variation.get(Random.randomInt(this.variation.size()));
                firstVariant = firstPair.getKey();
                criterionForFirstVariant = firstPair.getValue();
                do {
                    Pair<HashMap<Long, Integer>, Double> secondPair = this.variation.get(Random.randomInt(this.variation.size()));
                    secondVariant = secondPair.getKey();
                    criterionForSecondVariant = secondPair.getValue();
                } while (firstVariant == secondVariant);

                pairsHash = Hash.hash((long) firstVariant.hashCode(), (long) secondVariant.hashCode());
//                System.out.println(firstVariant + " " + secondVariant + pairsHash);

                pairsFlag = !putPairIfAbsent(pairsHash);
            }

//        System.out.println(firstVariant);
//        System.out.println(secondVariant);


            HashMap<Long, Integer> betweenVariant = null;
            HashMap<Long, Integer> beyondVariant = null;
            if (criterionForFirstVariant < criterionForSecondVariant) {
//            System.out.println("1 < 2");
                betweenVariant = generateVariantFromTwo(firstVariant, secondVariant, 0.5);
                beyondVariant = generateVariantFromTwo(firstVariant, secondVariant, 1.2);
//                betweenVariant = generateVariantFromTwo(firstVariant, secondVariant, 0.8);
//                beyondVariant = generateVariantFromTwo(firstVariant, secondVariant, 1.2);
            } else if (criterionForFirstVariant > criterionForSecondVariant) {
//            System.out.println("1 > 2");
                betweenVariant = generateVariantFromTwo(firstVariant, secondVariant, 0.5);
                beyondVariant = generateVariantFromTwo(firstVariant, secondVariant, -0.2);

//                betweenVariant = generateVariantFromTwo(firstVariant, secondVariant, 0.2);
//                beyondVariant = generateVariantFromTwo(firstVariant, secondVariant, -0.2);
            } else {
                betweenVariant = generateVariantFromTwo(firstVariant, secondVariant, 0.5);
            }


            if(addVariantIfAbsent(betweenVariant)) {
                addCount++;
            }

            if(beyondVariant != null && addVariantIfAbsent(beyondVariant)) {
                addCount++;
            }

            if (addCount != 0) {
                generationFlag = false;
            }
        }
        return addCount;
    }

    protected boolean addVariantIfAbsent(HashMap<Long, Integer> variant) throws Exception {
        if(checkVariantAvailability(variant)) {
            double criterionForVariant = getCriterionForVariant(variant);
            this.variation.add(new Pair<>(variant, criterionForVariant));
            return true;
        }
        return false;
    }


    protected HashMap<Long, Integer> generateVariantFromTwo(HashMap<Long, Integer> firstVariant,
                                                          HashMap<Long, Integer> secondVariant,
                                                          double alpha) {

        HashMap<Long, Double> alphaVariant = new HashMap<>();
        for (Order order : this.orders) {
            for (Product product : order.getProducts()) {
                double negativeBudget = 0;
                ArrayList<Pair<Long, Double>> positiveValues = new ArrayList<>();

                for (TechProcess techProcess : product.getTechProcesses()) {
                    long hash = Hash.hash(order.getId(), product.getId(), techProcess.getId());

                    double value = firstVariant.get(hash) * alpha + secondVariant.get(hash) * (1 - alpha);
                    if (value < 0) {
                        alphaVariant.put(hash, 0.0);
                        negativeBudget -= value;
                    } else {
                        positiveValues.add(new Pair<>(hash, value));
                    }

                }

                positiveValues.sort((first, second) -> (int) (first.getValue() - second.getValue()));

                int count = positiveValues.size();
                double avg = negativeBudget / count;
                int budget = product.getCount();

                for (Pair<Long, Double> p :
                        positiveValues) {
                    if (p.getValue() < avg) {
                        negativeBudget -= p.getValue();
                        count--;
                        avg = negativeBudget / count;
                        p.setValue(0.0);
                    } else {
                        p.setValue(p.getValue() - avg);
                        ;
                        int round = (int) Math.round(p.getValue());
                        budget -= round;
                        negativeBudget -= avg;
                    }
                }

                //Если остались нераспределенные или лишние детали
                positiveValues.get(positiveValues.size() - 1).setValue(positiveValues.get(positiveValues.size() - 1).getValue() + budget);


//                System.out.println("afterpairs " + positiveValues + " " + negativeBudget + " " + product.getCount());

                for (Pair<Long, Double> pair : positiveValues) {
                    int round = (int) Math.round(pair.getValue());
                    alphaVariant.put(pair.getKey(), (double) round);
                }

            }
        }

        HashMap<Long, Integer> readyAlphaVariant = new HashMap<>();
        alphaVariant.forEach((k, v) -> readyAlphaVariant.put(k, v.intValue()));
        return readyAlphaVariant;
    }



}
