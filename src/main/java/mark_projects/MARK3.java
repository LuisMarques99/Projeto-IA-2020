package mark_projects;

import hex.genmodel.MojoModel;
import hex.genmodel.easy.EasyPredictModelWrapper;
import hex.genmodel.easy.RowData;
import hex.genmodel.easy.exception.PredictException;
import hex.genmodel.easy.prediction.MultinomialModelPrediction;
import impl.Point;
import impl.UIConfiguration;
import interf.IPoint;
import performance.EvaluateFire;
import robocode.*;
import robocode.Robot;
import utils.Utils;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Esta é a classe onde se irá desenvolver o robot final com a capacidade de movimentacao autonoma proveniente
 * do robot {@link MARK1 Mark1} e disparo autonomo proveniente do robot {@link MARK2 Mark2}. Este robot deverá
 * possuir a capacidade de implementacao de ambas as capacidades dos robots anteriores e aplica-las em tempo
 * real no Robocode
 */
public class MARK3 extends AdvancedRobot {
    /*
     * lista de obstáculos, preenchida ao fazer scan
     * */
    private java.util.List<Rectangle> obstacles;
    public static UIConfiguration conf;
    private List<IPoint> points;
    private HashMap<String, Rectangle> inimigos; //utilizada par associar inimigos a retângulos e permitir remover retângulos de inimigos já desatualizados

    //variável que contém o ponto atual para o qual o robot se está a dirigir
    private int currentPoint = -1;

    private final String NAME = "C´mon Champ! It´s warmup time.";
    private EvaluateFire evaluateFire;
    private MojoModel model;
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

        obstacles = new ArrayList<>();
        inimigos = new HashMap<>();
        conf = new UIConfiguration((int) getBattleFieldWidth(), (int) getBattleFieldHeight(), obstacles);

        while (true) {
            this.setTurnGunRight(360);

            //se se está a dirigir para algum ponto
            if (points != null) {
                if (currentPoint >= 0) {
                    IPoint ponto = points.get(currentPoint);
                    //se já está no ponto ou lá perto...
                    if (Utils.getDistance(this, ponto.getX(), ponto.getY()) < 2) {
                        currentPoint++;
                        //se chegou ao fim do caminho
                        if (currentPoint >= points.size())
                            currentPoint = -1;
                    }
                    advancedRobotGoTo(this, ponto.getX(), ponto.getY());
                }
            }
            this.execute();
        }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        super.onMouseClicked(e);

        conf.setStart(new impl.Point((int) this.getX(), (int) this.getY()));
        conf.setEnd(new Point(e.getX(), e.getY()));

