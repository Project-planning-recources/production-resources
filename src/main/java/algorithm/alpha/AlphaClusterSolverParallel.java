package algorithm.alpha;

import algorithm.model.order.Order;
import algorithm.model.production.Production;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import util.Hash;
import util.Pair;
import util.Random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class AlphaClusterSolverParallel extends AlphaSolverParallel {

    protected ArrayList<Double> clusterCentres;
    protected ArrayList<Integer> clusterSizes;
    protected HashMap<HashMap<Long, Integer>, Integer> clusterBelong;

    protected Semaphore clusterSemaphore;

    public AlphaClusterSolverParallel(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, String frontAlgorithmType, int frontThreadsCount, int startVariatorCount, int variatorBudget, ArrayList<Pair<HashMap<Long, Integer>, Double>> variation, HashMap<Long, Boolean> variantPairs, AlphaVariatorAlgorithmParallel main,
                                      ArrayList<Double> clusterCentres, ArrayList<Integer> clusterSizes, HashMap<HashMap<Long, Integer>, Integer> clusterBelong,
                                      Semaphore variationSemaphore, Semaphore pairsSemaphore, Semaphore clusterSemaphore) {
        super(inputProduction, inputOrders, startTime, frontAlgorithmType, frontThreadsCount, startVariatorCount, variatorBudget, variation, variantPairs, main, variationSemaphore, pairsSemaphore);

        this.clusterCentres = clusterCentres;
        this.clusterSizes = clusterSizes;
        this.clusterBelong = clusterBelong;
        this.clusterSemaphore = clusterSemaphore;
    }

    @Override
    protected void generateAlphaVariants() throws Exception {
        for (int i = 0; i < this.variatorBudget;) {
            loading();
            this.variationSemaphore.acquire();
            this.clusterSemaphore.acquire();
            AlphaClusterVariatorAlgorithm.updateClusters(this.variation, this.clusterCentres, this.clusterSizes, this.clusterBelong);
            this.variationSemaphore.release();
            this.clusterSemaphore.release();
            int addCount = generateAndAddNewVariants();
            i += addCount;
            calculated += addCount;
        }
    }

    @Override
    protected ArrayList<Pair<HashMap<Long, Integer>, Double>> getVariantPair() {
        ArrayList<Pair<HashMap<Long, Integer>, Double>> pairs = new ArrayList<>();
        HashMap<Long, Integer> firstVariant = null;
        HashMap<Long, Integer> secondVariant = null;

        long pairsHash = 0;
        double criterionForFirstVariant = 0;
        double criterionForSecondVariant = 0;

        boolean pairsFlag = true;
        while (pairsFlag) {

            try {
                this.variationSemaphore.acquire();
                this.clusterSemaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Pair<HashMap<Long, Integer>, Double> pair = AlphaClusterVariatorAlgorithm.randomChooseVariantAndCriterion(0, this.variation, this.clusterSizes, this.clusterBelong);
            firstVariant = pair.getKey();
            criterionForFirstVariant = pair.getValue();
            do {
                if(Random.randomInt(100) <= 66) {
                    pair = AlphaClusterVariatorAlgorithm.randomChooseVariantAndCriterion(1, this.variation, this.clusterSizes, this.clusterBelong);
                    secondVariant = pair.getKey();
                    criterionForSecondVariant = pair.getValue();
                } else {
                    pair = AlphaClusterVariatorAlgorithm.randomChooseVariantAndCriterion(2, this.variation, this.clusterSizes, this.clusterBelong);
                    secondVariant = pair.getKey();
                    criterionForSecondVariant = pair.getValue();
                }
            } while (firstVariant == secondVariant);
            this.variationSemaphore.release();
            this.clusterSemaphore.release();

            pairsHash = Hash.hash((long) firstVariant.hashCode(), (long) secondVariant.hashCode());
            pairsFlag = !putPairIfAbsent(pairsHash);
        }

        pairs.add(new Pair<>(firstVariant, criterionForFirstVariant));
        pairs.add(new Pair<>(secondVariant, criterionForSecondVariant));

        return pairs;
    }
}
