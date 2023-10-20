package generator;

import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;

public class GeneratedData {

    private InputOrderInformation inputOrderInformation;
    private InputProduction inputProduction;

    public GeneratedData(InputProduction inputProduction, InputOrderInformation inputOrderInformation) {
        this.inputOrderInformation = inputOrderInformation;
        this.inputProduction = inputProduction;
    }

    public InputOrderInformation getInputOrderInformation() {
        return inputOrderInformation;
    }

    public InputProduction getInputProduction() {
        return inputProduction;
    }

    @Override
    public String toString() {
        return "GeneratedData{" +
                "inputOrderInformation=" + inputOrderInformation +
                ", inputProduction=" + inputProduction +
                '}';
    }
}
