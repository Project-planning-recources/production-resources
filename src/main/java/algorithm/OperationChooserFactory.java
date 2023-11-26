package algorithm;

import algorithm.operationchooser.DateComingOrderChooser;
import algorithm.operationchooser.FirstElementChooser;
import algorithm.operationchooser.GreedyOperationChooser;
import algorithm.operationchooser.OperationChooser;

import java.util.ArrayList;
import java.util.HashMap;

public class OperationChooserFactory {
    private OperationChooserFactory() {}

    public static OperationChooser getOperationChooser(String chooser, AbstractAlgorithm abstractAlgorithm) {
        switch(chooser) {
            case("FirstElement"):
                return new FirstElementChooser();
            case("DateComingOrder"):
                return new DateComingOrderChooser();
            default:
                throw new RuntimeException("Unsupported chooser");
        }
    }
}
