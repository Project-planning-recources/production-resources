package algorithm.parallel;

import algorithm.model.result.OperationResult;

import java.util.List;

public interface Record {

    OperationResult getRecord(List<OperationResult> operations);
}
