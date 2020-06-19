package mark2;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import robocode.AdvancedRobot;
import robocode.Bullet;

import java.io.FileWriter;
import java.util.HashMap;

public class FieldRecognitionRobot extends AdvancedRobot {

    private static class Data {
        private String targetName;
        private double targetBearing;
        private double targetHeading;
        private double targetVelocity;
        private double power;
        private double distance;
        private double bearing;
        private double heading;
        private int hit;

        public Data(String targetName, double targetBearing, double targetHeading, double targetVelocity, double power,
                    double distance, double bearing, double heading) {
            this.targetName = targetName;
            this.targetBearing = targetBearing;
            this.targetHeading = targetHeading;
            this.targetVelocity = targetVelocity;
            this.power = power;
            this.distance = distance;
            this.bearing = bearing;
            this.heading = heading;
            hit = 0;
        }

        public String getTargetName() {
            return targetName;
        }

        public void setTargetName(String targetName) {
            this.targetName = targetName;
        }

        public double getTargetBearing() {
            return targetBearing;
        }

        public void setTargetBearing(double targetBearing) {
            this.targetBearing = targetBearing;
        }

        public double getTargetHeading() {
            return targetHeading;
        }

        public void setTargetHeading(double targetHeading) {
            this.targetHeading = targetHeading;
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

        public double getBearing() {
            return bearing;
        }

        public void setBearing(double bearing) {
            if (bearing > 180 || bearing < -180) {
                throw new IllegalArgumentException("Invalid hit value: It must be between -180 and 180!");
            }
            this.bearing = bearing;
        }

        public double getHeading() {
            return heading;
        }

        public void setHeading(double heading) {
            this.heading = heading;
        }

        public int getHit() {
            return hit;
        }

        public void setHit(int hit) throws IllegalArgumentException {
            if (hit != 0 || hit != 1) {
                throw new IllegalArgumentException("Invalid hit value: It must be 0 or 1!");
            }
            this.hit = hit;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "targetName='" + targetName + '\'' +
                    ", targetBearing=" + targetBearing +
                    ", targetHeading=" + targetHeading +
                    ", targetVelocity=" + targetVelocity +
                    ", power=" + power +
                    ", distance=" + distance +
                    ", bearing=" + bearing +
                    ", heading=" + heading +
                    ", hit=" + hit +
                    '}';
        }
    }

    FileWriter fileWriter;

    HashMap<Bullet, Data> dataset = new HashMap<>();
}
