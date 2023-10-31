package algorithm;

import algorithm.alternativeness.AlternativeElector;
import algorithm.model.order.Product;
import algorithm.operationchooser.OperationChooser;
import algorithm.model.order.Operation;
import algorithm.model.order.Order;
import algorithm.model.production.Equipment;
import algorithm.model.production.Production;
import algorithm.model.production.WorkingDay;
import algorithm.model.result.OperationResult;
import algorithm.model.result.OrderResult;
import algorithm.model.result.ProductResult;
import algorithm.model.result.Result;
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

    /**
     * Лист с операциями, которые можем начать выполнять в текущий момент
     * (Операции без предшественников и с прошедшим временем раннего начала)
     */
    protected ArrayList<OperationResult> waitingOperations;

    /**
     * Таймлайн (список тактирования) для тактирования по времени. Содержит в себе времена,
     * в которые мы производим опросы оборудования. По данному таймлайну выполняется алгоритм
     */
    protected LinkedList<LocalDateTime> timeline;

    public AbstractAlgorithm(InputProduction inputProduction, ArrayList<InputOrder> inputOrders, LocalDateTime startTime,
                             String operationChooser, String alternativeElector) {
        this.production = new Production(inputProduction);
        ArrayList<Order> orders = new ArrayList<>();
        inputOrders.forEach(inputOrder -> {
            orders.add(new Order(inputOrder));
        });
        this.orders = orders;

        // todo: Использовать время начала
        this.startTime = startTime;

        this.ongoingOperations = new ArrayList<>();
        this.waitingOperations = new ArrayList<>();
        initOperationsHashMap();
        initEquipmentHashMap();
        initTimeline();
        initResult();
        this.operationChooser = OperationChooserFactory.getOperationChooser(operationChooser, this);
        this.alternativeElector = AlternativeElectorFactory.getAlternativeElector(alternativeElector, this);
    }

    @Override
    public OutputResult start() throws Exception {
        while(!this.timeline.isEmpty()) {
            LocalDateTime timeTick = this.timeline.pop();
            tickOfTime(timeTick);

        }
        setTimeForOrdersAndResult();


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

    protected HashMap<Long, Operation> allOperations;

    protected void initOperationsHashMap () {
        this.allOperations = new HashMap<>();

        this.orders.forEach(order -> {
            order.getProducts().forEach(product -> {
                product.getTechProcesses().forEach(techProcess -> {
                    techProcess.getOperations().forEach(operation -> {
                        allOperations.put(operation.getId(), operation);
                    });
                });
            });
        });
    }

    protected HashMap<Long, Equipment> allEquipment;

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


    protected Result result;

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
            while(true) {
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

        while(true) {
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


    private ArrayList<OperationResult> ongoingOperations;

    /**
     * В данной функции обрабатываем один такт времени
     */
    protected void tickOfTime(LocalDateTime timeTick) throws Exception {
        /**
         * Добавляем операции заказа, для которого наступило время раннего начала
         */
        this.orders.forEach(order -> {
            if(order.getStartTime().isEqual(timeTick)) {
                addNewOrderOperations(order);
            }
        });


        if(isWeekend(timeTick)) {
            moveTimeTickFromWeekend(timeTick);
        } else {
            /**
             * Обрабатываем операции, которые завершились в данный момент
             */
            ArrayList<OperationResult> finishOperations = new ArrayList<>();
            this.ongoingOperations.forEach(ongoingOperation -> {
                if(ongoingOperation.getEndTime().isEqual(timeTick)) {
                    finishOperations.add(ongoingOperation);
                }
            });
            finishOperations.forEach(finishOperation -> {
                releaseEquipmentAndNextOperation(finishOperation, timeTick);
            });


            /**
             * Начинаем выполнять новые операции, если это возможно
             */
            startOperations(timeTick);
        }

    }

    protected void startOperations(LocalDateTime timeTick) throws Exception {
        ArrayList<OperationResult> candidates = new ArrayList<>();
        this.waitingOperations.forEach(waitingOperation -> {
            try {
                if(this.production.isOperationCanBePerformed(this.allOperations.get(waitingOperation.getOperationId()).getRequiredEquipment())) {
                    candidates.add(waitingOperation);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        while(!candidates.isEmpty()) {
            OperationResult choose = operationChooser.choose(candidates);

            choose.setStartTime(timeTick);
            if(choose.getPrevOperationId() == 0) {
                choose.getProductResult().setStartTime(timeTick);
            }
            LocalDateTime endTime = addOperationTimeToTimeline(timeTick, this.allOperations.get(choose.getOperationId()).getDuration());
            choose.setEndTime(endTime);

            Equipment equipment = production.getEquipmentForOperation(choose, this.allOperations.get(choose.getOperationId()).getRequiredEquipment());
            equipment.setUsing(true);
            choose.setEquipmentId(equipment.getId());

            this.waitingOperations.remove(choose);
            this.ongoingOperations.add(choose);

            candidates.clear();
            this.waitingOperations.forEach(waitingOperation -> {
                try {
                    if(this.production.isOperationCanBePerformed(this.allOperations.get(waitingOperation.getOperationId()).getRequiredEquipment())) {
                        candidates.add(waitingOperation);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * Если операция завершилась, освобождаем оборудование и начинаем следующую, если такая есть
     */
    protected void releaseEquipmentAndNextOperation(OperationResult ongoingOperation, LocalDateTime timeTick) {

        if(ongoingOperation.getNextOperation() != null) {
            this.waitingOperations.add(ongoingOperation.getNextOperation());
        } else {
            ongoingOperation.getProductResult().setEndTime(timeTick);
        }
        this.ongoingOperations.remove(ongoingOperation);
        this.allEquipment.get(ongoingOperation.getEquipmentId()).setUsing(false);

    }

    private long concreteProductId = 1;

    protected long chooseAlternativeness(long concreteProductId, Product product) {
        return this.alternativeElector.chooseTechProcess(product);
    }
    /**
     * Добавляем операции заказа, у которого наступило время раннего начала, в список операций
     * Добавляем в объект результата объекты результатов заказа, деталей и операций
     */
    protected void addNewOrderOperations(Order order) {

        ArrayList<ProductResult> productResults = new ArrayList<>();
        OrderResult orderResult = new OrderResult(order.getId(), null, null, productResults);
        orderResult.setResult(this.result);
        order.getProducts().forEach(product -> {

            for (int i = 0; i < product.getCount(); i++) {

                /**
                 * Выбираем техпроцесс
                 */
                long techProcessId = chooseAlternativeness(this.concreteProductId, product);
                LinkedList<Operation> operations = product.getTechProcessByTechProcessId(techProcessId).getOperations();

                LinkedList<OperationResult> operationResults = new LinkedList<>();
                ProductResult productResult = new ProductResult(this.concreteProductId++, product.getId(), techProcessId, null, null, operationResults, orderResult);
                OperationResult prevOperation = null;
                for (int j = 0; j < operations.size(); j++) {
                    Operation operation = operations.get(j);
                    OperationResult operationResult = new OperationResult(operation.getId(), operation.getPrevOperationId(), operation.getNextOperationId(),
                            0, null, null, productResult);

                    if(prevOperation != null) {
                        prevOperation.setNextOperation(operationResult);
                    }
                    prevOperation = operationResult;

                    if(j == 0) {
                        this.waitingOperations.add(operationResult);
                    }
                    operationResults.add(operationResult);
                }



                productResults.add(productResult);
            }
        });


        this.result.getOrderResults().add(orderResult);
    }


}
