package model.order;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;

@XmlRootElement(name = "OrderInformation")
@XmlType(name = "OrderInformation")
public class OrderInformation {

    /**
     * Список заказов
     */
    @XmlElement(name = "Order")
    private ArrayList<Order> orders;

    public OrderInformation() {

    }

    public OrderInformation(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public Order getOrderByOrderId(long orderId) {
        for(Order order : orders) {
            if(order.getId() == orderId) {
                return order;
            }
        }
        return null;
    }
}
