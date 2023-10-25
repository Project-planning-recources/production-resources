package algorithm;

import algorithm.operationchooser.FirstElementChooser;
import algorithm.parallel.ParallelMain;
import algorithm.parallel.ParallelSolver;
import parse.input.order.InputOrder;
import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import testing.ComparisonTester;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

public class AlternativenessOwnAlgorithm implements Algorithm {

    private InputProduction inputProduction;
    private ArrayList<InputOrder> inputOrders;
    private LocalDateTime startTime;

    private int threadsNum = 1;
    private int startVariatorCount = 10;
    private int variatorBudget = 100;

    public AlternativenessOwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime) {
        this.inputProduction = inputProduction;
        this.inputOrders = inputOrders;
        this.startTime = startTime;
    }
    public AlternativenessOwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, int threadsNum) {
        this.inputProduction = inputProduction;
        this.inputOrders = inputOrders;
        this.startTime = startTime;
        this.threadsNum = threadsNum;
    }

    @Override
    public OutputResult start() throws Exception {
        if(this.threadsNum == 1) {
            return startConsistentAlg();
        } else {
            return startParallelAlg();
        }
    }

    private ArrayList<HashMap<Long, HashMap<Long, HashMap<Long, Integer>>>> variation;
    private OutputResult startConsistentAlg() {
        this.variation = new ArrayList<>();

        for (int i = 0; i < this.startVariatorCount; i++) {
            HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> variant = generateRandomAlternativesDistribution();
            if(checkVariantAvailability(variant)) {
                this.variation.add(variant);
            } else {
                i--;
            }
        }

        while(this.variation.size() < this.variatorBudget) {
            generateAndAddNewVariants();
        }




        return null;
    }

    private HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> generateRandomAlternativesDistribution() {
        return null;
    }

    private HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> makeVariantIntegerAndPositive(HashMap<Long, HashMap<Long, HashMap<Long, Double>>> alphaVariant) {
        return null;
    }

    private HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> generateVariantFromTwo(HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> firstVariant,
                                                                                        HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> secondVariant,
                                                                                        double alpha) {
        HashMap<Long, HashMap<Long, HashMap<Long, Double>>> alphaVariant = null;

        return makeVariantIntegerAndPositive(alphaVariant);
    }

    private Boolean checkVariantAvailability(HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> variant) {
        return null;
    }

    private void generateAndAddNewVariants() {

    }


    private OutputResult startParallelAlg() {
        ArrayList<ParallelSolver> solvers = new ArrayList<>();
        solvers.add(new ParallelSolver(this.inputProduction, this.inputOrders, this.startTime));
        solvers.add(new ParallelSolver(this.inputProduction, this.inputOrders, this.startTime));
        solvers.add(new ParallelSolver(this.inputProduction, this.inputOrders, this.startTime));
        solvers.add(new ParallelSolver(this.inputProduction, this.inputOrders, this.startTime));

        ParallelMain parallelMain = new ParallelMain(this.inputProduction, this.inputOrders, this.startTime, solvers);
        parallelMain.start();

        return null;
    }
}
