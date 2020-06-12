package samples;

import impl.UIConfiguration;
import interf.IPoint;
import interf.IUIConfiguration;
import maps.Maps;
import mark_projects.GeneticAlgorithm;
import viewer.PathViewer;

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
        List<IPoint> points;
        points = GeneticAlgorithm.markGeneticAlgorithm(7 , 100 , 0.5 , (UIConfiguration) conf);
        PathViewer pv = new PathViewer(conf);
        pv.paintPath(points);
    }
}