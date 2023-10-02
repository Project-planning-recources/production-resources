package algorithm;

import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class AlternativenessOwnAlgorithm implements Algorithm {

    private InputProduction inputProduction;
    private ArrayList<InputOrder> inputOrders;
    private LocalDateTime startTime;

    public AlternativenessOwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime) {
        this.inputProduction = inputProduction;
        this.inputOrders = inputOrders;
        this.startTime = startTime;
    }

    @Override
    public OutputResult start() throws Exception {
        BaseAlgorithm firstStart = new BaseAlgorithm(inputProduction, inputOrders, startTime);

        return null;
    }
}
