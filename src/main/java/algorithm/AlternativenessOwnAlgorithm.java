package algorithm;

import algorithm.operationchooser.FirstElementChooser;
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


    public AlternativenessOwnAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime) {
        this.inputProduction = inputProduction;
        this.inputOrders = inputOrders;
        this.startTime = startTime;
    }

    @Override
    public OutputResult start() throws Exception {
        BaseAlgorithm firstStart = new BaseAlgorithm(inputProduction, inputOrders, startTime);

        OutputResult firstStartResult = firstStart.start();


        ArrayList<Integer> alternativeness = new ArrayList<>();
        firstStartResult.getOrderResults().forEach(outputOrderResult -> {
            outputOrderResult.getProductResults().forEach(outputProductResult -> {
                alternativeness.add((int)outputProductResult.getTechProcessId());
            });
        });
        System.out.println(alternativeness);


        OwnAlgorithm ownAlgorithm = new OwnAlgorithm(inputProduction, inputOrders, startTime,
                "FirstElement",
                alternativeness);

        OutputResult start = ownAlgorithm.start();


        ComparisonTester.test(new InputOrderInformation(inputOrders), firstStartResult, start);



        return null;
    }
}
