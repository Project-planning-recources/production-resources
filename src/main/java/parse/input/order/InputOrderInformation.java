package parse.input.order;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;

/**
 * <b>Класс для IO</b>
 * <b>Данные о заказах</b>
 */
@XmlRootElement(name = "OrderInformation")
@XmlType(name = "OrderInformation")
public class InputOrderInformation {

    /**
     * Список заказов
     */
    @XmlElement(name = "Order")
    private ArrayList<InputOrder> inputOrders;

    public InputOrderInformation() {

    }

    public InputOrderInformation(ArrayList<InputOrder> inputOrders) {
        this.inputOrders = inputOrders;
    }

    public ArrayList<InputOrder> getOrders() {
        return inputOrders;
    }

    public InputOrder getOrderByOrderId(long orderId) {
        for(InputOrder inputOrder : inputOrders) {
            if(inputOrder.getId() == orderId) {
                return inputOrder;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "InputOrderInformation{" +
                "inputOrders=" + inputOrders +
                '}';
    }
}
