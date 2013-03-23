package differentialEvolution;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("1: Select a function and display the best fitness value, and standard deviation.\n"
                + "2: Plot a graph for a particular function.\n");

        Scanner in = new Scanner(System.in);
        int firstOption = in.nextInt();

        System.out.println("Enter a number corresponding to the function you would like to minimize: \n"
                + "1: 1st De Jong\n" 
                + "2: Axis Parallel Hyper-Ellipsoid\n" 
                + "3: Schwefel's Problem\n"
                + "4: Rosenbrock's Valley\n" 
                + "5: Rastrigin's Function\n");

        int selectedBenchmark = in.nextInt();
        in.close();

        System.out.println("Running, please wait...");

        /**
         * If the first option is selected, generate a random population and
         * apply the selected function to the differential evolution of that
         * population, for 50 runs. Print the run time after completion. If the
         * second option is selected, do the same as above, however with only 1
         * run. Graph the best fitness value (x axis) per function call (y
         * axis).
         */
        if (firstOption == 1) {
            long startTime = System.nanoTime();
            DifferentialEvolution.generateAndTestLoop(selectedBenchmark);
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            System.out.println("Run time: " + duration * (1.0e-9) + "s");
        } else if (firstOption == 2) {
            long startTime = System.nanoTime();
            DifferentialEvolution.plotGraph(selectedBenchmark);
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            System.out.println("Run time: " + duration * (1.0e-9) + "s");
        } else {
        	System.out.print("Invalid option. Please run this program again and select a valid option.");
        }
        System.out.println("Mean: " + DifferentialEvolution.getMean());
        System.out.println("Standard Deviation: " + DifferentialEvolution.getStandardDeviation());
    }
}
