package samples;

import impl.Point;
import impl.UIConfiguration;
import interf.IPoint;
import performance.EvaluateFire;
import robocode.Robot;
import robocode.*;
import robocode.control.events.*;
import robocode.control.events.RoundEndedEvent;
import utils.Utils;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RobotDoSenhorProfessor extends AdvancedRobot {
    EvaluateFire ef;

    @Override
    public void run() {
        super.run();
        //TODO: instanciar a classe EvaluateFire com o nome da equipa
        ef = new EvaluateFire("C'mon Champ! Warmup time!");


        while (true) {
            this.setAhead(100);
            this.setTurnRight(100);

            this.execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        super.onScannedRobot(event);

        fire(2);

        //TODO: usar este método sempre que é feito scan a um robot
        ef.addScanned(event);
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        super.onBulletHit(event);

        //TODO: usar este método sempre que acertam num robot
        ef.addHit(event);
    }


    //TODO: override deste método
    @Override
    public void onBattleEnded(BattleEndedEvent event) {
        super.onBattleEnded(event);

        //TODO: usar este método no final da batalha
        ef.submit(event.getResults());
    }
}