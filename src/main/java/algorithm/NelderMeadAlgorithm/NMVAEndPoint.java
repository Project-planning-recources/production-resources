package algorithm.NelderMeadAlgorithm;

import parse.input.Reader;
import parse.input.XMLReader;
import parse.input.order.InputOrderInformation;
import parse.input.production.InputProduction;
import parse.input.order.InputOrder;
import parse.output.result.OutputResult;
import util.Criterion;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class NMVAEndPoint {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;

    InputProduction inputProduction;
    InputOrderInformation orderFile;

    NelderMeadVariatorAlgorithm algo;


    public NMVAEndPoint(String inputProductionPath, String inputOrdersPath) throws IOException {
        XMLReader Reader = new XMLReader();
        this.inputProduction = Reader.readProductionFile(inputProductionPath);
        this.orderFile = Reader.readOrderFile(inputOrdersPath);

        clientSocket = new Socket("localhost", 5000);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
    }

    public void SetHyperParameters(ArrayList<Double> hyperParams) {
         algo = new NelderMeadVariatorAlgorithm(inputProduction, orderFile.getOrders(), null,
                 "candidates", 1, 15);
         algo.setHyperParameters(hyperParams.get(0), hyperParams.get(1), hyperParams.get(2));
    }

    public double GetCriterion() throws Exception {
        OutputResult result = algo.start();
        return Criterion.getCriterion(orderFile, result);
    }

    public void AcceptAndResponse() throws Exception {
        out.write("Response" + "\n");
        out.flush();

        String[] request = in.readLine().split("/");
        ArrayList<Double> hyperParams = new ArrayList<Double>();
        for (String param : request) {
            hyperParams.add(Double.parseDouble(param));
        }
        SetHyperParameters(hyperParams);
        double response = GetCriterion();

        out.write("\n" + response + "\n");
        out.flush();
    }
}
