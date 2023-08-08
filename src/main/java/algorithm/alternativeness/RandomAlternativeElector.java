package algorithm.alternativeness;

import model.order.Product;
import model.order.TechProcess;
import util.Random;

import java.util.ArrayList;

public class RandomAlternativeElector implements AlternativeElector {

    @Override
    public int chooseTechProcess(Product product) {
        ArrayList<TechProcess> techProcesses = product.getTechProcesses();
        return Random.randomInt(techProcesses.size());
    }
}
