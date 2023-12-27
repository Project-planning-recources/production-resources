package algorithm.model.order;

import parse.adapter.DateAdapter;
import parse.input.order.InputOrder;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Класс для Алгоритма</b>
 * <b>Заказ</b>
 */
public class Order {

    /**
     * ID заказа
     */
    private long id;

    /**
     * Время раннего начала выполнения заказа
     */
    private LocalDateTime startTime;

    /**
     * Дедлайн для заказа (директивный срок)
     */
    private LocalDateTime deadline;

    /**
     * Список изделий, которые надо изготовить
     */
    private ArrayList<Product> products;

    public Order() {

    }

    public Order(InputOrder inputOrder) {
        this.id = inputOrder.getId();
        this.startTime = inputOrder.getStartTime();
        this.deadline = inputOrder.getDeadline();
        ArrayList<Product> products = new ArrayList<>();
        inputOrder.getProducts().forEach(inputProduct -> {
            products.add(new Product(inputProduct, this.id));
        });
        this.products = products;
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
