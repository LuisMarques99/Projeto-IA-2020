package mark_projects;

import impl.Point;
import impl.UIConfiguration;
import interf.IPoint;
import robocode.AdvancedRobot;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Esta irá ser a classe utilizada para a construcao do algoritmo genético que vai ser utilizado nos
 * robots MARK1 e MARK3
 */
public class GeneticAlgorithm extends AdvancedRobot{

    private static boolean flag = false;

    public static List<IPoint> markGeneticAlgorithm(int populationSize , int maxIterations , double mutationRate , UIConfiguration conf) {
        int i = 0 , a = 0;
        double index, prevIndex = 0;
        List<IPoint> finalPointList = null; //lista final onde vai guardado o melhor caminho a seguir
        List<IPoint> points = new ArrayList<>(); //lista temporária onde vai ser carregado novas populacoes a cada iteracao
        List<Rectangle> obstacles = conf.getObstacles(); //lista com os obstáculos encontrados no mapa
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

                Line2D line1 = new Line2D.Double(x1Value,y1Value,x2Value,y2Value);

                //se o ponto onde nos encontramos for o ultimo ponto entao adiciona esse como o ponto seleccionado pelo utilizador
                if(a == populationSize - 1){
                    line1 = new Line2D.Double(x1Value,y1Value,conf.getEnd().getX(),conf.getEnd().getY());
                    x2Value = conf.getEnd().getX();
                    y2Value = conf.getEnd().getY();
                }

                //determinar se a linha se intercepta com algum dos obstáculos
                for(int c = 0 ; c < obstacles.size() ; c++){
                    if(line1.intersects(obstacles.get(c).getBounds2D())){
                        flag = true;
                    }
                }
                points.add(new Point(x2Value , y2Value));
                x1Value = x2Value;
                y1Value = y2Value;
                a++;
            } while(a < populationSize);
            a = 0;
            index = getFitness(points);

            /**
             * Se o index de fitness registado nesta populacao for maior do que o anterior, entao esta populacao vai
             * ser a seleccionada caso contrário continua com a populacao anterior, ou seja, nao faz nada!
             */
            if(index > prevIndex){
                finalPointList = points;
                prevIndex = index;
            }
            points = new ArrayList<>();
            i++;
        } while (i < maxIterations);

        mutateList(finalPointList , mutationRate);
        return finalPointList;
    }

    /**
     * Funcao responsavel por determinar o nivel de fitness de um caminho escolhido pelo algoritmo genético
     * @param points escala quantificavel de pontos atribuidos a cada caminho (mais pontos significa melhor fitness)
     * @return quantidade de pontos
     */
    public static double getFitness(List<IPoint> points){
        //se o caminho resulta num caminho que nao passa por cima de nenhuma posicao inimiga, entao ganha 10000 pontos de fitness automáticamente.
        double fitnessPoints = 0;
        if(points != null){
            if(flag == false){
                fitnessPoints += 10000;
            }
            flag = false;
            double distance = 0;

            for(int a = 0 ; a < points.size() - 1 ; a++){
                distance += getDistanceBetweenPoints(points.get(a) , points.get(a + 1));
            }

            fitnessPoints -= distance;
        }
        else {
            fitnessPoints = 0;
        }
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
        double distance = 0;
        double yAxis = 0;
        double xAxis = 0;

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

    public static List<IPoint> mutateList(List<IPoint> list , double mutationRate){
        /*
        a ideia aqui é ir a cada ponto do caminho ja escolhido como valido e mais curto e modificar os seus pontos
        de X e Y um a um e ver se o seu fitness aumenta!
         */
        return list;
    }
}