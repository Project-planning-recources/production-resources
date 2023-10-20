package parse.input;

import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.input.production.InputWorkingDay;
import parse.output.result.OutputResult;

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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <b>Читалка для XML</b>
 * <p>Сюда добавлять все необходимое для чтения xml файлов (либо в папку parse.input, если понадобиться создать отдельные классы)</p>
 */
public class XMLReader implements Reader {


    @Override
    public InputProduction readProductionFile(String productionFileName) {
        InputProduction inputProduction = (InputProduction) readXmlFile(productionFileName, InputProduction.class);
        for(short dayNumber = 1; dayNumber < 8; dayNumber++) {
            if(Objects.isNull(inputProduction.getSchedule().getWorkDayByDayNumber(dayNumber))) {
                inputProduction.getSchedule().getWeek().add(new InputWorkingDay(dayNumber, LocalTime.MIN, LocalTime.MIN, false));
            }
        }
        return inputProduction;
    }


    @Override
    public InputOrderInformation readOrderFile(String orderFileName) {
        return (InputOrderInformation) readXmlFile(orderFileName, InputOrderInformation.class);
    }

    @Override
    public OutputResult readResultFile(String resultFileName) {
        return (OutputResult) readXmlFile(resultFileName, OutputResult.class);
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
