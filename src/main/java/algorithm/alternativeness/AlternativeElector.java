package algorithm.alternativeness;

import model.order.Product;

/**
 * <b></b>Интерфейс для объекта, который выбирает для деталей техпроцесс
 */
public interface AlternativeElector {
    /**
     * Функция выбора техпроцесса
     * @param product - деталь, для которой выбираем техпроцесс
     * @return номер выбранного техпроцесса
     */
    int chooseTechProcess(Product product);
}
