package algorithm;

import algorithm.alternativeness.AlternativeElector;
import algorithm.alternativeness.RandomAlternativeElector;
import algorithm.operationchooser.DateComingOrderChooser;
import algorithm.operationchooser.FirstElementChooser;
import algorithm.operationchooser.GreedyOperationChooser;
import algorithm.operationchooser.OperationChooser;

public class AlternativeElectorFactory {
    private AlternativeElectorFactory() {}

    public static AlternativeElector getAlternativeElector(String elector, AbstractAlgorithm abstractAlgorithm) throws Exception {

        if(elector != null) {
            switch(elector) {
                case("Random"):
                    return new RandomAlternativeElector();
                default:
                    throw new Exception("Unsupported elector");
            }
        } else {
            return null;
        }
    }
}
