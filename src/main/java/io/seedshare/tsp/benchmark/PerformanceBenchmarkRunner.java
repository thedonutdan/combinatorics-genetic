package io.seedshare.tsp.benchmark;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

import io.seedshare.tsp.api.ProblemReader;
import io.seedshare.tsp.api.impl.GeneticDroneRouter;
import io.seedshare.tsp.api.impl.NNDroneRouter;
import io.seedshare.tsp.api.impl.TSPLIBProblemReader;
import io.seedshare.tsp.model.RoutingProblem;
import io.seedshare.tsp.model.ServiceDestination;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 1, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 2, timeUnit = TimeUnit.SECONDS)
public class PerformanceBenchmarkRunner {

  private RoutingProblem problem;

  @Setup(Level.Trial)
  public void setUp() throws IOException {
    ProblemReader reader = new TSPLIBProblemReader();
    problem = reader.readProblem(new File("problems/a280.tsp"));
  }

  @Benchmark
  public void NNDroneRouterBenchmark(Blackhole blackhole) {
    NNDroneRouter router = new NNDroneRouter(PerformanceBenchmarkRunner::euclideanDistance);
    List<ServiceDestination> result =
        router.route(problem.getDistributionCenter(), problem.getServiceDestinations());
    blackhole.consume(result);
  }

  @Benchmark
  public void geneticDroneRouterBenchmark(Blackhole blackhole) {
    GeneticDroneRouter router =
        new GeneticDroneRouter(PerformanceBenchmarkRunner::euclideanDistance, 1000, 100);
    ServiceDestination distributionCenter =
        new ServiceDestination(
            problem.getDistributionCenter().getId(),
            problem.getDistributionCenter().getX(),
            problem.getDistributionCenter().getY());
    List<ServiceDestination> serviceDestinations =
        new ArrayList<>(problem.getServiceDestinations());
    List<ServiceDestination> result = router.route(distributionCenter, serviceDestinations);
    blackhole.consume(result);
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
