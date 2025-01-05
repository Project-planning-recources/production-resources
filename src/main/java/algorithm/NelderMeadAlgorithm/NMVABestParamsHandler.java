package algorithm.NelderMeadAlgorithm;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import parse.input.XMLReader;
import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import util.Criterion;
import util.Pair;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class NMVABestParamsHandler {
    int sizeOfBatch;

    ArrayList<Pair<InputProduction, InputOrderInformation>> validationSample;
    ArrayList<Double> benchmarkCrits;
    ArrayList<Double> calculatedCrits;

    int batchIndex;
    HashMap<ArrayList<Double>, Double> paramsAndCritHandler;
    Pair<ArrayList<Double>, Double> bestParamsAndCrit;

    public NMVABestParamsHandler(int sizeOfBatch) throws Exception {
        this.sizeOfBatch = sizeOfBatch;

        FillValidationSample();

        calculatedCrits = new ArrayList<>();
        batchIndex = 0;
        paramsAndCritHandler = new HashMap<>();
        bestParamsAndCrit = new Pair<>(new ArrayList<>(), Double.MAX_VALUE);
    }

    private void FillValidationSample() throws Exception {

        int validationCount = 100;

        XMLReader Reader = new XMLReader();
        validationSample = new ArrayList<>();
        benchmarkCrits = new ArrayList<>();
        for (int i = 1; i <= validationCount; i++) {
            InputProduction inputProduction = Reader.readProductionFile("./data/test_data/" + i + "_production.xml");
            InputOrderInformation orderFile = Reader.readOrderFile("./data/test_data/" + i + "_orders.xml");
            NelderMeadVariatorAlgorithm algo = new NelderMeadVariatorAlgorithm(inputProduction, orderFile.getOrders(), null,
                    "candidates", 1, 15);
            OutputResult result = algo.start();
            benchmarkCrits.add(Criterion.getCriterion(orderFile, result));
            validationSample.add(new Pair<>(inputProduction, orderFile));
        }
    }

    public void AddParamsAndCrit(ArrayList<Double> params, Double crit) throws Exception {
        paramsAndCritHandler.put(params, crit);
        if (crit < bestParamsAndCrit.getValue()) {
            bestParamsAndCrit = new Pair<>(params, crit);
        }
        if (paramsAndCritHandler.size() == sizeOfBatch) {
            for (int i = 0; i < validationSample.size(); i++) {
                calculatedCrits.add(CalculateCrit(bestParamsAndCrit.getKey(), i));
            }
            batchIndex++;
            paramsAndCritHandler.clear();
        }
    }

    private Double CalculateCrit(ArrayList<Double> hyperParams, int validationIndex) throws Exception {
        Pair<InputProduction, InputOrderInformation> validation =  validationSample.get(validationIndex);

        NelderMeadVariatorAlgorithm algo = new NelderMeadVariatorAlgorithm(
                validation.getKey(), validation.getValue().getOrders(), null,
                "candidates", 1, 15
        );
        algo.setHyperParameters(hyperParams.get(0), hyperParams.get(1), hyperParams.get(2));
        OutputResult result = algo.start();
        return Criterion.getCriterion(validation.getValue(), result);
    }

    public void SaveBenchmark() throws IOException {
        for (int i = 0; i < batchIndex; i++) {
            FileWriter fileWriter = new FileWriter("./data/test_results/batchTest" + i + ".txt");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (int j = 0; j < validationSample.size(); j++) {
                bufferedWriter.write("(" + j + ")---:");
                bufferedWriter.newLine();
                bufferedWriter.write( "Calculated : " + calculatedCrits.get(i * validationSample.size() + j));
                bufferedWriter.newLine();
                bufferedWriter.write( "Benchmark : " + benchmarkCrits.get(j));
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
        }
    }

    public void SaveBenchmarkCSV() throws IOException {
        for (int i = 0; i < batchIndex; i++) {
            FileWriter fileWriter = new FileWriter("./data/test_results/batchTest_" + i + ".csv");
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            CSVPrinter csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT.withHeader("Calculated", "Benchmark").withDelimiter(';'));
            for (int j = 0; j < validationSample.size(); j++) {
                csvPrinter.printRecord(calculatedCrits.get(i * validationSample.size() + j).toString(), benchmarkCrits.get(j).toString());
            }
            csvPrinter.flush();
            bufferedWriter.close();
        }
    }
}
