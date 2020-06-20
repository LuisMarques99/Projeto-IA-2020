package mark_projects;

import hex.genmodel.MojoModel;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.prediction.MultinomialModelPrediction;
import performance.EvaluateFire;
import robocode.*;
import utils.Utils;

import java.io.IOException;

/**
 * Classe que representa a estrutura do robot com a caacidade de disparo autonomo {@link MARK2 Mark2}.
 * Implementacao do codigo com base em machine learning e utilizacao do H2O
 */
public class MARK2 extends AdvancedRobot {

    /**
     * referencia String para o nome da nosso robot
     */
    private final String NAME = "C´mon Champ! It´s warmup time.";

    /**
     * referencia EvaluateFire para o gestor de envio dos dados para as tabelas de classificação
     */
    private EvaluateFire evaluateFire;

    /**
     * referencia MojoModel para o modelo utilizado
     */
    private MojoModel model;

    /**
     * referencia EasyPredictModelWrapper para o gestor de previsões do modelo
     */
    private EasyPredictModelWrapper predictorModel;

    @Override
    public void run() {
        super.run();
        try {
            model = MojoModel.load("C:/Users/luismarques99/OneDrive/MyProjects/ESTG/IA/ProjetoIA2020/h2o/models/drf_100_50_10fold_battle_results.zip");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        predictorModel = new EasyPredictModelWrapper(model);
        evaluateFire = new EvaluateFire(NAME);

        while (true) {
            setAhead(10);
            setTurnRight(5);
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        double targetPosX = Utils.getEnemyCoordinates(this, event.getBearing(), event.getDistance()).getX();
        double targetPosY = Utils.getEnemyCoordinates(this, event.getBearing(), event.getDistance()).getY();

        RowData rowData = new RowData();
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
            if (event.getName().equals("sample.Walls") && event.getVelocity() == 0.0) {
                if (getEnergy() > 40 && event.getDistance() < 200) fireBullet(3);
                else if (getEnergy() > 20 && event.getDistance() < 700) fireBullet(2);
                else fireBullet(1);
            }
            if (label.equals(event.getName()) && hitValue == 1.0 && probability > 0.7) {
                if (getEnergy() > 40 && event.getDistance() < 200) fireBullet(3);
                else if (getEnergy() > 20 && event.getDistance() < 700) fireBullet(2);
                else fireBullet(1);
            }
        } catch (PredictException e) {
            System.err.println(e.toString());
        }

        evaluateFire.addScanned(event);
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
        super.onHitRobot(event);
        setBack(100);
    }

    @Override
    public void onHitWall(HitWallEvent event) {
        super.onHitWall(event);
        setBack(100);
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