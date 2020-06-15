package mark_projects;

import impl.Point;
import impl.UIConfiguration;
import interf.IPoint;
import robocode.AdvancedRobot;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;

/**
 * Esta irá ser a classe utilizada para a construcao do algoritmo genético que vai ser utilizado nos
 * robots MARK1 e MARK3
 */
public class GeneticAlgorithm extends AdvancedRobot{

    private static ArrayList<List<IPoint>> pointsList = new ArrayList<>(); //lista de listas de pontos
    private static List<IPoint> finalPointList = new ArrayList<>(); //lista final onde vai guardado o melhor caminho a seguir

    public static int getGeneration() {
        return generation;
    }

    public static void setGeneration(int generation) {
        GeneticAlgorithm.generation = generation;
    }

    private static int generation = 0;

    /**
     * Funcao responsavel por determinar um caminho valido a percorrer pelo robot desde o ponto de partida ao ponto de chegada
     * Algoritmo genetico determina o caminho valido mais curto encontrado entre 2 pontos usando tecnicas de cruzamento, selecao
     * e mutacao genetica
     * @param numberOfPoints quantidade de pontos maximo existente no caminho a percorrer
     * @param maxIterations numero de iteracoes maxima permitida para o algoritmo gerar novas populacoes
     * @param mutationRate taxa de mutacao aplicavel ao melhor caminho encontrado
     * @param conf configuracao inicial do mapa carregado (dimensao, obstaculos)
     * @return retorna o melhor caminho calculado a ser percorrido pelo robot
     */
    public static List<IPoint> markGeneticAlgorithm(int numberOfPoints , int maxIterations , double mutationRate , UIConfiguration conf) {
        int i = 0 , a = 1;
        double index, prevIndex = Integer.MIN_VALUE;
        List<IPoint> points = new ArrayList<>(); //lista temporária onde vai ser carregado novas populacoes a cada iteracao
        Random rand = new Random();

        do{
            int x1Value = conf.getStart().getX();
            int y1Value = conf.getStart().getY();
            points.add(new Point(x1Value,y1Value)); //adicionar o ponto de partida do robot como ponto de partida

            do{
                int x2Value = rand.nextInt(conf.getWidth() - 20);
                x2Value += 20;
                int y2Value = rand.nextInt(conf.getHeight() - 20);
                y2Value += 20;

                //se o ponto onde nos encontramos for o ultimo ponto entao adiciona esse como o ponto seleccionado pelo utilizador
                if(a == numberOfPoints - 1){
                    x2Value = conf.getEnd().getX();
                    y2Value = conf.getEnd().getY();
                }
                points.add(new Point(x2Value , y2Value));
                a++;
            } while(a < numberOfPoints);
            a = 1;
            index = getFitness(points , conf);

            /**
             * Se o index de fitness registado nesta populacao for maior do que o anterior, entao esta populacao vai
             * ser a seleccionada caso contrário continua com a populacao anterior, ou seja, nao faz nada!
             * 1º selecao feita aqui:
             */
            if(index > prevIndex){
                finalPointList.clear();
                for(int j = 0 ; j < points.size() ; j++){ finalPointList.add(points.get(j)); }
                prevIndex = index;
            }
            pointsList.add(points); //guardar todos os caminhos gerados (podem ser validos ou inválidos)
            points = new ArrayList<>();
            i++;
        } while (i < maxIterations);
        setGeneration(generation++);
        reproducePopulation(pointsList , conf); //2º selecao feita aqui
        mutateList(finalPointList , mutationRate , conf); //mutacao da melhor selecao feita aqui
        return finalPointList;
    }

    /**
     * Funcao responsavel por determinar o nivel de fitness de um caminho escolhido pelo algoritmo genético
     * @param points escala quantificavel de pontos atribuidos a cada caminho (mais pontos significa melhor fitness)
     * @param conf configuracao do mapa carregado
     * @return quantidade de pontos
     */
    public static double getFitness(List<IPoint> points , UIConfiguration conf){
        //se o caminho resulta num caminho que nao passa por cima de nenhuma posicao inimiga, entao ganha 10000 pontos de fitness automáticamente.
        double fitnessPoints = 0;
        double distance = 0;

        if(collisionDetection(points , conf) == false){
            fitnessPoints = 10000;
        }
        distance = 0;

        for(int a = 0 ; a < points.size() - 1 ; a++){
            distance += getDistanceBetweenPoints(points.get(a) , points.get(a + 1));
        }

        fitnessPoints -= distance;
        /*
        Se for feito com o modelo de populacao generational significa que metemos aqui todos os pontos de um unico caminho
        para ver qual o seu fitness, pois a seguir, como estamos a utilizar o modelo generational, toda essa populacao vai ser
        substituida. ISTO ADMITINDO QUE UTILIZAMOS ESSE MODELO!
         */
        return fitnessPoints;
    }

    /**
     * Funcao responsavel por retornar o valor double de distancia entre 2 pontos na lista de pontos do caminho a seguir pelo robot
     * @param point1 ponto de partida
     * @param point2 ponto de chegada
     * @return valor double de distancia entre os dois pontos
     */
    public static double getDistanceBetweenPoints(IPoint point1 , IPoint point2){
        int x1 = point1.getX();
        int y1 = point1.getY();
        int x2 = point2.getX();
        int y2 = point2.getY();
        double distance;
        double yAxis;
        double xAxis;

        if(x1 > x2 && y1 > y2){ //↙
            yAxis = y1 - y2;
            xAxis = x1 - x2;
        }
        else if(x1 > x2 && y2 > y1){ //↖
            yAxis = y2 - y1;
            xAxis = x1 - x2;
        }
        else if(x2 > x1 && y1 > y2){ //↘
            yAxis = y1 - y2;
            xAxis = x2 - x1;
        }
        else { //↗
            yAxis = y2 - y1;
            xAxis = x2 - x1;
        }
        distance = Math.sqrt((yAxis * yAxis) + (xAxis * xAxis));
        return distance;
    }

