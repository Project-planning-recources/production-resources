package parse.input;

import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;

import javax.xml.bind.JAXBException;
import java.io.IOException;

/**
 * <b>Интерфейс, который должен реализовывать считыватель входных файлов</b>
 *
 * <p>Процесс:
 *  * Считыватель читает файлы производства и заказов, проверяет ошибки в файле(например, время конца работы может быть раньше времени начала)</p>
 */
public interface Reader {

    /**
     * Функция для чтения файла с данными о производстве
     * @param productionFileName - имя файла
     * @return  класс с данными о производстве
     */
    InputProduction readProductionFile(String productionFileName) throws IOException, JAXBException;

    /**
     * Функция для чтения файла с данными о заказах
     * @param orderFileName - имя файла
     * @return  класс с данными о заказах
     */
    InputOrderInformation readOrderFile(String orderFileName) throws IOException, JAXBException;

    /**
     * Функция для чтения файла с результатами работы алгоритма
     * @param resultFileName - имя файла
     * @return  класс с данными о результате работы алгоритма
     */
    OutputResult readResultFile(String resultFileName);
}
