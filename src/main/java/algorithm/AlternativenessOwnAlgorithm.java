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
import java.util.stream.Collectors;

public class AlternativenessOwnAlgorithm implements Algorithm {

    private InputProduction inputProduction;
    private ArrayList<InputOrder> inputOrders;
    private LocalDateTime startTime;

    private int threadsNum;


    public AlternativenessOwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, int threadsNum) {
        this.inputProduction = inputProduction;
        this.inputOrders = inputOrders;
        this.startTime = startTime;
        this.threadsNum = threadsNum;
    }

    @Override
    public OutputResult start() throws Exception {

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
