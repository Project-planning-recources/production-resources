package model.order;

import parse.adapter.DateAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Заказ</b>
 */
@XmlType(name = "Order")
public class Order {

    /**
     * ID заказа
     */
    @XmlAttribute(name = "id")
    private long id;

    /**
     * Время раннего начала выполнения заказа
     */
    @XmlAttribute(name = "date_begin")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private LocalDateTime startTime;

    /**
     * Дедлайн для заказа (директивный срок)
     */
    @XmlAttribute(name = "date_end")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private LocalDateTime deadline;

    /**
     * Список изделий, которые надо изготовить
     */
    @XmlElement(name = "Detail")
    private ArrayList<Product> products;

    public Order() {

    }

    public Order(long id, LocalDateTime startTime, LocalDateTime deadline, ArrayList<Product> products) {
        this.id = id;
        this.startTime = startTime;
        this.deadline = deadline;
        this.products = products;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public Product getProductByProductId(long productId) {
        for(Product product : products) {
            if(product.getId() == productId) {
                return product;
            }
        }
        return null;
    }
}
