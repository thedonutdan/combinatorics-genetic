package io.seedshare.tsp.assessment;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import io.seedshare.tsp.api.ProblemReader;
import io.seedshare.tsp.api.impl.GeneticDroneRouter;
import io.seedshare.tsp.api.impl.TSPLIBProblemReader;
import io.seedshare.tsp.model.RoutingProblem;
import io.seedshare.tsp.model.ServiceDestination;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Assessment {
  public static void main(String[] args) {
    ProblemReader reader = new TSPLIBProblemReader();
    try {
      System.out.println("Running assessment of routers...");
      reader.readProblem(new File("problems/a280.tsp"));
      System.out.println("Running assessment on genetic algorithm...");
      RoutingProblem problem = reader.readSolution(new File("problems/a280.opt.tour"));
      double geneticLength = geneticDroneRouterAssessment(problem, 100000, 1000);
      double optimalLength =
          routeLength(problem.getOptimalRoute(), problem.getDistributionCenter());
      System.out.println("Genetic: " + geneticLength + "\nOptimal: " + optimalLength);
    } catch (IOException e) {
      System.out.println("Error reading input file: " + e.getMessage());
    }
  }

  private static double geneticDroneRouterAssessment(
      RoutingProblem problem, int epochs, int population) {
    GeneticDroneRouter router =
        new GeneticDroneRouter(Assessment::euclideanDistance, epochs, population);
    List<ServiceDestination> route =
        router.route(problem.getDistributionCenter(), problem.getServiceDestinations());
    return routeLength(route, problem.getDistributionCenter());
  }

  private static Double euclideanDistance(ServiceDestination sd1, ServiceDestination sd2) {
    double xdif = abs(sd1.getX() - sd2.getX());
    double ydif = abs(sd1.getY() - sd2.getY());
    return sqrt(xdif * xdif + ydif * ydif);
  }

  /** Calculates route length */
  private static double routeLength(List<ServiceDestination> route, ServiceDestination origin) {
    double length = 0;
    for (int i = 0; i < route.size() - 1; i++) {
      length += euclideanDistance(route.get(i), route.get(i + 1));
    }

    length += euclideanDistance(route.get(route.size() - 1), origin);

    return length;
  }
}
