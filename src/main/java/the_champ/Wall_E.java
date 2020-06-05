package the_champ;

import impl.Point;
import impl.UIConfiguration;
import interf.IPoint;
import robocode.Robot;
import robocode.*;
import utils.Utils;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Wall_E extends AdvancedRobot {

    private static class Dados {
        String nome;
        Double distancia;

        /**
         * Data to be stored in log_robocode.txt file
         * @param nome name of the robot
         * @param distancia distance to the robot
         */
        public Dados(String nome, Double distancia) {
            this.nome = nome;
            this.distancia = distancia;
        }
    }

    FileWriter fw;
    HashMap<Bullet, Dados> balasNoAr = new HashMap<>();

    /**
     * lista de obstáculos, preenchida ao fazer scan
     */
    private java.util.List<Rectangle> obstacles;

    public static UIConfiguration conf;
    private List<IPoint> points;

    public int enemy_count = 0;

    //variável que contém o ponto atual para o qual o robot se está a dirigir
    private int currentPoint = -1;

    /**
     * utilizada par associar inimigos a retângulos e permitir remover retângulos de inimigos já desatualizados
     */
    private HashMap<String, Rectangle> inimigos;

    /**
     * Metodo que inicializa o robot
     * Metodo que inicializa o FileWriter para escrever em ficheiro
     * Metodo que inicializa os arrays de obstaculos
     */
    @Override
    public void run() {
        super.run();
        setBodyColor(Color.red);
        obstacles = new ArrayList<>();
        inimigos = new HashMap<>();
        conf = new UIConfiguration((int) getBattleFieldWidth(), (int) getBattleFieldHeight(), obstacles);

        try {
            fw = new FileWriter("log_robocode.txt" , true);
            System.out.println("Escrita ativada!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //scan inicial para ver a quantidade de inimigos/obstaculos
        this.turnRadarRight(720);
        enemy_count = inimigos.size();
        System.out.println("Quantidade de inimigos encontrados: " + enemy_count);
        while (true) {
            turnGunRightRadians(Double.POSITIVE_INFINITY);
            //se se está a dirigir para algum ponto
            if (currentPoint >= 0) {
                IPoint ponto = points.get(currentPoint);
                //se já está no ponto ou lá perto...
                if (Utils.getDistance(this, ponto.getX(), ponto.getY()) < 2){
                    currentPoint++;
                    //se chegou ao fim do caminho
                    if (currentPoint >= points.size()){
                        currentPoint = -1;
                    }
                }
                advancedRobotGoTo(this, ponto.getX(), ponto.getY());
            }
            execute();
        }
    }

    /**
     * Metodo responsavel por decidir as accoes a tomar quando faz scan de um inimigo
     * @param event evento que determina se um robot foi descoberto pelo scan
     */
    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        //Adicionar o inimigo á lista dos inimigos descobertos pelo scan
        Point2D.Double ponto = getEnemyCoordinates(this, event.getBearing(), event.getDistance());
        ponto.x -= this.getWidth() * 2.5 / 2;
        ponto.y -= this.getHeight() * 2.5 / 2;

        Rectangle rect = new Rectangle((int) ponto.x, (int) ponto.y, (int) (this.getWidth() * 2.5),
                (int) (this.getHeight() * 2.5));

        if (inimigos.containsKey(event.getName())) //se já existe um retângulo deste inimigo
            obstacles.remove(inimigos.get(event.getName()));//remover da lista de retângulos

        obstacles.add(rect);
        inimigos.put(event.getName(), rect);

        int count = 0;
        int count2 = 0;

        //o robot irá dar prioridade de ataque aos robots que nao se mexem
        for(int a = 1 ; a <= enemy_count + 1 ; a++){
            String number = String.valueOf(a);
            if(event.getName().equals("sample.SittingDuck") || event.getName().equals("sample.SittingDuck (" + number + ")") ||
            event.getName().equals("sample.Fire") || event.getName().equals("sample.Fire (" + number + ")") || event.getName().equals("sample.Target")
            || event.getName().equals("sample.Target (" + number + ")")) {
                Bullet b1 = fireBullet(3);
                if(b1 != null){
                    System.out.println("Firing heavy bullets of power: 3");
                    balasNoAr.put(b1, new Dados(event.getName(), event.getDistance()));
                }
                count ++;
            }
        }

        if(count == 0){
            //em seguida da prioridade aos robots com menor capacidade de movimentacao
            for(int a = 1 ; a < enemy_count + 1 ; a++){
                String number = String.valueOf(a);
                if(event.getName().equals("sample.MyFirstJuniorRobot") || event.getName().equals("sample.MyFirstJuniorRobot (" + number + ")") ||
                event.getName().equals("sample.RamFire") || event.getName().equals("sample.RamFire (" + number + ")") || event.getName().equals("sample.SpinBot")
                || event.getName().equals("sample.SpinBot (" + number + ")")){
                    Bullet b1 = fireBullet(2);
                    if(b1 != null){
                        System.out.println("Firing medium bullets of power: 2");
                        balasNoAr.put(b1, new Dados(event.getName(), event.getDistance()));
                    }
                    count2++;
                }
            }
        }
        if(count2 == 0){
            //por fim da prioridade aos robots com alta capacidade de movimentacao
            Bullet b1 = fireBullet(1);
            if(b1 != null){
                System.out.println("Firing light bullets of power: 1");
                balasNoAr.put(b1, new Dados(event.getName(), event.getDistance()));
            }
        }
    }

    /**
     * Metodo responsavel por mover o robot para o local seleccionado pelo utilizador
     * Faz uso do algoritmo genetico para determinar o melhor caminho a percorrer pelo robot
     * @param e evento de clique de um periférico pelo utilizador
     */
    @Override
    public void onMouseClicked(MouseEvent e) {
        super.onMouseClicked(e);

        conf.setStart(new impl.Point((int) this.getX(), (int) this.getY()));
        conf.setEnd(new impl.Point(e.getX(), e.getY()));

        /*
         * TODO: Implementar a chamada ao algoritmo genético!
         *
         * */
        System.out.println("Choo Choo!!!");
        points = new ArrayList<>();
        points.add(new impl.Point(100, 100));
        points.add(new impl.Point(200, 200));
        points.add(new impl.Point(250, 500));
        points.add(new Point(300, 350));

        for (int i = 0; i < points.size(); i++) {
            advancedRobotGoTo(this, points.get(i).getX(), points.get(i).getY());
        }
        //Utils.advancedRobotGoTo(this, points.get(i).getX(), points.get(i).getY());
    }

    /**
     * Metodo responsavel por escrever em ficheiro a entrada de uma bala que acertou no inimigo
     * @param event evento que determina que uma bala acertou no inimigo
     */
    @Override
    public void onBulletHit(BulletHitEvent event) {
        super.onBulletHit(event);
        Dados d = balasNoAr.get(event.getBullet());
        try {
            //testar se acertei em quem era suposto
            if (event.getName().equals(event.getBullet().getVictim())) {
                fw.append(d.nome + "," + d.distancia + ",acertei\n");
                System.out.println("Acertei! Nome: " + d.nome + "Distancia: " + d.distancia);
            }
            else {
                fw.append(d.nome + "," + d.distancia + ",falhei\n");
                System.out.println("Falhei! Nome: " + d.nome + "Distancia: " + d.distancia);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        balasNoAr.remove(event.getBullet());
    }

    /**
     * Metodo resppnsavel por escrever em ficheiro a entrada de uma bala que falhou o alvo
     * Falha contabilizada como: bala contra a parede do jogo, bala contra outra bala.
     * @param event evento que determina que uma bala falhou o inimigo
     */
    @Override
    public void onBulletMissed(BulletMissedEvent event) {
        super.onBulletMissed(event);
        Dados d = balasNoAr.get(event.getBullet());
        try {
            fw.append(d.nome + "," + d.distancia + ",falhei\n");
            System.out.println("Falhei! Nome: " + d.nome + "Distancia: " + d.distancia);
        } catch (IOException e) {
            e.printStackTrace();
        }
        balasNoAr.remove(event.getBullet());
    }

    /**
     * Metodo responsavel por escrever em ficheiro a entrada de uma bala que acertou noutra bala (contabiliza na mesma
     * como uma falha)
     * @param event evento que determina que uma bala acertou noutra bala
     */
    @Override
    public void onBulletHitBullet(BulletHitBulletEvent event) {
        super.onBulletHitBullet(event);
        Dados d = balasNoAr.get(event.getBullet());
        try {
            fw.append(d.nome + "," + d.distancia + ",falhei\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        balasNoAr.remove(event.getBullet());
    }

    /**
     * Metodo responsavel por fechar o FileWriter para o ficheiro devido ao jogo ter terminado
     * @param event evento que determina o fim do jogo
     */
    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        super.onBattleEnded(event);
        System.out.println("Battle has ended!");
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        return new Point2D.Double((robot.getX() + Math.sin(angle) * distance),
                (robot.getY() + Math.cos(angle) * distance));
    }

    /**
     * ******** TODO: Necessário selecionar a opção Paint na consola do Robot *******
     *
     * @param
     */
    @Override
    public void onPaint(Graphics2D g) {
        super.onPaint(g);

        g.setColor(Color.RED);
        obstacles.stream().forEach(x -> g.drawRect(x.x, x.y, (int) x.getWidth(), (int) x.getHeight()));

        if (points != null) {
            for (int i = 1; i < points.size(); i++)
                drawThickLine(g, points.get(i - 1).getX(), points.get(i - 1).getY(), points.get(i).getX(),
                        points.get(i).getY(), 2, Color.green);
        }
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

        int[] xPoints = new int[4];
        int[] yPoints = new int[4];

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
     * Metodo responsavel por determinar as accoes necessárias a tomar apos a morte do robot no jogo
     * Remove as listagens de inimigos/obstaculos criadas durante o jogo
     * @param event evento que determina se o robot morreu
     */
    @Override
    public void onRobotDeath(RobotDeathEvent event) {
        super.onRobotDeath(event);

        Rectangle rect = inimigos.get(event.getName());
        obstacles.remove(rect);
        inimigos.remove(event.getName());
    }

    /**
     * Dirige o robot (AdvancedRobot) para determinadas coordenadas
     *
     * @param robot o meu robot
     * @param x coordenada x do alvo
     * @param y coordenada y do alvo
     * */
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
}