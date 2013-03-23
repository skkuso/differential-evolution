package differentialEvolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.math.plot.Plot2DPanel;

/***
 * 
 * @author Sarim Mahmood 100372299
 * @author Scott Rosenquist 100343666
 * @author Bryce Benn 100395647
 */
public class DifferentialEvolution {
    static int dimensionality = 30;
    static int populationSize = 2 * dimensionality;
    static double mutationConstant = 0.5;
    static double crossoverRate = 0.9;
    static int maxFunctionCalls = 1000 * dimensionality;
    static int runs = 50;
    static List<Double> standardDeviations = new ArrayList<Double>();
    static double bestFitnessValue = 0;
    static double mean = 0;
    static SummaryStatistics callFitnesses = new SummaryStatistics();
    static SummaryStatistics runFitnesses = new SummaryStatistics();
    static boolean isGraph = false;
    static Plot2DPanel plot = new Plot2DPanel();
    static double[] fitnessValues = null;
    static int function = 0;

    /**
     * Graph one run of a function's best fitness value per run. This method
     * will create a graph of a particular size and plot the best fitness values
     * 
     * @param function
     */
    public static void plotGraph(int function) {
        DifferentialEvolution.runs = 1;
        isGraph = true;
        generateAndTestLoop(function);
        double[] xAxis = new double[maxFunctionCalls];
        for (int i = 0; i < maxFunctionCalls; i++) {
            xAxis[i] = i;
        }
        plot.addLinePlot("my plot", xAxis, fitnessValues);
        JFrame frame = new JFrame("a plot panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setContentPane(plot);
        frame.setVisible(true);
        frame.setBounds(500, 240, 656, 436);
    }

    /**
     * Generate an initial population of size 2*n, with each individual of size
     * n. The values of each individual are randomly selected from a range of
     * values known to successfully minimize each function. The population is
     * 
     * @param function indicates the function to be optimized
     */
    public static void generateAndTestLoop(int function) {
        int currentFunctionCalls = 0;
        double bestFitnessPerCall = 0;
        double bestFitnessPerRun = 0;
        DifferentialEvolution.function = function;

        if (isGraph) {
            fitnessValues = new double[maxFunctionCalls];
        }

        /*
         * Generate a population of size 2*n with individuals of size n.
         */
        List<List<Double>> initialPopulation = new ArrayList<List<Double>>();
        switch (function) {
        case 1:
            initialPopulation.addAll(generatePopulation(-5.12, 5.12));
            break;
        case 2:
            initialPopulation.addAll(generatePopulation(-5.12, 5.12));
            break;
        case 3:
            initialPopulation.addAll(generatePopulation(-65.0, 65.0));
            break;
        case 4:
            initialPopulation.addAll(generatePopulation(-2.0, 2.0));
            break;
        case 5:
            initialPopulation.addAll(generatePopulation(-5.12, 5.12));
            break;
        }

        /*
         * Run the initial population through the differential evolution
         * algorithm for the number of runs specified in the runs variable.
         */
        for (int r = 0; r < runs; r++) {
            currentFunctionCalls = 0;
            while (currentFunctionCalls < maxFunctionCalls) {
                for (int i = 0; i < populationSize; i++) {
                    List<Integer> randomizedIndex = randomParentSelector(populationSize, 4);
                    List<Double> individualA = new ArrayList<Double>();
                    List<Double> individualB = new ArrayList<Double>();
                    List<Double> individualC = new ArrayList<Double>();

                    /*
                     * This block of code assures that the parents Xa, Xb and Xc
                     * are selected randomly such that i != a != b != c.
                     */
                    if (randomizedIndex.get(0) == i) {
                        individualA = initialPopulation.get(randomizedIndex.get(3));
                    } else {
                        individualA = initialPopulation.get(randomizedIndex.get(0));
                    }

                    if (randomizedIndex.get(1) == i) {
                        individualB = initialPopulation.get(randomizedIndex.get(3));
                    } else {
                        individualB = initialPopulation.get(randomizedIndex.get(1));
                    }

                    if (randomizedIndex.get(2) == i) {
                        individualC = initialPopulation.get(randomizedIndex.get(3));
                    } else {
                        individualC = initialPopulation.get(randomizedIndex.get(2));
                    }

                    List<Double> trialVector = new ArrayList<Double>();

                    // mutation
                    List<Double> noiseVector = addIndividuals(individualA,
                            multiplyIndividuals(subtractIndividuals(individualC, individualB), mutationConstant));

                    // crossover
                    for (int j = 0; j < dimensionality; j++) {
                        if (crossoverRate > Math.random() || j == (int) Math.random() * j) {
                            trialVector.add(j, noiseVector.get(j));
                        } else {
                            trialVector.add(j, initialPopulation.get(i).get(j));
                        }
                    }

                    // selection
                    List<Double> childIndividual = new ArrayList<Double>();
                    double currentIndividualFitness = cost(function, initialPopulation.get(i));
                    double trialVectorFitness = cost(function, trialVector);
                    if (trialVectorFitness <= currentIndividualFitness) {
                        childIndividual = trialVector;
                        bestFitnessPerCall = trialVectorFitness;
                    } else {
                        childIndividual = initialPopulation.get(i);
                        bestFitnessPerCall = currentIndividualFitness;
                    }
                    initialPopulation.set(i, childIndividual);
                }

                if (isGraph) {
                    fitnessValues[currentFunctionCalls] = bestFitnessPerCall;
                }
                callFitnesses.addValue(bestFitnessPerCall);
                currentFunctionCalls++;
            }
            bestFitnessPerRun = callFitnesses.getMin();
            runFitnesses.addValue(bestFitnessPerRun);
        }
        standardDeviations.add(runFitnesses.getStandardDeviation());
        mean = runFitnesses.getMean();
    }

    /**
     * Generate a two dimensional array that represents
     * 
     * @param min
     *            is the minimum value that an element in the generated
     *            population can have
     * @param max
     *            is the maximum value that an element in the generated
     *            population can have
     * @return a 2-dimensional list that contains a uniformly distributed random
     *         population
     */
    public static List<List<Double>> generatePopulation(double min, double max) {
        List<List<Double>> population = new ArrayList<List<Double>>();
        List<Double> individual;
        for (int i = 0; i < populationSize; i++) {
            individual = new ArrayList<Double>();
            for (int j = 0; j < dimensionality; j++) {
                individual.add(Math.random() * (2 * max) + min);
            }
            population.add(individual);
        }
        return population;
    }

    /**
     * Generate a
     * 
     * @param maxValue
     * @param size
     * @return an array of 4 random, distinct numbers
     */
    public static ArrayList<Integer> randomParentSelector(int maxValue, int size) {
        ArrayList<Integer> populationIndex = new ArrayList<Integer>();
        ArrayList<Integer> parents = new ArrayList<Integer>();

        for (int i = 0; i < maxValue; i++) {
            populationIndex.add(i);
        }

        Random rand = new Random();
        while (populationIndex.size() > populationSize - size) {
            int index = rand.nextInt(populationIndex.size());
            parents.add(populationIndex.remove(index));
        }
        return parents;
    }

    /**
     * Calculate the fitness value of a given individual. These functions
     * 
     * @param individual
     *            is the subject of the cost calculation
     * @param n
     *            represents the function that will be used to calculate the
     *            cost
     * @return the calculated cost value of the given individual
     */
    public static double cost(int n, List<Double> individual) {
        double cost = 0;
        switch (n) {
        case 1:
            // 1st de jong
            for (int i = 0; i < individual.size(); i++) {
                cost += Math.pow((individual.get(i)), 2);
            }
            break;
        case 2:
            // axis parallel hyper ellipsoid
            for (int i = 0; i < individual.size(); i++) {
                cost += (i + 1) * Math.pow((individual.get(i)), 2);
            }
            break;
        case 3:
            // schwefel's problem
            for (int i = 0; i < individual.size(); i++) {
                double innerSum = 0;
                for (int j = 0; j <= i; j++) {
                    innerSum += individual.get(i);
                }
                cost += Math.pow(innerSum, 2);
            }
            break;
        case 4:
            // rosenbrock's valley
            for (int i = 0; i < individual.size() - 1; i++) {
                cost += 100 * Math.pow(((individual.get(i + 1)) - Math.pow(individual.get(i), 2)), 2)
                        + Math.pow((1 - individual.get(i)), 2);
            }
            break;
        case 5:
            // rastrigrin's function
            for (int i = 0; i < individual.size(); i++) {
                cost += (Math.pow(individual.get(i), 2) - 10 * Math.cos(2 * Math.PI * individual.get(i)));
            }
            cost += 10 * dimensionality;
            break;
        }
        return cost;
    }

    /**
     * Add the values of each element in two lists and return a list with
     * the results.
     * @param individualA
     * @param individualB
     * @return a list with the sum of each element in the lists provided
     */
    public static List<Double> addIndividuals(List<Double> individualA, List<Double> individualB) {
        List<Double> individualResult = new ArrayList<Double>();
        for (int i = 0; i < dimensionality; i++) {
            individualResult.add(individualA.get(i) + individualB.get(i));
        }
        return individualResult;
    }

    /**
     * Subtract the values of each element in two lists and return a list with
     * the results.
     * @param individualA 
     * @param individualB
     * @return a list with the subtraction of each element in the lists provided
     */
    public static List<Double> subtractIndividuals(List<Double> individualA, List<Double> individualB) {
        List<Double> individualResult = new ArrayList<Double>();
        for (int i = 0; i < dimensionality; i++) {
            individualResult.add(individualA.get(i) - individualB.get(i));
        }
        return individualResult;
    }

    /**
     * Multiply the values of each element in a list with a constant and return
     * a list with the results.
     * @param individual 
     * @param constant
     * @return a list with the multiplication of each element in the lists provided
     */
    public static List<Double> multiplyIndividuals(List<Double> individual, double constant) {
        List<Double> individualResult = new ArrayList<Double>();
        for (int i = 0; i < dimensionality; i++) {
            individualResult.add(constant * individual.get(i));
        }
        return individualResult;
    }

    /**
     * Return the standard deviation of the best fitness values per run.
     * @return the standard deviation of the elements in a list
     */
    public static List<Double> getStandardDeviation() {
        return standardDeviations;
    }

    /**
     * Return the mean value of the best fitness values per run.
     * @return the mean of the elements in a list
     */
    public static double getMean() {
        return mean;
    }
}
