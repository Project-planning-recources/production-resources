package parse.output;

import model.result.Result;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;

public class XMLWriter implements Writer{

    @Override
    public void writeResultFile(String resultFileName, Result result) {
        if(resultFileName == null)
            return;

        try {
            JAXBContext context = JAXBContext.newInstance(Result.class);
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            mar.marshal(result, new File(resultFileName));
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new RuntimeException("JAXBException: " + e.getMessage());
        }
    }
}
