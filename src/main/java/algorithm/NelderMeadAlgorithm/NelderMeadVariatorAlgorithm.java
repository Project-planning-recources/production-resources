package algorithm.NelderMeadAlgorithm;

import algorithm.AbstractVariatorAlgorithm;
import algorithm.Algorithm;
import algorithm.FrontAlgorithmFactory;
import algorithm.alpha.InabilityGenerateException;
import algorithm.model.order.Order;
import algorithm.model.order.Product;
import algorithm.model.order.TechProcess;
import algorithm.model.production.Production;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import util.Criterion;
import util.Hash;
import util.Pair;
import util.Random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class NelderMeadVariatorAlgorithm extends AbstractVariatorAlgorithm {
    protected String frontAlgorithmType;
    protected int frontThreadsCount;
    long startVariationsCount;
    //protected int startVariatorCount;

    public NelderMeadVariatorAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime,
                                       String frontAlgorithmType, int frontThreadsCount, int variatorBudget) {
        super(inputProduction, inputOrders, startTime, variatorBudget);
        this.frontAlgorithmType = frontAlgorithmType;
        this.frontThreadsCount = frontThreadsCount;
    }
    public NelderMeadVariatorAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime,
                                       String frontAlgorithmType, int frontThreadsCount, int variatorBudget) {
        super(production, orders, startTime, variatorBudget);
        this.frontAlgorithmType = frontAlgorithmType;
        this.frontThreadsCount = frontThreadsCount;
    }

    @Override
    public OutputResult start() throws Exception {
        startVariationsCount = getNumberOfTechProcesses();

        for (int i = 0; i < startVariationsCount + 1; ) {
            HashMap<Long, Integer> randomVariant = generateRandomAlternativesDistribution();
            Algorithm algorithm;
            OutputResult result;
            //this.variation.add(new Pair<>(randomVariant, Criterion.getCriterion(this.orders, result)));
            if (checkVariantAvailability(randomVariant)) {

//                System.out.println("stert" + i);
                algorithm = FrontAlgorithmFactory.getOwnFrontAlgorithm(this.production, this.orders, this.startTime, randomVariant, this.frontAlgorithmType, this.frontThreadsCount);
                result = algorithm.start();
//                System.out.println("finish" + i);

                this.variation.add(new Pair<>(randomVariant, Criterion.getCriterion(this.orders, result)));
                System.out.println("Стартовая точка " + (i+1) + ":" + Criterion.getCriterion(this.orders, result));
                i++;
                //loading();
            }
            /*HashMap<Long, Integer> randomReflectedVariant = reflectVariant(randomVariant, middleVariant);
            if(i == startVariationsCount + 1) {
                break;
            }
            //HashMap<Long, Integer> variant = generateRandomAlternativesDistribution();
            if (checkVariantAvailability(randomReflectedVariant)) {

//                System.out.println("stert" + i);
                algorithm = FrontAlgorithmFactory.getOwnFrontAlgorithm(this.production, this.orders, this.startTime, randomReflectedVariant, this.frontAlgorithmType, this.frontThreadsCount);
                result = algorithm.start();
//                System.out.println("finish" + i);

                this.variation.add(new Pair<>(randomReflectedVariant, Criterion.getCriterion(this.orders, result)));
                System.out.println("Стартовая точка " + (i+1) + ":" + Criterion.getCriterion(this.orders, result));
                i++;
                //loading();
            }
             */

        }
        System.out.println();
        try {
            generateVariants();
        } catch (InabilityGenerateException ex) {
            System.out.println("Закончились варианты перебираемых пар");
            return FrontAlgorithmFactory.getOwnFrontAlgorithm(this.production, this.orders, this.startTime, returnRecordVariantPair(this.variation).getKey(), this.frontAlgorithmType, this.frontThreadsCount).start();
        }

        return FrontAlgorithmFactory.getOwnFrontAlgorithm(this.production, this.orders, this.startTime, returnRecordVariantPair(this.variation).getKey(), this.frontAlgorithmType, this.frontThreadsCount).start();

    }

    private long getNumberOfTechProcesses() {
        HashMap<Long, TechProcess> listProcesses= new HashMap<>();
        for(Order order : this.orders) {
            for(Product product : order.getProducts()) {
                for(TechProcess process : product.getTechProcesses()) {
                    if(!listProcesses.containsKey(process.getId())){
                        listProcesses.put(process.getId(), process);
                    }
                }
            }
        }
        return listProcesses.size();
    }

    protected void loading(int count) {
        System.out.println("Вычисление " + (count+1) + "/" + this.variatorBudget);
    }



    private void generateVariants() throws Exception {
        for (int i = 0; i < this.variatorBudget; i++) {
            this.variation.sort(Comparator.comparing(Pair::getValue));
            Pair<HashMap<Long, Integer>, Double> variantH = this.variation.get(this.variation.size() - 1);
            Pair<HashMap<Long, Integer>, Double> variantG = this.variation.get(this.variation.size() - 2);
            Pair<HashMap<Long, Integer>, Double> variantL = this.variation.get(0);
            loading(i);
            HashMap<Long, Integer> centralvariant = findCenterVariation();
            HashMap<Long, Integer> variantRWithoutCriterion = reflectVariant(variantH.getKey(), centralvariant);
            double criterionForVariantR = getCriterionForVariant(variantRWithoutCriterion);
            System.out.println("Критерий R = " + criterionForVariantR);
            Pair<HashMap<Long, Integer>, Double> variantR = new Pair<>(variantRWithoutCriterion, criterionForVariantR);
            if(criterionForVariantR < variantL.getValue()) {
                HashMap<Long, Integer> variantEWithoutCriterion = expandVariant(variantRWithoutCriterion, centralvariant);
                double criterionForVariantE = getCriterionForVariant(variantEWithoutCriterion);
                System.out.println("Критерий E = " + criterionForVariantE);
                if(criterionForVariantE < criterionForVariantR) {
                    if(checkNegativeAndDeal(variantEWithoutCriterion)) {
                        this.variation.remove(this.variation.size() - 1);
                        this.variation.add(new Pair<>(variantEWithoutCriterion, criterionForVariantE));
                        //variantH = new Pair<>(variantEWithoutCriterion, criterionForVariantE);
                    }
                    else {
                        System.out.println("Variant is not possible");
                    }
                }
                else {
                    if(checkNegativeAndDeal(variantRWithoutCriterion)) {
                        this.variation.remove(this.variation.size() - 1);
                        this.variation.add(variantR);
                        //variantH = variantR;
                    }
                    else{
                        System.out.println("Variant is not possible");
                    }
                }
            } else if (criterionForVariantR < variantG.getValue()) {
                this.variation.remove(this.variation.size() - 1);
                this.variation.add(variantR);
                //variantH = variantR;
            } else if(criterionForVariantR < variantH.getValue()){
                this.variation.remove(this.variation.size() - 1);
                this.variation.add(variantR);
                contraction(variantR, centralvariant);
            } else {
                contraction(variantH, centralvariant);
            }

            if(variantH == this.variation.get((this.variation.size() - 1))) {
                HashMap<Long, Integer> newVariation = generateRandomAlternativesDistribution();
                //this.variation.remove(this.variation.size() - 1);
                if(checkNegativeAndDeal(newVariation)) {
                    this.variation.remove(this.variation.size() - 1);
                    double neWCriterion = getCriterionForVariant(newVariation);
                    System.out.println("Критерий New = " + neWCriterion);
                    this.variation.add(new Pair<>(newVariation, neWCriterion));
                }
                else{
                    System.out.println("Variant is not possible");
                }

            }
        }

    }


    private HashMap<Long, Integer> findCenterVariation() {
        HashMap<Long, Double> alphaVariant = new HashMap<>();
        for (Order order : this.orders) {
            for (Product product : order.getProducts()) {
                double negativeBudget = 0;
                ArrayList<Pair<Long, Double>> positiveValues = new ArrayList<>();

                for (TechProcess techProcess : product.getTechProcesses()) {
                    long hash = Hash.hash(order.getId(), product.getId(), techProcess.getId());
                    double value = 0;
                    for(int i = 0; i < this.variation.size() - 1; i++) {
                        value +=this.variation.get(i).getKey().get(Hash.hash(order.getId(), product.getId(), techProcess.getId()));
                    }
                    value /= this.variation.size() - 1;

                    //double value = firstVariant.get(hash) * alpha + secondVariant.get(hash) * (1 - alpha);
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

    private HashMap<Long, Integer> getVariant(HashMap<Long, Integer> variantToReflect, HashMap<Long, Integer> centralVariant, double alpha) {
        //double alpha = 1;
        HashMap<Long, Double> alphaVariant = new HashMap<>();
        for (Order order : this.orders) {
            for (Product product : order.getProducts()) {
                double negativeBudget = 0;
                ArrayList<Pair<Long, Double>> positiveValues = new ArrayList<>();

                for (TechProcess techProcess : product.getTechProcesses()) {
                    long hash = Hash.hash(order.getId(), product.getId(), techProcess.getId());

                    double value = centralVariant.get(hash) * (1 + alpha) - variantToReflect.get(hash) * alpha;
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

    private HashMap<Long, Integer> reflectVariant(HashMap<Long, Integer> variantToReflect, HashMap<Long, Integer> centralVariant) {
        HashMap<Long, Integer> variant = getVariant(variantToReflect, centralVariant, 1);
        return variant;
    }
    private HashMap<Long, Integer> expandVariant(HashMap<Long, Integer> variantToExpand, HashMap<Long, Integer> centralVariant) {
        HashMap<Long, Integer> variant = getVariant(variantToExpand, centralVariant, -2);
        return variant;
    }

    private HashMap<Long, Integer> contractVariant(HashMap<Long, Integer> variantToContract, HashMap<Long, Integer> centralVariant) {
        HashMap<Long, Integer> variant = getVariant(variantToContract, centralVariant, -0.5);
        return variant;
    }

    private void contraction(Pair<HashMap<Long, Integer>, Double> variantH, HashMap<Long, Integer> centralVariant) throws Exception {
        HashMap<Long, Integer> variantSWithoutCriterion = contractVariant(variantH.getKey(), centralVariant);
        double criterionForVariantS = getCriterionForVariant(variantSWithoutCriterion);
        System.out.println("Критерий S = " + criterionForVariantS);
        if(criterionForVariantS < variantH.getValue()){
            if(checkNegativeAndDeal(variantSWithoutCriterion)) {
                this.variation.remove(this.variation.size() - 1);
                this.variation.add(new Pair<>(variantSWithoutCriterion, criterionForVariantS));
            }
            else{
                System.out.println("Variant is not possible");
            }
        }
    }

    protected double getCriterionForVariant(HashMap<Long, Integer> variant) throws Exception {
        Algorithm algorithm = FrontAlgorithmFactory.getOwnFrontAlgorithm(this.production, this.orders, this.startTime, variant, this.frontAlgorithmType, this.frontThreadsCount);
        OutputResult result = algorithm.start();

        return Criterion.getCriterion(this.orders, result);
    }
}

