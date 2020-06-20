package mark_projects;

import hex.genmodel.MojoModel;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.prediction.MultinomialModelPrediction;
import impl.UIConfiguration;
import interf.IPoint;
import performance.EvaluateFire;
import robocode.*;
import utils.Utils;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Esta é a classe onde se irá desenvolver o robot com a capacidade de disparo autonomo
 * Implementacao do codigo com base em machine learning e utilizacao do H2O
 */
public class MARK2 extends AdvancedRobot {

    private final String NAME = "C´mon Champ! It´s warmup time.";
    private EvaluateFire evaluateFire;
    private MojoModel model;
    private EasyPredictModelWrapper predictorModel;
//    private java.util.List<Rectangle> obstacles;
//    public static UIConfiguration conf;
//    private HashMap<String, Rectangle> enemies;
    //utilizada par associar inimigos a retângulos e permitir remover retângulos de inimigos já desatualizados


    @Override
    public void run() {
        super.run();
        try {
//            model = MojoModel.load(Paths.get(System.getProperty("user.dir"), "h2o",
//                    "models", "drf_100_50_10fold_battle_results.zip").toString());
            model = MojoModel.load("C:/Users/luismarques99/OneDrive/MyProjects/ESTG/IA/ProjetoIA2020/h2o/models/drf_100_50_10fold_battle_results.zip");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        predictorModel = new EasyPredictModelWrapper(model);

        evaluateFire = new EvaluateFire(NAME);
//        obstacles = new ArrayList<>();
//        enemies = new HashMap<>();
//        conf = new UIConfiguration((int) getBattleFieldWidth(), (int) getBattleFieldHeight(), obstacles);

        while (true) {
            setAhead(20);
            setTurnRight(10);
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        double targetPosX = Utils.getEnemyCoordinates(this, event.getBearing(), event.getDistance()).getX();
        double targetPosY = Utils.getEnemyCoordinates(this, event.getBearing(), event.getDistance()).getY();

        RowData rowData = new RowData();
//        rowData.put("Target Name", event.getName());
        rowData.put("Target Pos X", targetPosX);
        rowData.put("Target Pos Y", targetPosY);
        rowData.put("Target Heading", event.getHeading());
        rowData.put("Target Velocity", event.getVelocity());
        rowData.put("Distance", event.getDistance());

        if (event.getDistance() < 200) rowData.put("Power", 3.0);
        else if (event.getDistance() < 700) rowData.put("Power", 2.0);
        else rowData.put("Power", 1.0);

        MultinomialModelPrediction prediction;
        String label = "";
        double probability = 0;
        double hitValue = 0;
        try {
            for (int i = 0; i < 2; i++) {
                rowData.put("Hit", (double) i);
                prediction = predictorModel.predictMultinomial(rowData);
                label = prediction.label;
                if (prediction.classProbabilities[prediction.labelIndex] > probability) {
                    probability = prediction.classProbabilities[prediction.labelIndex];
                    hitValue = i;
                }
            }
            if (label.equals(event.getName()) && hitValue == 1.0 && probability > 0.75) {
                Bullet bullet;
                if (event.getDistance() < 200) bullet = fireBullet(3);
                else if (event.getDistance() < 700) bullet = fireBullet(2);
                else bullet = fireBullet(1);
            }
        } catch (PredictException e) {
            System.err.println(e.toString());
        }
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        super.onBulletHit(event);

        evaluateFire.addHit(event);
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        super.onBattleEnded(event);

        evaluateFire.submit(event.getResults());
    }
}