        points = new ArrayList<>();
        // chamada ao algoritmo genético
        points = GeneticAlgorithm.markGeneticAlgorithm(4, 2000, 0.05, conf);
        if (points != null) {
            System.out.println("> Moving to selected target!");
            currentPoint = 0;
        } else {
            System.out.println("> System could not find any available route...");
        }
    }

    /**
     * ******** TODO: Necessário selecionar a opção Paint na consola do Robot *******
     *
     * @param g g
     */
    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);

        g.setColor(Color.RED);
        obstacles.stream().forEach(x -> g.drawRect(x.x, x.y, (int) x.getWidth(), (int) x.getHeight()));

        if (points != null) {
            for (int i = 1; i < points.size(); i++)
                drawThickLine(g, points.get(i - 1).getX(), points.get(i - 1).getY(), points.get(i).getX(), points.get(i).getY(), 2, Color.green);
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        //determinar se um robot atravessou o caminho que estamos a percorrer e o que fazer nessa situacao
        if (points != null && GeneticAlgorithm.collisionDetection(points, conf)) {
            IPoint lastPoint = points.get(points.size() - 1);

            for (int a = 0; a < obstacles.size(); a++) {
                if (event.getDistance() < 80) {
                    points.clear();
                    currentPoint = -1;
                    break;
                } else if (obstacles.get(a).getBounds2D().contains(lastPoint.getX(), lastPoint.getY())) {
                    if (getDistanceRemaining() < 200) {
                        points.clear();
                        currentPoint = -1;
                        break;
                    }
                } else {
                    conf.setStart(new impl.Point((int) this.getX(), (int) this.getY()));
                    ;
                    points = new ArrayList<>();
                    points = GeneticAlgorithm.markGeneticAlgorithm(4, 1000, 0.05, conf);
                }
            }
        }

        Point2D.Double ponto = getEnemyCoordinates(this, event.getBearing(), event.getDistance());

        ponto.x -= this.getWidth() * 2.5 / 2;
        ponto.y -= this.getHeight() * 2.5 / 2;

        Rectangle rect = new Rectangle((int) ponto.x, (int) ponto.y, (int) (this.getWidth() * 2.5), (int) (this.getHeight() * 2.5));

        if (inimigos.containsKey(event.getName())) //se já existe um retângulo deste inimigo
            obstacles.remove(inimigos.get(event.getName()));//remover da lista de retângulos

        obstacles.add(rect);
        inimigos.put(event.getName(), rect);

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
            if (label.equals(event.getName()) && hitValue == 1.0 && probability > 0.75) {
                if (event.getDistance() < 200)  fireBullet(3);
                else if (event.getDistance() < 700)  fireBullet(2);
                else fireBullet(1);
            }
        } catch (PredictException e) {
            System.err.println(e.toString());
        }
    }

    /**
     * Metodo responsavel por decidir o que fazer aquando a morte de um robot
     *
     * @param event evento que determina que um robot morreu no campo de batalha
     */
    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        super.onRobotDeath(event);

        Rectangle rect = inimigos.get(event.getName());
        obstacles.remove(rect);
        inimigos.remove(event.getName());
    }

    /**
     * Devolve as coordenadas de um alvo
     *
     * @param robot    o meu robot
     * @param bearing  ângulo para o alvo, em graus
     * @param distance distância ao alvo
     * @return coordenadas do alvo
     */
    public static Point2D.Double getEnemyCoordinates(Robot robot, double bearing, double distance) {
        double angle = Math.toRadians((robot.getHeading() + bearing) % 360);

        return new Point2D.Double((robot.getX() + Math.sin(angle) * distance), (robot.getY() + Math.cos(angle) * distance));
    }


    private void drawThickLine(Graphics g, int x1, int y1, int x2, int y2, int thickness, Color c) {
        g.setColor(c);
        int dX = x2 - x1;
        int dY = y2 - y1;

        double lineLength = Math.sqrt(dX * dX + dY * dY);

        double scale = (double) (thickness) / (2 * lineLength);

        double ddx = -scale * (double) dY;
        double ddy = scale * (double) dX;
        ddx += (ddx > 0) ? 0.5 : -0.5;
        ddy += (ddy > 0) ? 0.5 : -0.5;
        int dx = (int) ddx;
        int dy = (int) ddy;

        int xPoints[] = new int[4];
        int yPoints[] = new int[4];

        xPoints[0] = x1 + dx;
        yPoints[0] = y1 + dy;
        xPoints[1] = x1 - dx;
        yPoints[1] = y1 - dy;
        xPoints[2] = x2 - dx;
        yPoints[2] = y2 - dy;
        xPoints[3] = x2 + dx;
        yPoints[3] = y2 + dy;

        g.fillPolygon(xPoints, yPoints, 4);
    }


    /**
     * Dirige o robot (AdvancedRobot) para determinadas coordenadas
     *
     * @param robot o meu robot
     * @param x     coordenada x do alvo
     * @param y     coordenada y do alvo
     */
    public static void advancedRobotGoTo(AdvancedRobot robot, double x, double y) {
        x -= robot.getX();
        y -= robot.getY();

        double angleToTarget = Math.atan2(x, y);
        double targetAngle = robocode.util.Utils.normalRelativeAngle(angleToTarget - Math.toRadians(robot.getHeading()));
        double distance = Math.hypot(x, y);
        double turnAngle = Math.atan(Math.tan(targetAngle));
        robot.setTurnRight(Math.toDegrees(turnAngle));
        if (targetAngle == turnAngle)
            robot.setAhead(distance);
        else
            robot.setBack(distance);
        robot.execute();
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
