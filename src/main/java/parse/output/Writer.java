package parse.output;

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
}
