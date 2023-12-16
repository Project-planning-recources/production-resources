package algorithm.alpha;

import parse.input.order.InputOrder;
import parse.input.production.InputProduction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

public class AlphaClusterVariatorAlgorithmParallel extends AlphaVariatorAlgorithmParallel {

    protected ArrayList<Double> clusterCentres;
    protected ArrayList<Integer> clusterSizes;
    protected HashMap<HashMap<Long, Integer>, Integer> clusterBelong;

    public AlphaClusterVariatorAlgorithmParallel(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, String frontAlgorithmType,
                                                 int frontThreadsCount, int startVariatorCount, int variatorBudget, int threadsNum) {
        super(inputProduction, inputOrders, startTime, frontAlgorithmType, frontThreadsCount, startVariatorCount, variatorBudget, threadsNum);

        this.clusterCentres = new ArrayList<>();
        this.clusterCentres.add(Double.MAX_VALUE);
        this.clusterCentres.add(Double.MIN_VALUE);
        this.clusterCentres.add(Double.MIN_VALUE);
        this.clusterSizes = new ArrayList<>();
        this.clusterSizes.add(0);
        this.clusterSizes.add(0);
        this.clusterSizes.add(0);
        this.clusterBelong = new HashMap<>();
    }

    @Override
    protected void setStartAlphaGeneration() {
        AlphaClusterVariatorAlgorithm.updateClusters(this.variation, this.clusterCentres, this.clusterSizes, this.clusterBelong);
        super.setStartAlphaGeneration();
    }

    @Override
    protected void initThreads() {
        int startGuaranteed = this.startVariatorCount / this.threadsNum;
        int startDistribute = this.startVariatorCount - startGuaranteed * this.threadsNum;
        int budgetGuaranteed = (this.variatorBudget - this.startVariatorCount) / this.threadsNum;
        int budgetDistribute = (this.variatorBudget - this.startVariatorCount) - budgetGuaranteed * this.threadsNum;

        Semaphore variationSemaphore = new Semaphore(1);
        Semaphore pairsSemaphore = new Semaphore(1);
        Semaphore clusterSemaphore = new Semaphore(1);

        for (int i = 1; i <= threadsNum; i++) {
            AlphaClusterSolverParallel solver = new AlphaClusterSolverParallel(this.inputProduction, this.inputOrders, startTime, this.frontAlgorithmType, this.frontThreadsCount,
                    startGuaranteed + (startDistribute-- > 0 ? 1 : 0),
                    budgetGuaranteed + (budgetDistribute-- > 0 ? 1 : 0),
                    this.variation, this.variantPairs, this,
                    this.clusterCentres, this.clusterSizes, this.clusterBelong,
                    variationSemaphore, pairsSemaphore, clusterSemaphore);
            this.solvers.add(solver);
            this.threads.add(new Thread(solver, "Solver " + i));
        }
    }
}
