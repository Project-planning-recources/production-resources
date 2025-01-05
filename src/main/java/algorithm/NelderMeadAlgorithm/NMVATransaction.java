package algorithm.NelderMeadAlgorithm;

import org.json.JSONObject;
import parse.input.XMLReader;
import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.output.result.OutputResult;
import util.Criterion;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class NMVATransaction {
    private static Socket clientSocket;
    private static BufferedReader in;
    private static BufferedWriter out;

    NMVABestParamsHandler statistics;

    InputOrderInformation orderFile;
    NelderMeadVariatorAlgorithm algo;

    public NMVATransaction() throws Exception {
        if (clientSocket == null || clientSocket.isClosed()) {
            clientSocket = new Socket("localhost", 5000);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        }
    }

    public void SetHyperParameters(ArrayList<Double> hyperParams, String inputProductionPath, String inputOrdersPath) {
        XMLReader Reader = new XMLReader();
        InputProduction inputProduction = Reader.readProductionFile(inputProductionPath);
        orderFile = Reader.readOrderFile(inputOrdersPath);
        algo = new NelderMeadVariatorAlgorithm(inputProduction, orderFile.getOrders(), null,
                "candidates", 1, 15);
        algo.setHyperParameters(hyperParams.get(0), hyperParams.get(1), hyperParams.get(2));
    }

    public double GetCriterion() throws Exception {
        OutputResult result = algo.start();
        return Criterion.getCriterion(orderFile, result);
    }

    private void ProcessOneBatch(int trialsInBatch, String inputProductionPath, String inputOrdersPath) throws Exception {
        for(int i = 0; i < trialsInBatch; i++) {
            String receivedJson = in.readLine(); // Читаем строку
            JSONObject serverJson = new JSONObject(receivedJson); // Преобразуем строку в JSON
            System.out.println("Получено от сервера: " + serverJson.toString());

            ArrayList<Double> hyperParams = new ArrayList<>();

            hyperParams.add(Double.parseDouble(serverJson.get("reflection").toString()));
            hyperParams.add(Double.parseDouble(serverJson.get("contraction").toString()));
            hyperParams.add(Double.parseDouble(serverJson.get("expansion").toString()));

            SetHyperParameters(hyperParams, inputProductionPath, inputOrdersPath);
            double response = GetCriterion();

            statistics.AddParamsAndCrit(hyperParams, response);

            // Подготавливаем JSON для отправки
            JSONObject clientJson = new JSONObject();
            clientJson.put("crit", response);

            // Отправляем JSON обратно серверу
            out.write(clientJson.toString() + "\n"); // Отправляем строку с завершающим символом новой строки
            out.flush();
            System.out.println("Отправлено серверу: " + clientJson.toString());
        }
    }

    public void StartTransaction() throws Exception {
        // Варьируемые значения
        int batchSize = 100;
        int trialsInBatch = 1;
        statistics = new NMVABestParamsHandler(10);

        try {
            System.out.println("Начинаем транзакцию");
            JSONObject jsonWithTrialsCount = new JSONObject();
            jsonWithTrialsCount.put("n_trials", batchSize * trialsInBatch);
            out.write(jsonWithTrialsCount.toString() + "\n");
            out.flush();
            for(int batchIndex = 0; batchIndex < batchSize; batchIndex++) {
                System.out.println("Начинаем работу над " + batchIndex + " задачей");
                String inputProductionPath = "./data/train_data/" + batchSize + "_production.xml";
                String inputOrdersPath = "./data/train_data/" + batchSize + "_orders.xml";
                ProcessOneBatch(trialsInBatch, inputProductionPath, inputOrdersPath);
            }
            System.out.println("Завершаем транзакцию");
        } finally {
            clientSocket.close(); // Закрываем сокет после выполнения
            statistics.SaveBenchmarkCSV();
            statistics.SaveBenchmark();
        }
    }
}
