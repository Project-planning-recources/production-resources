package parse.output;

import model.result.Result;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public interface Writer {

    /**
     * Функция для записи результатов работы алгоритма в файл
     * @param resultFileName - имя файла
     * @param result - результат работы алгоритма
     */
    void writeResultFile(String resultFileName, Result result) throws JAXBException, IOException;
}
