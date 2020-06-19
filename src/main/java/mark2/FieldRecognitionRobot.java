package mark2;

import robocode.*;
import utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FieldRecognitionRobot extends AdvancedRobot {

    private static class Data {
        private String targetName;
        private double targetPosX;
        private double targetPosY;
        private double targetVelocity;
        private double power;
        private double distance;
        private int hit;

        public Data(String targetName, double targetPosX, double targetPosY, double targetVelocity, double power,
                    double distance) {
            this.targetName = targetName;
            this.targetPosX = targetPosX;
            this.targetPosY = targetPosY;
            this.targetVelocity = targetVelocity;
            this.power = power;
            this.distance = distance;
            hit = 0;
        }

        public String getTargetName() {
            return targetName;
        }

        public void setTargetName(String targetName) {
            this.targetName = targetName;
        }

        public double getTargetPosX() {
            return targetPosX;
        }

        public void setTargetPosX(double targetPosX) {
            this.targetPosX = targetPosX;
        }

        public double getTargetPosY() {
            return targetPosY;
        }

        public void setTargetPosY(double targetPosY) {
            this.targetPosY = targetPosY;
        }

        public double getTargetVelocity() {
            return targetVelocity;
        }

        public void setTargetVelocity(double targetVelocity) {
            this.targetVelocity = targetVelocity;
        }

        public double getPower() {
            return power;
        }

        public void setPower(double power) {
            this.power = power;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public int getHit() {
            return hit;
        }

        public void setHit(int hit) {
//            if (hit != 0 || hit != 1) {
//                System.err.println("ERROR! The hit value must be 0 or 1!");
//            }
            this.hit = hit;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "targetName='" + targetName + '\'' +
                    ", targetPosX=" + targetPosX +
                    ", targetPosY=" + targetPosY +
                    ", targetVelocity=" + targetVelocity +
                    ", power=" + power +
                    ", distance=" + distance +
                    ", hit=" + hit +
                    '}';
        }
    }

    CSVFileWriter csvWriter;

    HashMap<Bullet, Data> bulletsMap = new HashMap<>();

    List<String[]> dataList = new ArrayList<>();

    @Override
    public void run() {
        super.run();

        // Writes file header
        dataList.add(new String[]{"Target Name", "Target Pos X", "Target Pos Y", "Target Velocity",
                "Power", "Distance", "Hit"});

        try {
            csvWriter = new CSVFileWriter("battleResults.csv");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        while (true) {
//            setTurnRadarRight(360);
//            turnGunRight(360);
//            turnGunRightRadians(Double.POSITIVE_INFINITY);
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
        else if (event.getDistance() < 500) bullet = fireBullet(2);
        else bullet = fireBullet(1);

        if (bullet == null) System.out.println("Didn't fire!");
        else {
            System.out.println("Fired at " + event.getName());

            double targetPosX = Utils.getEnemyCoordinates(this, event.getBearing(), event.getDistance()).getX();
            double targetPosY = Utils.getEnemyCoordinates(this, event.getBearing(), event.getDistance()).getY();
            bulletsMap.put(bullet, new Data(event.getName(), targetPosX, targetPosY, event.getVelocity(),
                    bullet.getPower(), event.getDistance()));
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
                String.valueOf(data.getTargetPosY()), String.valueOf(data.getTargetVelocity()),
                String.valueOf(data.getPower()), String.valueOf(data.getDistance()),
                String.valueOf(data.getHit())});

        bulletsMap.remove(event.getBullet());
    }

    @Override
    public void onBulletMissed(BulletMissedEvent event) {
        super.onBulletMissed(event);

        Data data = bulletsMap.get(event.getBullet());
        data.setHit(0);

        dataList.add(new String[]{data.getTargetName(), String.valueOf(data.getTargetPosX()),
                String.valueOf(data.getTargetPosY()), String.valueOf(data.getTargetVelocity()),
                String.valueOf(data.getPower()), String.valueOf(data.getDistance()),
                String.valueOf(data.getHit())});

        bulletsMap.remove(event.getBullet());
    }

    @Override
    public void onBulletHitBullet(BulletHitBulletEvent event) {
        super.onBulletHitBullet(event);

        Data data = bulletsMap.get(event.getBullet());
        data.setHit(0);

        dataList.add(new String[]{data.getTargetName(), String.valueOf(data.getTargetPosX()),
                String.valueOf(data.getTargetPosY()), String.valueOf(data.getTargetVelocity()),
                String.valueOf(data.getPower()), String.valueOf(data.getDistance()),
                String.valueOf(data.getHit())});

        bulletsMap.remove(event.getBullet());
    }

    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        super.onRoundEnded(event);
        dataList.add(new String[]{"Ronda: ", String.valueOf(event.getRound() + 1)});
        System.out.println(dataList.toString());
    }

    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        super.onBattleEnded(event);
        System.out.println(dataList.toString());
        try {
            csvWriter.writeAtOnce(dataList);
            csvWriter.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
