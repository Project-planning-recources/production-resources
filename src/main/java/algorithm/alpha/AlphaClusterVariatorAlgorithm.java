package algorithm.alpha;

import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import util.Hash;
import util.Pair;
import util.Random;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    protected void updateClusters() {
        this.variation.forEach(variantPair -> {
            if(variantPair.getValue() < this.clusterCentres.get(0)) {
                this.clusterCentres.set(0, variantPair.getValue());
            }
            if(variantPair.getValue() > this.clusterCentres.get(2)) {
                this.clusterCentres.set(2, variantPair.getValue());
            }
        });
        this.variation.forEach(variantPair -> {
            if(Math.abs((this.clusterCentres.get(2) - this.clusterCentres.get(1)) - (this.clusterCentres.get(1) - this.clusterCentres.get(0))) >
               Math.abs((this.clusterCentres.get(2) - variantPair.getValue()) - (variantPair.getValue() - this.clusterCentres.get(0))) ) {
                this.clusterCentres.set(1, variantPair.getValue());
            }
        });
        this.clusterSizes = (ArrayList<Integer>) this.clusterSizes.stream().map(s -> 0).collect(Collectors.toList());
        this.variation.forEach(variantPair -> {
            double d0 = variantPair.getValue() - this.clusterCentres.get(0);
            double d1 = Math.abs(variantPair.getValue() - this.clusterCentres.get(1));
            double d2 = this.clusterCentres.get(2) - variantPair.getValue();
            if(!this.clusterBelong.containsKey(variantPair.getKey())) {
                this.clusterBelong.put(variantPair.getKey(), -1);
            }
            if(d0 < d1 && d0 < d2) {
                this.clusterBelong.replace(variantPair.getKey(), 0);
                this.clusterSizes.set(0, this.clusterSizes.get(0) + 1);
            } else if(d1 <= d0 && d1 <= d2) {
                this.clusterBelong.replace(variantPair.getKey(), 1);
                this.clusterSizes.set(1, this.clusterSizes.get(1) + 1);
            } else {
                this.clusterBelong.replace(variantPair.getKey(), 2);
                this.clusterSizes.set(2, this.clusterSizes.get(2) + 1);
            }
        });
    }

    @Override
    protected void generateAlphaVariants() throws Exception {
        for (int i = this.startVariatorCount; i <= this.variatorBudget;) {
            loading();
            updateClusters();
            int addCount = generateAndAddNewVariants();
            i += addCount;
        }
    }

    protected Pair<HashMap<Long, Integer>, Double> randomChooseVariantAndCriterion(int cluster) {
        Pair<HashMap<Long, Integer>, Double> pair = new Pair<>(null, null);
        int index = Random.randomInt(this.clusterSizes.get(cluster));
        for (Pair<HashMap<Long, Integer>, Double> variantPair : this.variation) {
            if (this.clusterBelong.get(variantPair.getKey()) == cluster) {
                if(index == 0) {
                    pair.setKey(variantPair.getKey());
                    pair.setValue(variantPair.getValue());
                    break;
                } else {
                    index--;
                }
            }
        }
        if(pair.getKey() == null) {
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
        while (pairsFlag) {

            Pair<HashMap<Long, Integer>, Double> pair = randomChooseVariantAndCriterion(0);
            firstVariant = pair.getKey();
            criterionForFirstVariant = pair.getValue();
            do {
                if(Random.randomInt(100) <= 66) {
                    pair = randomChooseVariantAndCriterion(1);
                    secondVariant = pair.getKey();
                    criterionForSecondVariant = pair.getValue();
                } else {
                    pair = randomChooseVariantAndCriterion(2);
                    secondVariant = pair.getKey();
                    criterionForSecondVariant = pair.getValue();
                }
            } while (firstVariant == secondVariant);

            pairsHash = Hash.hash((long) firstVariant.hashCode(), (long) secondVariant.hashCode());
            pairsFlag = !putPairIfAbsent(pairsHash);
        }

        pairs.add(new Pair<>(firstVariant, criterionForFirstVariant));
        pairs.add(new Pair<>(secondVariant, criterionForSecondVariant));

        return pairs;
    }
}
