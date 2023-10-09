package parse.output;

import algorithm.model.result.Result;
import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

public class XMLWriter implements Writer{

    @Override
    public void writeResultFile(String resultFileName, OutputResult result) {
        if(resultFileName == null)
            return;

        try {
            JAXBContext context = JAXBContext.newInstance(OutputResult.class);
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            mar.marshal(result, new File(resultFileName));
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("JAXBException: " + e.getMessage());
        }
    }

    @Override
    public void writeProductionFile(String productionFileName, InputProduction inputProduction) {
        if(productionFileName == null)
            return;

        try {
            JAXBContext context = JAXBContext.newInstance(InputProduction.class);
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            mar.marshal(inputProduction, new File(productionFileName));
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("JAXBException: " + e.getMessage());
        }
    }

    @Override
    public void writeOrderInformationFile(String orderInformationFileName, InputOrderInformation inputOrderInformation) {
        if(orderInformationFileName == null)
            return;

        try {
            JAXBContext context = JAXBContext.newInstance(InputOrderInformation.class);
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            mar.marshal(inputOrderInformation, new File(orderInformationFileName));
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("JAXBException: " + e.getMessage());
        }
    }
}
