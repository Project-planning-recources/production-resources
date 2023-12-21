package algorithm;

import algorithm.alternativeness.AlternativeElector;
import algorithm.model.order.Order;
import algorithm.model.order.Product;
import algorithm.model.production.Equipment;
import algorithm.model.production.Production;
import algorithm.model.production.WorkingDay;
import algorithm.model.result.OrderResult;
import algorithm.model.result.ProductResult;
import algorithm.model.result.Result;
import algorithm.operationchooser.OperationChooser;
import parse.input.order.InputOrder;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public abstract class AbstractAlgorithm implements Algorithm {

    /**
     * Предприятие
     */
    protected Production production;

    /**
     * Все заказы
     */
    protected ArrayList<Order> orders;

    /**
     * Время начала работы
     */
    protected LocalDateTime startTime;

    /**
     *  Объект, отвечающий за выбор операции для исполнения
     */
    protected OperationChooser operationChooser;

    /**
     * Объект, отвечающий за выбор альтернативности для детали
     */
    protected AlternativeElector alternativeElector;

    protected HashMap<Long, Equipment> allEquipment;

    /**
     * Лист с операциями, которые можем начать выполнять в текущий момент
     * (Операции без предшественников и с прошедшим временем раннего начала)
     */


    /**
     * Таймлайн (список тактирования) для тактирования по времени. Содержит в себе времена,
     * в которые мы производим опросы оборудования. По данному таймлайну выполняется алгоритм
     */
    protected LinkedList<LocalDateTime> timeline;

    protected Result result;

    protected long concreteProductId = 1;

    private long counter = 0;


    public AbstractAlgorithm(Production production, ArrayList<Order> orders, LocalDateTime startTime) {
        this.production = production;
        this.orders = orders;
        this.startTime = startTime;
    }

    public AbstractAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime) {
        this.production = new Production(inputProduction);
        ArrayList<Order> orders = new ArrayList<>();
        inputOrders.forEach(inputOrder -> {
            orders.add(new Order(inputOrder));
        });
        this.orders = orders;
        this.startTime = startTime;

    }

    /**
     * Если поддерживается операция, возвращает хэш-мапу альтернативностей
     * @return хэш-мапа распределения альтернативностей
     */
    protected HashMap<Long, Integer> getAlternativenessMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputResult start() throws Exception {
//        int c = 0;
        while(!this.timeline.isEmpty()) {
//            c++;
//            if (c > 3000) {
//                System.out.println("Туть");
//            }
            //System.out.println("AbstractAlgorithm start ");
            LocalDateTime timeTick = this.timeline.pop();
            tickOfTime(timeTick);
        }
        try {
            setTimeForOrdersAndResult();
        } catch (NullPointerException e) {
            System.out.println("Падаем");
        }


        production.getEquipmentGroups().forEach(group -> {
            group.getEquipment().forEach(equipment -> equipment.setIsBusyTo(null));
        });

        return new OutputResult(this.result);
    }

    /**
     * Устанавливаем время завершения заказа для каждого заказа
     */
    protected void setTimeForOrdersAndResult() {
        LocalDateTime firstResult = LocalDateTime.MAX;
        LocalDateTime lastResult = LocalDateTime.MIN;
        for (OrderResult orderResult :
                this.result.getOrderResults()) {

            LocalDateTime first = LocalDateTime.MAX;
            LocalDateTime last = LocalDateTime.MIN;
            for (ProductResult productResult :
                    orderResult.getProductResults()) {
                if(productResult.getEndTime().isAfter(last)) {
                    last = productResult.getEndTime();
                }
                if(productResult.getStartTime().isBefore(first)) {
                    first = productResult.getStartTime();
                }
            }
            orderResult.setStartTime(first);
            orderResult.setEndTime(last);

            if(lastResult.isBefore(last)) {
                lastResult = last;
            }
            if(firstResult.isAfter(first)) {
                firstResult = first;
            }
        }
        this.result.setAllStartTime(firstResult);
        this.result.setAllEndTime(lastResult);
    }

    protected void initEquipmentHashMap () {
        this.allEquipment = new HashMap<>();

        this.production.getEquipmentGroups().forEach(equipmentGroup -> {
            equipmentGroup.getEquipment().forEach(equipment -> {
                this.allEquipment.put(equipment.getId(), equipment);
            });
        });

    }

    /**
     * Инициализация таймлайна
     */
    protected void initTimeline() {
        this.timeline = new LinkedList<>();

        this.orders.forEach(order -> {
            addTimeToTimeline(order.getStartTime());
        });
    }

    /**
     * Создаёт пустой объект результатов
     * Результат по каждому заказу должен создаваться при наступлении времени раннего начала этого заказа
     */
    protected void initResult() {
        this.result = new Result(null, null, new ArrayList<>());
    }

    /**
     * Вставляет время в нужную позицию таймлайна
     *
     * @param time добавляемое время
     */
    public void addTimeToTimeline(LocalDateTime time) {
        if(this.timeline.isEmpty()) {
            this.timeline.add(time);
        } else {
            Iterator<LocalDateTime> iterator = this.timeline.iterator();
            int addIndex = 0;
            LocalDateTime curr = this.timeline.getFirst();
            while(iterator.hasNext()) {
                LocalDateTime next = iterator.next();
                if(next.isAfter(time)) {
                    break;
                } else {
                    addIndex++;
                    curr = next;
                }
            }
            if(!curr.isEqual(time)) {
                this.timeline.add(addIndex, time);
            }
        }
    }

    /**
     * Проверяет, рабочее время или выходной
     * @param timeTick - время
     * @return boolean
     */
    protected boolean isWeekend(LocalDateTime timeTick) {
        WorkingDay day = production.getSchedule().getWorkDayByDayNumber((short) timeTick.getDayOfWeek().getValue());
        if (Objects.isNull(day) || !day.getWeekday()) {
            return true;
        } else {
            return timeTick.toLocalTime().isBefore(day.getStartWorkingTime()) || timeTick.toLocalTime().isAfter(day.getEndWorkingTime());
        }
    }

    /**
     * Добавляет в таймлайн ближайшее рабочее время
     * @param timeTick - время
     */
    protected void moveTimeTickFromWeekend(LocalDateTime timeTick) {
        LocalDateTime newTick = timeTick;
        WorkingDay day = production.getSchedule().getWorkDayByDayNumber((short) newTick.getDayOfWeek().getValue());
        if (isWeekend(timeTick)) {
//            int c = 0;
            while(true) {
//                c++;
               //System.out.println("AbstractAlgorithm moveTimeTickFromWeekend ");
                if (!day.getWeekday()) {
                    newTick = LocalDateTime.of(newTick.toLocalDate(), day.getStartWorkingTime());
                    if(isWeekend(newTick)) {
                        newTick = newTick.plus(1, ChronoUnit.DAYS);
                        day = production.getSchedule().getWorkDayByDayNumber((short) newTick.getDayOfWeek().getValue());
                    } else {
                        break;
                    }
                } else {
                    if (newTick.toLocalTime().isBefore(day.getStartWorkingTime())) {
                        newTick = LocalDateTime.of(newTick.toLocalDate(), day.getStartWorkingTime());
                        break;
                    } else if (newTick.toLocalTime().isAfter(day.getEndWorkingTime())) {
                        newTick = newTick.plus(1, ChronoUnit.DAYS);
                        day = production.getSchedule().getWorkDayByDayNumber((short) newTick.getDayOfWeek().getValue());
                        newTick = LocalDateTime.of(newTick.toLocalDate(), day.getStartWorkingTime());
                        if(!isWeekend(newTick)) {
                            break;
                        }
                    }
                }
            }
        }
        addTimeToTimeline(newTick);
    }

    /**
     * Добавляет время освобождения оборудования в таймлайн
     * Должна вычислить, когда операция закончит выполняться и добавить данное время в таймлайн (метод addTimeToTimeline)
     *
     * При добавлении времени учесть: время работы предприятия и порядок времён
     * @param operationStartTime - время, когда операцию начали выполнять
     * @param duration - время выполнения операции в секундах
     */
    protected LocalDateTime addOperationTimeToTimeline(LocalDateTime operationStartTime, int duration) {
        LocalDateTime finalTime = operationStartTime.plus(duration, ChronoUnit.SECONDS);
        LocalDateTime currentDate = operationStartTime;
        WorkingDay currentWorkingDay = production.getSchedule().getWorkDayByDayNumber((short)currentDate.getDayOfWeek().getValue());
        long diffInSeconds = duration;

//        int c = 0;
        while(true) {
//            c++;
            //System.out.println("AbstractAlgorithm addOperationTimeToTimeline ");
            if(currentWorkingDay.getWeekday()) {
                if(finalTime.toLocalDate().isAfter(currentDate.toLocalDate()) || finalTime.toLocalTime().isAfter(currentWorkingDay.getEndWorkingTime())) {
                    diffInSeconds -= Math.abs(ChronoUnit.SECONDS.between(currentDate, LocalDateTime.of(currentDate.toLocalDate(), currentWorkingDay.getEndWorkingTime())));
                } else {
                    break;
                }
            }

            currentDate = currentDate.plus(1, ChronoUnit.DAYS);
            WorkingDay nextWorkingDay = production.getSchedule().getWorkDayByDayNumber((short)currentDate.getDayOfWeek().getValue());
            currentDate = LocalDateTime.of(currentDate.toLocalDate(), nextWorkingDay.getStartWorkingTime());
            if(nextWorkingDay.getWeekday()) {
                finalTime = LocalDateTime.of(currentDate.toLocalDate(), nextWorkingDay.getStartWorkingTime()).plus(diffInSeconds, ChronoUnit.SECONDS);
            }

            currentWorkingDay = nextWorkingDay;
        }

        addTimeToTimeline(finalTime);
        return finalTime;
    }

    /**
     * В данной функции обрабатываем один такт времени
     */
    protected abstract void tickOfTime(LocalDateTime timeTick) throws Exception;

    protected abstract void startOperations(LocalDateTime timeTick);

    /**
     * Если операция завершилась, освобождаем оборудование и начинаем следующую, если такая есть
     */

    protected long chooseAlternativeness(long concreteProductId, Product product) {
        return this.alternativeElector.chooseTechProcess(product);
    }
    /**
     * Добавляем операции заказа, у которого наступило время раннего начала, в список операций
     * Добавляем в объект результата объекты результатов заказа, деталей и операций
     */

    protected abstract void addNewOrderOperations(Order order);



}
