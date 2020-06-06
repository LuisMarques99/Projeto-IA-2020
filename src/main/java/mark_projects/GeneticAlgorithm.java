package mark_projects;

import impl.Point;
import interf.IPoint;
import java.util.List;

/**
 * Esta irá ser a classe utilizada para a construcao do algoritmo genético que vai ser utilizado nos
 * robots MARK1 e MARK3
 */
public class GeneticAlgorithm {

    public static List<IPoint> markGeneticAlgorithm(List<IPoint> points , int populationSize , int maxIterations , double mutationFactor){
        int i = 0;
        double index = 0 , prevIndex = 0;
        List<IPoint> tempList = null; //lista temporária onde vai ser determinado os pontos iniciais

        // A ideia é fazer com que este algoritmo determine os melhores pontos e os adicione a um array do tipo List<IPoint>
        //Estes points.add(...) aqui em baixo sao para apagar...
        points.add(new Point(100,100));
        points.add(new Point(200,200));
        points.add(new Point(250,500));
        points.add(new Point(300,350));

        do{
            tempList.clear();
            //.... Aqui escreve-se o codigo de determinar os pontos e adiciona-se a tempList!

            index = getFitness(tempList);

            //Se o index de fitness registado nesta populacao for maior do que o anterior, entao esta populacao vai ser a seleccionada
            //Caso contrário continua com a populacao anterior, ou seja, nao faz nada!

            if(index > prevIndex){
                points.clear(); //para limpar todos os pontos que tinha anteriormente
                points = tempList;
                prevIndex = index;
            }

            i++;
        } while (i <= maxIterations);

        return points;
    }

    /**
     * Funcao responsavel por determinar o nivel de fitness de um caminho escolhido pelo algoritmo genético
     * @param points escala quantificavel de pontos atribuidos a cada caminho (mais pontos significa melhor fitness)
     * @return quantidade de pontos
     */
    public static double getFitness(List<IPoint> points){
        double fitnessPoints = 0;
        /*
        Se for feito com o modelo de populacao generational significa que metemos aqui todos os pontos de um unico caminho
        para ver qual o seu fitness, pois a seguir, como estamos a utilizar o modelo generational, toda essa populacao vai ser
        substituida. ISTO ADMITINDO QUE UTILIZAMOS ESSE MODELO!
         */
        return fitnessPoints;
    }
}
