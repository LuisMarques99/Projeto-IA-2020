package samples;

import impl.UIConfiguration;
import interf.IPoint;
import interf.IUIConfiguration;
import maps.Maps;
import mark_projects.GeneticAlgorithm;
import performance.Evaluate;
import viewer.PathViewer;

import java.util.List;

/**
 * Exemplo que mostra como desenhar um caminho no visualizador.
 */
public class PathDrawingSample {
    public static IUIConfiguration conf;

    public static void main(String[] args) {
        try {
            conf = Maps.getMap(0);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        int numberOfPaths = 2000; //mudar aqui!
        List<IPoint> points;
        points = GeneticAlgorithm.markGeneticAlgorithm(4 , numberOfPaths , 0.05 , (UIConfiguration) conf);

        /*
        Evaluate eval = new Evaluate(numberOfPaths , 1 , "C´mon Champ! It´s warmup time.");
        eval.addSolution(points , GeneticAlgorithm.getGeneration());

        if(eval.submit())
            System.out.println("> Resultado submetido para o servidor");
        else
            System.out.println("> Erro a submeter resultado.");
         */

        PathViewer pv = new PathViewer(conf);
        pv.paintPath(points);
    }
}