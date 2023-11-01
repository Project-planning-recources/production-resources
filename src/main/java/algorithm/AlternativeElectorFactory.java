package algorithm;

import algorithm.alternativeness.AlternativeElector;
import algorithm.alternativeness.FromMapAlternativeElector;
import algorithm.alternativeness.RandomAlternativeElector;
import algorithm.operationchooser.DateComingOrderChooser;
import algorithm.operationchooser.FirstElementChooser;
import algorithm.operationchooser.GreedyOperationChooser;
import algorithm.operationchooser.OperationChooser;

public class AlternativeElectorFactory {
    private AlternativeElectorFactory() {}

    public static AlternativeElector getAlternativeElector(String elector, AbstractAlgorithm abstractAlgorithm) {

        if(elector != null) {
            switch(elector) {
                case("Random"):
                    return new RandomAlternativeElector();
                case("Map"):
                    return new FromMapAlternativeElector(abstractAlgorithm.getAlternativenessMap());
                default:
                    throw new RuntimeException("Unsupported elector");
            }
        } else {
            return null;
        }
    }
}
