package algorithm.alternativeness;

import algorithm.model.order.Product;
import algorithm.model.order.TechProcess;
import util.Random;

import java.util.ArrayList;

public class RandomAlternativeElector implements AlternativeElector {

    @Override
    public long chooseTechProcess(Product product) {
        ArrayList<TechProcess> techProcesses = product.getTechProcesses();
        int randomed = Random.randomInt(techProcesses.size());
        return techProcesses.get(randomed).getId();
    }
}
