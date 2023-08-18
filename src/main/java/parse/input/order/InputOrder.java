package parse.input.order;

import parse.adapter.DateAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * <b>Класс для IO</b>
 * <b>Заказ</b>
 */
@XmlType(name = "Order")
public class InputOrder {

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
    private ArrayList<InputProduct> inputProducts;

    public InputOrder() {

    }

    public InputOrder(long id, LocalDateTime startTime, LocalDateTime deadline, ArrayList<InputProduct> inputProducts) {
        this.id = id;
        this.startTime = startTime;
        this.deadline = deadline;
        this.inputProducts = inputProducts;
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

    public ArrayList<InputProduct> getProducts() {
        return inputProducts;
    }

    public InputProduct getProductByProductId(long productId) {
        for(InputProduct inputProduct : inputProducts) {
            if(inputProduct.getId() == productId) {
                return inputProduct;
            }
        }
        return null;
    }
}
