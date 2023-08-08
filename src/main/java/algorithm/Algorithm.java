package algorithm;

import model.order.Order;
import model.production.Production;
import model.result.Result;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
    Result start() throws Exception;

}
