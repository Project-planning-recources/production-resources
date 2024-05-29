package algorithm.model.order;

import parse.input.order.InputProduct;

import java.util.ArrayList;
import java.util.Objects;

/**
 * <b>Класс для Алгоритма</b>
 * <b>Данные о детали</b>
 * <p>Тут не конкретная деталь, а просто её "тип". Конкретные детали появляются на этапе формирования результата, когда мы присваиваем им id (класс ProductionResult)</p>
 */
public class Product {

    /**
     * ID детали
     */
    private long id;

    private long orderId;

    /**
     * Название детали
     */
    private String name;

    /**
     * Количество деталей в заказе
     */
    private Integer count;


    /**
     * Список возможных альтернативных техпроцессов для детали
     */
    private ArrayList<TechProcess> techProcesses;

    public Product() {

    }

    public Product(InputProduct inputProduct, long orderId) {
        this.id = inputProduct.getId();
        this.orderId = orderId;
        this.name = inputProduct.getName();
        this.count = inputProduct.getCount();

        ArrayList<TechProcess> techProcesses = new ArrayList<>();
        inputProduct.getTechProcesses().forEach(inputTechProcess -> {
            techProcesses.add(new TechProcess(inputTechProcess));
        });
        this.techProcesses = techProcesses;
    }

    public Product(long id, String name, Integer count, ArrayList<TechProcess> techProcesses) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.techProcesses = techProcesses;
    }

    public long getId() {
        return id;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return count;
    }

    public ArrayList<TechProcess> getTechProcesses() {
        return techProcesses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public TechProcess getTechProcessByTechProcessId(long techProcessId) {
        for(TechProcess techProcess : techProcesses) {
            if(techProcess.getId() == techProcessId) {
                return techProcess;
            }
        }
        return null;
    }
}
