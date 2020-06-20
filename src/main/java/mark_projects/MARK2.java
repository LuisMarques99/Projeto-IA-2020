package mark_projects;

import hex.genmodel.MojoModel;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import impl.UIConfiguration;
import interf.IPoint;
import performance.EvaluateFire;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
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
    private String[] modelVariables;
    private java.util.List<Rectangle> obstacles;
    public static UIConfiguration conf;
    private HashMap<String, Rectangle> enemies;
    //utilizada par associar inimigos a retângulos e permitir remover retângulos de inimigos já desatualizados


    @Override
    public void run() {
        super.run();
        try {
            model = MojoModel.load(Paths.get(System.getProperty("user.dir"), "h2o",
                    "models", "drf_100_50_10fold_battle_results.zip").toString());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        predictorModel = new EasyPredictModelWrapper(model);
        modelVariables = model._names;

        evaluateFire = new EvaluateFire(NAME);
        obstacles = new ArrayList<>();
        enemies = new HashMap<>();
        conf = new UIConfiguration((int) getBattleFieldWidth(), (int) getBattleFieldHeight(), obstacles);

        while (true) {
            setAhead(30);
            setTurnRight(20);
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        double targetPosX = Utils.getEnemyCoordinates(this, event.getBearing(), event.getDistance()).getX();
        double targetPosY = Utils.getEnemyCoordinates(this, event.getBearing(), event.getDistance()).getY();

        RowData rowData = new RowData();
        rowData.put("Target Name", event.getName());
        rowData.put("Target Pos X", targetPosX);
        rowData.put("Target Pos Y", targetPosY);
        rowData.put("Target Heading", event.getHeading());
        rowData.put("Target Velocity", event.getVelocity());
        rowData.put("Distance", event.getDistance());

    }
}