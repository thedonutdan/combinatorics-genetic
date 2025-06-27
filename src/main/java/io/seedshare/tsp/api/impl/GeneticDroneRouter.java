/*
 * Copyright 2025 kevvurs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.seedshare.tsp.api.impl;

import io.seedshare.tsp.api.AbstractDroneRouter;
import io.seedshare.tsp.api.TravelMetric;
import io.seedshare.tsp.model.ServiceDestination;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/** Routes the drone's path using genetic approximation */
public class GeneticDroneRouter extends AbstractDroneRouter {
  private int epochs;
  private int populationSize;
  private Random random = new Random();

  public GeneticDroneRouter(TravelMetric distanceFn, int epochs, int populationSize) {
    super(distanceFn);
    this.epochs = epochs;
    this.populationSize = populationSize;
  }

  @Override
  public List<ServiceDestination> route(
      ServiceDestination distributionCenter, List<ServiceDestination> serviceDestinations) {
    if (distributionCenter == null || serviceDestinations == null) {
      throw new IllegalArgumentException("route(): arguments cannot be null");
    }
    // Start with completely random population
    List<List<ServiceDestination>> population = new ArrayList<>();

    for (int i = 0; i < populationSize; i++) {
      List<ServiceDestination> randRoute = new ArrayList<>(serviceDestinations);
      Collections.shuffle(randRoute);
      population.add(randRoute);
    }

    // Cull, crossover, mutate, introduce randomness
    for (int i = 0; i < epochs; i++) {
      List<List<ServiceDestination>> culledPopulation = cull(population, distributionCenter);
      population = new ArrayList<>(culledPopulation);
      // Crossover
      while (population.size() < (int) Math.round(populationSize * 0.55)) {
        population.add(
            OX(
                culledPopulation.get(random.nextInt(culledPopulation.size())),
                culledPopulation.get(random.nextInt(culledPopulation.size()))));
      }
      while (population.size() < (int) Math.round(populationSize * 0.9)) {
        int randIdx = random.nextInt(culledPopulation.size());
        population.add(swapMutate(culledPopulation.get(randIdx)));
      }
      while (population.size() < populationSize) {
        List<ServiceDestination> randRoute = new ArrayList<>(serviceDestinations);
        Collections.shuffle(randRoute);
        population.add(randRoute);
      }
    }

    // Final sort to put shortest route at front
    population.sort(Comparator.comparingDouble(route -> routeLength(route, distributionCenter)));

    return population.get(0);
  }

  /**
   * Culls the population and returns only the elite 10% and 10% chosen through tournament selection
   */
  private List<List<ServiceDestination>> cull(
      List<List<ServiceDestination>> population, ServiceDestination origin) {
    List<List<ServiceDestination>> newPopulation = new ArrayList<>();
    int eliteCount = populationSize / 10; // Keep elite 10 percent
    int tournamentCount = populationSize / 10; // Keep 10% selected via tournament

    List<List<ServiceDestination>> sorted = new ArrayList<>(population);
    sorted.sort(Comparator.comparingDouble(route -> routeLength(route, origin)));

    List<List<ServiceDestination>> elites = sorted.subList(0, eliteCount);
    for (int i = 0; i < eliteCount; i++) {
      newPopulation.add(new ArrayList<>(elites.get(i)));
    }

    newPopulation.addAll(
        tournamentElimination(sorted.subList(eliteCount, sorted.size()), tournamentCount, origin));

    return newPopulation;
  }

  /** Calculates route length */
  private double routeLength(List<ServiceDestination> route, ServiceDestination origin) {
    double length = 0;
    for (int i = 0; i < route.size() - 1; i++) {
      length += distance(route.get(i), route.get(i + 1));
    }

    length += distance(route.get(route.size() - 1), origin);

    return length;
  }

  /** Tournament style elimination. Returns survivors */
  private List<List<ServiceDestination>> tournamentElimination(
      List<List<ServiceDestination>> population, int survivorCount, ServiceDestination origin) {
    List<List<ServiceDestination>> survivors = new ArrayList<>();

    for (int i = 0; i < survivorCount; i++) {
      List<List<ServiceDestination>> tournament = new ArrayList<>();

      for (int j = 0; j < population.size() / 5; j++) {
        List<ServiceDestination> candidate = population.get(random.nextInt(population.size()));
        tournament.add(candidate);
      }

      tournament.sort(Comparator.comparingDouble(route -> routeLength(route, origin)));

      survivors.add(tournament.get(0));
    }

    return survivors;
  }

  /** Order-preserving crossover between two parents */
  private List<ServiceDestination> OX(
      List<ServiceDestination> parent1, List<ServiceDestination> parent2) {
    // Randomly select a gene from parent1 starting in the first half and ending in the last half
    int size = parent1.size();
    int start = random.nextInt(size / 2);
    int end = start + random.nextInt(size - start);
    List<ServiceDestination> child = new ArrayList<>(Collections.nCopies(size, null));

    // Preserve allele position from gene in child
    for (int i = start; i <= end; i++) {
      child.set(i, parent1.get(i));
    }

    // Fill in remaining slots with parent2 alleles (starting after gene and wrapping back around)
    int currentIdx = (end + 1) % size;
    for (int i = 0; i < size; i++) {
      ServiceDestination candidate = parent2.get((end + 1 + i) % size);
      if (!child.contains(candidate)) {
        child.set(currentIdx, candidate);
        currentIdx = (currentIdx + 1) % size;
      }
    }

    return child;
  }

  /** Performs a simple swap mutation */
  private List<ServiceDestination> swapMutate(List<ServiceDestination> route) {
    List<ServiceDestination> child = new ArrayList<>(route);
    int size = route.size();
    int i = random.nextInt(size);
    int j = random.nextInt(size);
    ServiceDestination allele1 = route.get(i);
    ServiceDestination allele2 = route.get(j);
    child.set(j, allele1);
    child.set(i, allele2);

    return child;
  }
}
