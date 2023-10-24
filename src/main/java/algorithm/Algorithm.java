package algorithm;

import algorithm.model.order.Product;
import parse.output.result.OutputResult;

/**
 * <b>Основной интерфейс для решателя</b>
 * <p>Отсюда будут вызываться все действия алгоритма и сюда будут передаваться все его составные части</p>
 */
public interface Algorithm {

    /**
     * Функция, запускающая алгоритм
     *
     * @return - информация о распределении деталей по производству
     */
    OutputResult start() throws Exception;

}
