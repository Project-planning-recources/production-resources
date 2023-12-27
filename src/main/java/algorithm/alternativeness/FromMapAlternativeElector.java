package algorithm.alternativeness;

import algorithm.model.order.Product;
import algorithm.model.order.TechProcess;
import util.Hash;

import java.util.HashMap;

public class FromMapAlternativeElector implements AlternativeElector{

    private HashMap<Long, Integer> variant;
    public FromMapAlternativeElector(HashMap<Long, Integer> variant) {
        this.variant = new HashMap<>(variant);
    }
    @Override
    public long chooseTechProcess(Product product) {


        for (TechProcess techProcess : product.getTechProcesses()) {
            long hash = Hash.hash(product.getOrderId(), product.getId(), techProcess.getId());
            int count = variant.get(hash);
            if(count > 0) {
                variant.replace(hash, --count);
                return techProcess.getId();
            }
        }

        throw new RuntimeException("Unreachable code");
    }
}