    /**
     * Funcao responsavel por retornar uma lista inicialmente válida, noutra lista tambem válida mas com um fitness superior
     * a lista de pontos que entrou como parametro.
     * @param list a lista válida original
     * @param mutationRate  taxa de mutacao a aplicar a um ponto aleatorio na lista
     * @param configuration configuracao inicial do mapa
     * @return uma lista válida mutada se possivel. retorna a lista original se impossivel.
     */
    public static List<IPoint> mutateList(List<IPoint> list , double mutationRate , UIConfiguration configuration){
        double fit1 = getFitness(finalPointList , configuration);

        System.out.println("Fitness inicial: " + fit1);
        System.out.print("Lista antes da mutacao: " + finalPointList.toString());
        System.out.println("\n");

        for(int a = 1 ; a < finalPointList.size() - 1 ; a++){
           int newX = (int) (finalPointList.get(a).getX() * mutationRate);
           int newY = (int) (finalPointList.get(a).getY() * mutationRate);
           fit1 = getFitness(finalPointList , configuration);

           //mutacao no eixo dos XX:
            //newX vai tomar sempre o valor maximo possivel de ser aplicado ao novo ponto sem que ultrapasse os limites do mapa
           if (configuration.getWidth() - finalPointList.get(a).getX() < newX){
               newX = configuration.getWidth() - finalPointList.get(a).getX();
           }

           Point p1 = new Point(finalPointList.get(a).getX() + newX , finalPointList.get(a).getY());
           finalPointList.set(a , p1);

           double fit2 = getFitness(finalPointList , configuration);

           if (fit2 < fit1){
               int prevX = finalPointList.get(a).getX() - newX;
               p1 = new Point(prevX , finalPointList.get(a).getY());
               finalPointList.set(a , p1);

               newX = (int) (finalPointList.get(a).getX() * mutationRate);
               if((finalPointList.get(a).getX() - newX) < 0){
                    newX = finalPointList.get(a).getX() - 1;
               }
               p1 = new Point(finalPointList.get(a).getX() - newX , finalPointList.get(a).getY());
               finalPointList.set(a , p1);

               fit2 = getFitness(finalPointList , configuration);

               if (fit2 < fit1){
                    prevX = finalPointList.get(a).getX() + newX;
                    p1 = new Point(prevX , finalPointList.get(a).getY());
                    finalPointList.set(a , p1);
               }

               //mutacao no eixo dos YY:
               //(...)
           }
        }

        System.out.println("Fitness final: " + getFitness(finalPointList , configuration));
        System.out.print("Lista depois da mutacao: ");
        System.out.println(finalPointList.toString());

        return list;
    }

    /**
     * Funcao responsavel por cruzar uma lista de caminhos carregados na funcao markGeneticAlgorithm.java
     * @param pointsList lista de pontos a ser carregada
     * @param conf configuracao inicial do mapa carregado (dimensao, obstaculos)
     */
    public static void reproducePopulation(ArrayList<List<IPoint>> pointsList , UIConfiguration conf){
        Random rand = new Random();

        while (getFitness(finalPointList , conf) < 0){
            for(int a = 0 ; a < pointsList.size() - 1 ; a = a + 2){
                int point1 = rand.nextInt((pointsList.get(0).size() - 1) - 1) + 1;
                int point2 = point1;

                while (point2 == point1){
                    point2 = rand.nextInt((pointsList.get(0).size() - 1) - 1) + 1;
                }
                IPoint p1 = pointsList.get(a).get(point1);
                IPoint p2 = pointsList.get(a).get(point2);

                pointsList.get(a).set(point1 , pointsList.get(a + 1).get(point1));
                pointsList.get(a).set(point2 , pointsList.get(a + 1).get(point2));
                pointsList.get(a + 1).set(point1 , p1);
                pointsList.get(a + 1).set(point2 , p2);
            }

            for(int b = 0 ; b < pointsList.size() ; b++){
                double score = 0;
                score = getFitness(pointsList.get(b) , conf);
                if(score > getFitness(finalPointList , conf)){
                    finalPointList = pointsList.get(b);
                }
            }
            Collections.shuffle(pointsList);
            setGeneration(generation++);
        }
    }

    /**
     * Funcao responsavel por determinar se um caminho passado por parametro cruza algum obstaculo do mapa
     * @param pointList a lista a ser passada por parametro
     * @param conf configuracao inicial do mapa (dimensao, obstaculos)
     * @return retorna true se passa por algum obstaculo, false caso contrario
     */
    public static boolean collisionDetection(List<IPoint> pointList , UIConfiguration conf){
        List<Rectangle> obstacles = conf.getObstacles(); //lista com os obstáculos encontrados no mapa
        int count = 0;

        for(int a = 0 ; a < pointList.size()-1 ; a++){
            int x1 = pointList.get(a).getX();
            int y1 = pointList.get(a).getY();
            int x2 = pointList.get(a+1).getX();
            int y2 = pointList.get(a+1).getY();
            Line2D line1 = new Line2D.Double(x1,y1,x2,y2);

            //determinar se a linha se intercepta com algum dos obstáculos
            for(int c = 0 ; c < obstacles.size() ; c++){
                if(line1.intersects(obstacles.get(c).getBounds2D())){
                    count++;
                }
            }
        }
        if (count > 0)
            return true;
        else
            return false;
    }
}