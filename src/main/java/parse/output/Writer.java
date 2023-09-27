package parse.output;

import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public interface Writer {

    /**
     * Функция для записи результатов работы алгоритма в файл
     * @param resultFileName - имя файла
     * @param outputResult - результат работы алгоритма
     */
    void writeResultFile(String resultFileName, OutputResult outputResult) throws JAXBException, IOException;

    /**
     * Функция для записи информации о производстве в файл
     * @param productionFileName - имя файла
     * @param inputProduction - информация о производстве
     */
    void writeProductionFile(String productionFileName, InputProduction inputProduction);

    /**
     * Функция для записи информации о заказах в файл
     * @param orderInformationFileName - имя файла
     * @param inputOrderInformation - информация о заказах
     */
    void writeOrderInformationFile(String orderInformationFileName, InputOrderInformation inputOrderInformation);
}
