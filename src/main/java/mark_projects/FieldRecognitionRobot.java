package mark_projects;

import robocode.*;
import utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Classe que representa a estrutura de um {@link FieldRecognitionRobot Field Recognition Robot} a ser utilizada para a
 * geracao de um dataset para treino de um modelo a ser aplicado posteriormente no robot {@link MARK2 Mark2}
 */
public class FieldRecognitionRobot extends AdvancedRobot {

    /**
     * Classe interna que representa a estrutura de um conjunto de {@link Data Dados} utilizado no
     * {@link FieldRecognitionRobot Field Recognition Robot}
     */
    private static class Data {
        //informacoes a ser guardadas no dataset
        /**
         * referencia String para o nome do robot alvo
         */
        private final String targetName;

        /**
         * referencia double para coordenada X da posicao do robot alvo
         */
        private final double targetPosX;

        /**
         * referencia double para coordenada Y da posicao do robot alvo
         */
        private final double targetPosY;

        /**
         * referencia double para a direcao em que o robot alvo está a apontar
         */
        private final double targetHeading;

        /**
         * referencia souble para a velocidade do robot alvo
         */
        private final double targetVelocity;

        /**
         * referencia double para a potencia da bala disparada
         */
        private final double power;

        /**
         * referencia double para a distancia a que se encontra o robot alvo
         */
        private final double distance;

        /**
         * referencia int com intenção booleana para definir se a bala atingiu o robot alvo (1 - true; 0 - false)
         */
        private int hit;

        /**
         * Cria uma instancia de {@link Data Data} com o volor de hit sendo 0 por defeito
         *
         * @param targetName     String nome do robot alvo
         * @param targetPosX     double coordenada X da posicao do robot alvo
         * @param targetPosY     double coordenada Y da posicao do robot alvo
         * @param targetHeading  double direcao em que o robot alvo está a apontar
         * @param targetVelocity double velocidade do robot alvo
         * @param power          double potencia da bala disparada
         * @param distance       double distancia a que se encontra o robot alvo
         */
        public Data(String targetName, double targetPosX, double targetPosY, double targetHeading,
                    double targetVelocity, double power, double distance) {
            this.targetName = targetName;
            this.targetPosX = targetPosX;
            this.targetPosY = targetPosY;
            this.targetHeading = targetHeading;
            this.targetVelocity = targetVelocity;
            this.power = power;
            this.distance = distance;
            hit = 0;
        }

        /**
         * Retorna o nome do robot alvo
         *
         * @return String nome do robot alvo
         */
        public String getTargetName() {
            return targetName;
        }

        /**
         * Retorna a coordenada X da posicao do robot alvo
         *
         * @return double coordenada X da posicao do robot alvo
         */
        public double getTargetPosX() {
            return targetPosX;
        }

        /**
         * Retorna a coordenada Y da posicao do robot alvo
         *
         * @return double coordenada Y da posicao do robot alvo
         */
        public double getTargetPosY() {
            return targetPosY;
        }

        /**
         * Retorna a direcao em que o robot alvo está a apontar
         *
         * @return double direcao em que o robot alvo está a apontar
         */
        public double getTargetHeading() {
            return targetHeading;
        }

        /**
         * Retorna a velocidade do robot alvo
         *
         * @return double velocidade do robot alvo
         */
        public double getTargetVelocity() {
            return targetVelocity;
        }

        /**
         * Retorna a potencia da bala disparada
         *
         * @return double potencia da bala disparada
         */
        public double getPower() {
            return power;
        }

        /**
         * Retorna a distancia a que se encontra o robot alvo
         *
         * @return double distancia a que se encontra o robot alvo
         */
        public double getDistance() {
            return distance;
        }

        /**
         * Retorna 1 se a bala atingiu o robot alvo ou 0 se a bala não tiver atingido
         *
         * @return int 1 se a bala atingiu o robot alvo ou 0 caso contrario
         */
        public int getHit() {
            return hit;
        }

        /**
         * Define se a bala atingiu o robot alvo (1) ou não (0)
         *
         * @param hit int 1 se a bala atingiu o robot alvo ou 0 caso contrario
         */
        public void setHit(int hit) {
            if (hit != 0 && hit != 1) {
                System.out.println("ERROR! The hit value must be 0 or 1!");
            }
            this.hit = hit;
        }

