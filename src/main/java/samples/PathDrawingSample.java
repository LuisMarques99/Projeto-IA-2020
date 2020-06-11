package samples;

import impl.UIConfiguration;
import interf.IUIConfiguration;
import maps.Maps;
import mark_projects.GeneticAlgorithm;
import viewer.PathViewer;
import impl.Point;
import interf.IPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Exemplo que mostra como desenhar um caminho no visualizador.
 */
public class PathDrawingSample {
    public static IUIConfiguration conf;

    public static void main(String[] args) {

        try {
            conf = Maps.getMap(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        List<IPoint> points = new ArrayList<>();
        points = GeneticAlgorithm.markGeneticAlgorithm(3 , 2000 , 0.5 , (UIConfiguration) conf);
        /*
        points.add(conf.getStart());
        points.add(new Point(200, 200));
        points.add(new Point(250, 500));
        points.add(new Point(300, 350));
        points.add(conf.getEnd());
         */

        PathViewer pv = new PathViewer(conf);

        pv.setFitness(9999);
        pv.setStringPath("(ponto1, ponto2, bla bla bla...)");

        //quando utilizado dentro de um ciclo permite ir atualizando o desenho e ver o algoritmo a progredir
        //por exemplo: desenhar o melhor caminho de cada geração
        /*
        pv.paintPath(points);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
        points.add(new Point(500, 500));
        points.add(new Point(600, 700));
         */
        pv.paintPath(points);

    }
}
