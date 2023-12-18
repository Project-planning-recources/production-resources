package algorithm.records.util;

import java.util.List;

public class WaitingCounter {

    public static <T extends Thread> void waitingCounter(List<T> threads) {
        int awakeTriersCounter = 0;
        boolean allThreadsAreTerminated;
        while(true) {
            allThreadsAreTerminated = true;
            for (T thread : threads) {
                if (!Thread.State.TERMINATED.equals(thread.getState())) {
                    allThreadsAreTerminated = false;
                }
            }
            if (awakeTriersCounter == Integer.MAX_VALUE - 1) {
                throw new IllegalStateException("Достигнуто максимальное количество попыток пробудить основной поток");
            }
            if (allThreadsAreTerminated) {
                return;
            }
            try {
                Thread.currentThread().sleep(1);
                awakeTriersCounter++;
            } catch (InterruptedException e) {
                throw new RuntimeException("Поток был прерван");
            }
        }
    }
}
