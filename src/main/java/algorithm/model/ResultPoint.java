package algorithm.model;

import parse.output.result.OutputResult;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultPoint {

    private HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> alternativeness;
    private OutputResult result;

    public ResultPoint(HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> alternativeness, OutputResult result) {
        this.alternativeness = alternativeness;
        this.result = result;
    }

    public HashMap<Long, HashMap<Long, HashMap<Long, Integer>>> getAlternativeness() {
        return alternativeness;
    }

    public OutputResult getResult() {
        return result;
    }
}
