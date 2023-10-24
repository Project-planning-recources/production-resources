package algorithm.parallel;

import parse.output.result.OutputResult;

import java.util.ArrayList;

public class ResultPoint {

    private ArrayList<Integer> alternativeness;
    private OutputResult result;

    public ResultPoint(ArrayList<Integer> alternativeness, OutputResult result) {
        this.alternativeness = alternativeness;
        this.result = result;
    }

    public ArrayList<Integer> getAlternativeness() {
        return alternativeness;
    }

    public OutputResult getResult() {
        return result;
    }
}