        /**
         * Retorna um array de strings com os atributos da classe {@link Data Data}
         *
         * @return String[] atributos da classe
         */
        public static String[] getAttributes() {
            return new String[]{"Target Name", "Target Pos X", "Target Pos Y", "Target Heading", "Target Velocity",
                    "Power", "Distance", "Hit"};
        }
    }

    /**
     * referencia CSVFileWriter para o ficheiro utilizado para exportar o dataset gerado
     */
    CSVFileWriter csvWriter;

    /**
     * referencia HashMap<Bullet, Data> para guardar os eventos do tipo {@link Bullet Bullet} com dados do tipo
     * {@link Data Data}
     */
    HashMap<Bullet, Data> bulletsMap = new HashMap<>();

    /**
     * referencia List<String[]> lista de arrays de strings para guardar os dados a serem exportados para o dataset
     */
    List<String[]> dataList = new ArrayList<>();

    @Override
    public void run() {
        super.run();

        while (true) {
            setAhead(30);
            setTurnRight(20);
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        Bullet bullet;
        if (event.getDistance() < 200) bullet = fireBullet(3);
        else if (event.getDistance() >= 200 && event.getDistance() < 700) bullet = fireBullet(2);
        else bullet = fireBullet(1);

        if (bullet != null) {
            double targetPosX = Utils.getEnemyCoordinates(this, event.getBearing(), event.getDistance()).getX();
            double targetPosY = Utils.getEnemyCoordinates(this, event.getBearing(), event.getDistance()).getY();
            bulletsMap.put(bullet, new Data(event.getName(), targetPosX, targetPosY, event.getHeading(),
                    event.getVelocity(), bullet.getPower(), event.getDistance()));
        }
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        super.onBulletHit(event);

        Data data = bulletsMap.get(event.getBullet());
        // Tests if the bullet hits the target
        if (event.getName().equals(event.getBullet().getVictim())) data.setHit(1);
        else data.setHit(0);

        dataList.add(new String[]{data.getTargetName(), String.valueOf(data.getTargetPosX()),
                String.valueOf(data.getTargetPosY()), String.valueOf(data.getTargetHeading()),
                String.valueOf(data.getTargetVelocity()), String.valueOf(data.getPower()),
                String.valueOf(data.getDistance()), String.valueOf(data.getHit())});

        bulletsMap.remove(event.getBullet());
    }

    @Override
    public void onBulletMissed(BulletMissedEvent event) {
        super.onBulletMissed(event);

        Data data = bulletsMap.get(event.getBullet());
        data.setHit(0);

        dataList.add(new String[]{data.getTargetName(), String.valueOf(data.getTargetPosX()),
                String.valueOf(data.getTargetPosY()), String.valueOf(data.getTargetHeading()),
                String.valueOf(data.getTargetVelocity()), String.valueOf(data.getPower()),
                String.valueOf(data.getDistance()), String.valueOf(data.getHit())});

        bulletsMap.remove(event.getBullet());
    }

    @Override
    public void onBulletHitBullet(BulletHitBulletEvent event) {
        super.onBulletHitBullet(event);

        Data data = bulletsMap.get(event.getBullet());
        data.setHit(0);

        dataList.add(new String[]{data.getTargetName(), String.valueOf(data.getTargetPosX()),
                String.valueOf(data.getTargetPosY()), String.valueOf(data.getTargetHeading()),
                String.valueOf(data.getTargetVelocity()), String.valueOf(data.getPower()),
                String.valueOf(data.getDistance()), String.valueOf(data.getHit())});

        bulletsMap.remove(event.getBullet());
    }

    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        super.onRoundEnded(event);
        try {
            dataToCSV();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        super.onBattleEnded(event);
    }

    /**
     * Funcao responsavel por escrever em ficheiro .csv os resultados da atividade do robot em batalho no Robocode
     * Escreve em ficheiro, os resultados dos tiros efetuados aos inimigos e o seu sucesso ou insucesso em acertar
     * nos mesmos.
     *
     * @throws IOException
     */
    private void dataToCSV() throws IOException {
        csvWriter = new CSVFileWriter("battle_results.csv", Data.getAttributes());
        csvWriter.writeAtOnce(dataList);
        csvWriter.close();
    }
}
