package testing;

import model.order.*;
import model.production.EquipmentGroup;
import model.production.Production;
import model.result.Result;
//import result.Result.LocalDateTime;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Тестер на физическую возможность выполнить данный заказ на данном производстве</b>
 */
public class PossibilityTester {
    private PossibilityTester() {
    }

    /**
     * Тестирующая функция
     *
     * @param production - информация о производстве
     * @param orders     - информация о заказах
     * @return Boolean - возможен ли заказ на данном производстве
     */
    public static Boolean test(Production production, OrderInformation orders) {
        boolean flag = true;
        for (Order or : orders.getOrders()) {
            for (Product p : or.getProducts()) {
                for (TechProcess tp : p.getTechProcesses()) {
                    for (Operation o : tp.getOperations()) {
                        if (!production.isPossibleToMake(o)) {
                            System.out.println("Операция " + o.getId() + " не может быть выполнена");
                            flag = false;
                        }
                    }
                }
            }
        }
        return flag;
    }


}
