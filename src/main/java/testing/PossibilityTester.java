package testing;

import parse.input.order.*;
import parse.input.production.*;

import java.util.List;
import java.util.Objects;


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
    public static Boolean test(InputProduction production, InputOrderInformation orders) {
        boolean flag = true;
        for (InputOrder or : orders.getOrders()) {
            for (InputProduct p : or.getProducts()) {
                for (InputTechProcess tp : p.getTechProcesses()) {
                    for (InputOperation o : tp.getOperations()) {
                        if (!isPossibleToMake(production.getEquipmentGroups(), o)) {
                            System.out.println("Операция " + o.getId() + " не может быть выполнена");
                            flag = false;
                        }
                    }
                }
            }
        }
        return flag;
    }

    private static boolean isPossibleToMake(List<InputEquipmentGroup> inputEquipmentGroups, InputOperation operation) {
        for (InputEquipmentGroup e : inputEquipmentGroups) {
            if (e.getId() == operation.getRequiredEquipment() || Objects.nonNull(e.getEquipment())) {
                return true;
            }
        }
        return false;
    }

}
