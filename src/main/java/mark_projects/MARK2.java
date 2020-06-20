package mark_projects;

import hex.genmodel.MojoModel;
import hex.genmodel.easy.EasyPredictModelWrapper;
import robocode.AdvancedRobot;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Esta é a classe onde se irá desenvolver o robot com a capacidade de disparo autonomo
 * Implementacao do codigo com base em machine learning e utilizacao do H2O
 */
public class MARK2 extends AdvancedRobot {
    EasyPredictModelWrapper model;

    @Override
    public void run() {
        super.run();

        try {
            model = new EasyPredictModelWrapper(MojoModel.load(Paths.get(System.getProperty("user.dir"),
                    "h2o","models", "drf_100_50_10fold_battle_results.zip").toString()));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}