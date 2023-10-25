package algorithm.parallel;

import algorithm.OwnAlgorithm;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class ParallelSolver extends Thread {

    private InputProduction inputProduction;
    private ArrayList<InputOrder> inputOrders;
    private LocalDateTime startTime;

    private OwnAlgorithm algorithm;
    private HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> alternativeness;
    private ParallelMain main;

    private boolean solving = false;

    public ParallelSolver(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime) {
        this.inputProduction = inputProduction;
        this.inputOrders = inputOrders;
        this.startTime = startTime;
    }

    public void setMain(ParallelMain main) {
        this.main = main;
    }

    public boolean setData(HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> alternativeness) {
        if (solving) {
            return false;
        } else {
            this.alternativeness = alternativeness;
            this.algorithm = new OwnAlgorithm(this.inputProduction, this.inputOrders, this.startTime, "FirstElement", alternativeness);
            return true;
        }
    }

    @Override
    public void run() {
        try {
            if(!this.solving && this.alternativeness != null && this.algorithm != null) {
                System.out.println("SOLVER " + this.getId() + " IS RUNNING");
                this.solving = true;
                OutputResult result = this.algorithm.start();

                main.addResult(this.alternativeness, result);
                this.solving = false;
                System.out.println("SOLVER " + this.getId() + " FINISHED");
            }



        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isSolving() {
        return solving;
    }
}
