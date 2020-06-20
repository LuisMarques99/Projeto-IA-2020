package mark_projects;

import hex.genmodel.MojoModel;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.prediction.MultinomialModelPrediction;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class App {
    public static void main(String[] args) {
        MojoModel model;
        EasyPredictModelWrapper predictorModel;
        String[] modelVariables;

        RowData rowData = new RowData();
        rowData.put("Target Name", "sample.Crazy");
        rowData.put("Target Pos X", 309.3637936278062);
        rowData.put("Target Pos Y", 100.0);
        rowData.put("Target Heading", 100.0);
        rowData.put("Target Velocity", 0.0);
        rowData.put("Distance", 317.1334490334601);
        rowData.put("Power", 2.0);

        try {
            model = MojoModel.load(Paths.get(System.getProperty("user.dir"), "h2o",
                    "models", "drf_100_50_10fold_battle_results.zip").toString());
            System.out.println("Categoria do modelo: " + model.getModelCategory().toString());
            predictorModel = new EasyPredictModelWrapper(model);
//            System.out.println("Categorias existentes: " + Arrays.toString(predictorModel.m.getDomainValues("name")));
            modelVariables = model._names;
            System.out.println(modelVariables.length + " variables:");
            for (String variable : modelVariables) System.out.println(variable);

            MultinomialModelPrediction prediction;
            String label = "";
            double probability = 0;
            double hitValue = 0;
            for (int i = 0; i < 2; i++) {
                rowData.put("Hit", (double) i);
                prediction = predictorModel.predictMultinomial(rowData);
                label = prediction.label;
                if (prediction.classProbabilities[prediction.labelIndex] > probability) {
                    probability = prediction.classProbabilities[prediction.labelIndex];
                    hitValue = i;
                }
            }
            System.out.println("Label: " + label);
            System.out.println("Hit: " + hitValue);
            System.out.println("Probability: " + probability);
            if (hitValue == 1.0 && probability > 0.85) System.out.println("MATEI TUDO!");

//            System.out.println(prediction.label);
//            double probability = prediction.classProbabilities[prediction.labelIndex];
//            System.out.println(probability);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (PredictException e) {
            System.err.println(e);
        }

    }
}
