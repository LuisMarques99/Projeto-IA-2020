package mark2;

import robocode.AdvancedRobot;
import robocode.Bullet;

import java.io.FileWriter;
import java.util.HashMap;

public class FieldRecognitionRobot extends AdvancedRobot {

    private static class Data {
        String eventName;

    }

    FileWriter fileWriter;

    HashMap<Bullet, Data> dataset = new HashMap<>();
}
