package algorithm.parallel;

import algorithm.BaseAlgorithm;
import algorithm.model.ResultPoint;
import parse.input.order.InputOrder;
import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import testing.ComparisonTester;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class ParallelMain extends Thread {

    private InputProduction inputProduction;
    private ArrayList<InputOrder> inputOrders;
    private LocalDateTime startTime;
    private ArrayList<ParallelSolver> solvers;


    private ArrayList<ResultPoint> results;

    public ParallelMain(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, ArrayList<ParallelSolver> solvers) {
        this.inputProduction = inputProduction;
        this.inputOrders = inputOrders;
        this.startTime = startTime;
        this.solvers = solvers;

        this.solvers.forEach(parallelSolver -> parallelSolver.setMain(this));

        results = new ArrayList<>();
    }

    @Override
    public void run() {
        System.out.println("=====MAIN IS RUNNING=====");

        BaseAlgorithm baseAlgorithm = new BaseAlgorithm(this.inputProduction, this.inputOrders, this.startTime);
        try {
            OutputResult firstStart = baseAlgorithm.start();

            HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> firstStartAlternativeness = firstStart.getAlternativeness();

            this.solvers.forEach(parallelSolver -> parallelSolver.setData(firstStartAlternativeness));
            this.solvers.forEach(ParallelSolver::run);

            boolean running = true;
            while(running) {
                for (int i = 0; i < this.solvers.size(); i++) {
                    if(solvers.get(i).isSolving()) {
                        break;
                    }
                    if(i == this.solvers.size() - 1 && !this.solvers.get(i).isSolving()) {
                        running = false;
                    }
                }
                Thread.sleep(1000);
                System.out.println("running...");

            }

            results.forEach(resultPoint -> ComparisonTester.test(new InputOrderInformation(this.inputOrders), firstStart, resultPoint.getResult()));
            System.out.println("=====END OF MAIN=====");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void addResult(HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> alternativeness, OutputResult result) {
        this.results.add(new ResultPoint(alternativeness, result));
    }
}
