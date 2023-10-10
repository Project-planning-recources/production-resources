package generator;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class GeneratorJsonReader {

    private GeneratorJsonReader() {}

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        OBJECT_MAPPER.setDateFormat(df);
    }

    public static GeneratorParameters readGeneratorParameters(String file) throws IOException {
        return OBJECT_MAPPER.readValue(new File(file), GeneratorParameters.class);
    }
}
