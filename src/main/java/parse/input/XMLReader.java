package parse.input;

import model.order.OrderInformation;
import model.production.Production;
import model.production.WorkingDay;
import model.result.Result;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.stream.Collectors;

/**
 * <b>Читалка для XML</b>
 * <p>Сюда добавлять все необходимое для чтения xml файлов (либо в папку parse.input, если понадобиться создать отдельные классы)</p>
 */
public class XMLReader implements Reader {


    @Override
    public Production readProductionFile(String productionFileName) {
        Production production = (Production) readXmlFile(productionFileName, Production.class);
        for(short dayNumber = 1; dayNumber < 8; dayNumber++) {
            if(!production.getSchedule().checkWorkDayInScheduleByDayNumber(dayNumber)) {
                production.getSchedule().getWeek().add(new WorkingDay(dayNumber, LocalTime.MIN, LocalTime.MIN, false));
            }
        }
        return production;
    }


    @Override
    public OrderInformation readOrderFile(String orderFileName) {
        return (OrderInformation) readXmlFile(orderFileName, OrderInformation.class);
    }

    @Override
    public Result readResultFile(String resultFileName) {
        return (Result) readXmlFile(resultFileName, Result.class);
    }

    private Object readXmlFile(String fileName, Class comingClass) {
        if(fileName == null)
            return null;

        try {
            JAXBContext context = JAXBContext.newInstance(comingClass);

            String collect = (new BufferedReader(
                    new InputStreamReader(Files.newInputStream(Paths.get(fileName)),
                            StandardCharsets.UTF_8))).lines().collect(Collectors.joining());
            return context.createUnmarshaller().unmarshal(new StringReader(collect));
        } catch (IOException e) {
            throw new RuntimeException("IOException: " + e.getMessage());
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("JAXBException: " + e.getMessage());
        }
    }
}
