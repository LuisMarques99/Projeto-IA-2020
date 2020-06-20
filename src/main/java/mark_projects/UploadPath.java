package mark_projects;

import impl.UIConfiguration;
import interf.IPoint;
import interf.IUIConfiguration;
import maps.Maps;
import performance.Evaluate;
import viewer.PathViewer;

import java.util.List;

public class UploadPath {
    public static IUIConfiguration conf;

    public static void main(String[] args) {
        int mapID = 3; //mudar aqui!
        int numberOfPaths = 4; //mudar aqui!

        try {
            conf = Maps.getMap(mapID);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        List<IPoint> points;
        points = GeneticAlgorithm.markGeneticAlgorithm(5, numberOfPaths, 0.05, (UIConfiguration) conf);

        Evaluate eval = new Evaluate(numberOfPaths, mapID, "C´mon Champ! It´s warmup time.");
        eval.addSolution(points, GeneticAlgorithm.getGeneration());

        if (eval.submit())
            System.out.println("> Resultado submetido para o servidor");
        else
            System.out.println("> Erro a submeter resultado.");


        PathViewer pv = new PathViewer(conf);
        pv.paintPath(points);
    }
}
