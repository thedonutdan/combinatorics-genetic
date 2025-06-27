package io.seedshare.tsp.api.impl;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import io.seedshare.tsp.api.ProblemReader;
import io.seedshare.tsp.model.RoutingProblem;
import io.seedshare.tsp.model.ServiceDestination;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class PerformanceBenchmarkRunner {

  private RoutingProblem problem;

  @Setup(Level.Trial)
  public void setUp() throws IOException {
    ProblemReader reader = new TSPLIBProblemReader();
    problem = reader.readProblem(new File("problems/a280.tsp"));
  }

  @Benchmark
  public List<ServiceDestination> NNBenchmark() {
    NNDroneRouter router = new NNDroneRouter(PerformanceBenchmarkRunner::euclideanDistance);
    return router.route(problem.getDistributionCenter(), problem.getServiceDestinations());
  }

  private static Double euclideanDistance(ServiceDestination sd1, ServiceDestination sd2) {
    double xdif = abs(sd1.getX() - sd2.getX());
    double ydif = abs(sd1.getY() - sd2.getY());
    return sqrt(xdif * xdif + ydif * ydif);
  }

  public static void main(String[] args) throws Exception {
    org.openjdk.jmh.Main.main(args);
  }
}
