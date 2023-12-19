package algorithm.alpha;

import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import util.Hash;
import util.Pair;
import util.Random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

public class AlphaClusterVariatorAlgorithm extends AlphaVariatorAlgorithm {

    protected ArrayList<Double> clusterCentres;
    protected ArrayList<Integer> clusterSizes;
    protected HashMap<HashMap<Long, Integer>, Integer> clusterBelong;

    public AlphaClusterVariatorAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime, String frontAlgorithmType, int frontThreadsCount, int startVariatorCount, int variatorBudget) {
        super(inputProduction, inputOrders, startTime, frontAlgorithmType, frontThreadsCount, startVariatorCount, variatorBudget);

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

    protected static void updateClusters(ArrayList<Pair<HashMap<Long, Integer>, Double>> variation, ArrayList<Double> clusterCentres,
                                         ArrayList<Integer> clusterSizes, HashMap<HashMap<Long, Integer>, Integer> clusterBelong) {
        variation.forEach(variantPair -> {
            if (variantPair.getValue() != -1) {
                if (variantPair.getValue() < clusterCentres.get(0)) {
                    clusterCentres.set(0, variantPair.getValue());
                }
                if (variantPair.getValue() > clusterCentres.get(2)) {
                    clusterCentres.set(2, variantPair.getValue());
                }
            }
        });
        variation.forEach(variantPair -> {
            if (variantPair.getValue() != -1) {
                if (Math.abs((clusterCentres.get(2) - clusterCentres.get(1)) - (clusterCentres.get(1) - clusterCentres.get(0))) >
                        Math.abs((clusterCentres.get(2) - variantPair.getValue()) - (variantPair.getValue() - clusterCentres.get(0)))) {
                    clusterCentres.set(1, variantPair.getValue());
                }
            }
        });
        for (int i = 0; i < 3; i++) {
            clusterSizes.set(i, 0);
        }
        for (Pair<HashMap<Long, Integer>, Double> variantPair : variation) {
            if (variantPair.getValue() != -1) {
                double d0 = variantPair.getValue() - clusterCentres.get(0);
                double d1 = Math.abs(variantPair.getValue() - clusterCentres.get(1));
                double d2 = clusterCentres.get(2) - variantPair.getValue();
                if (!clusterBelong.containsKey(variantPair.getKey())) {
                    clusterBelong.put(variantPair.getKey(), -1);
                }
                if (d0 < d1 && d0 < d2) {
                    clusterBelong.replace(variantPair.getKey(), 0);
                    clusterSizes.set(0, clusterSizes.get(0) + 1);
                } else if (d1 <= d0 && d1 <= d2) {
                    clusterBelong.replace(variantPair.getKey(), 1);
                    clusterSizes.set(1, clusterSizes.get(1) + 1);
                } else {
                    clusterBelong.replace(variantPair.getKey(), 2);
                    clusterSizes.set(2, clusterSizes.get(2) + 1);
                }
            }
        }
    }

    @Override
    protected void generateAlphaVariants() throws Exception {
        for (int i = this.startVariatorCount; i <= this.variatorBudget; ) {
            loading();
            updateClusters(this.variation, this.clusterCentres, this.clusterSizes, this.clusterBelong);
            int addCount = generateAndAddNewVariants();
            i += addCount;
        }
    }

    protected static Pair<HashMap<Long, Integer>, Double> randomChooseVariantAndCriterionFromCluster(int cluster, ArrayList<Pair<HashMap<Long, Integer>, Double>> variation, ArrayList<Integer> clusterSizes, HashMap<HashMap<Long, Integer>, Integer> clusterBelong) {
        Pair<HashMap<Long, Integer>, Double> pair = new Pair<>(null, null);
        int index = Random.randomInt(clusterSizes.get(cluster));
        for (Pair<HashMap<Long, Integer>, Double> variantPair : variation) {
            if (clusterBelong.containsKey(variantPair.getKey()) && clusterBelong.get(variantPair.getKey()) == cluster) {
                if (index == 0) {
                    pair.setKey(variantPair.getKey());
                    pair.setValue(variantPair.getValue());
                    break;
                } else {
                    index--;
                }
            }
        }
        if (pair.getKey() == null) {
            throw new RuntimeException("Unreachable code");
        }
        return pair;
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
        int counter = 0;
        while (pairsFlag) {
            counter++;
            if(counter > 50) {
                return null;
            }
            Pair<HashMap<Long, Integer>, Double> pair = null;

            if (clusterSizes.get(0) == 0 || clusterSizes.get(1) == 0 || clusterSizes.get(2) == 0) {
                pair = this.variation.get(Random.randomInt(this.variation.size()));
                firstVariant = pair.getKey();
                criterionForFirstVariant = pair.getValue();
                do {
                    pair = this.variation.get(Random.randomInt(this.variation.size()));
                    secondVariant = pair.getKey();
                    criterionForSecondVariant = pair.getValue();
                } while (firstVariant == secondVariant);
            } else {
                pair = randomChooseVariantAndCriterionFromCluster(0, this.variation, this.clusterSizes, this.clusterBelong);
                firstVariant = pair.getKey();
                criterionForFirstVariant = pair.getValue();
                do {
                    if (Random.randomInt(100) <= 66) {
                        pair = randomChooseVariantAndCriterionFromCluster(1, this.variation, this.clusterSizes, this.clusterBelong);
                        secondVariant = pair.getKey();
                        criterionForSecondVariant = pair.getValue();
                    } else {
                        pair = randomChooseVariantAndCriterionFromCluster(2, this.variation, this.clusterSizes, this.clusterBelong);
                        secondVariant = pair.getKey();
                        criterionForSecondVariant = pair.getValue();
                    }
                } while (firstVariant == secondVariant);
            }

            pairsHash = Hash.hash((long) firstVariant.hashCode(), (long) secondVariant.hashCode());
            pairsFlag = !putPairIfAbsent(pairsHash);
        }

        pairs.add(new Pair<>(firstVariant, criterionForFirstVariant));
        pairs.add(new Pair<>(secondVariant, criterionForSecondVariant));

        return pairs;
    }
}
