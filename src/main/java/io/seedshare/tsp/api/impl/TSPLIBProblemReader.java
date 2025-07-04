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

import io.seedshare.tsp.api.ProblemReader;
import io.seedshare.tsp.model.RoutingProblem;
import io.seedshare.tsp.model.ServiceDestination;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Reads a euclidian distance problem from .tsp files in the format found in TSPLIB */
public class TSPLIBProblemReader implements ProblemReader {
  private RoutingProblem problem;
  private Map<String, ServiceDestination> idMap = new HashMap<>();

  public RoutingProblem readProblem(File file) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      List<ServiceDestination> nodes = new ArrayList<>();
      String line = reader.readLine();
      while (line != null && !line.equals("NODE_COORD_SECTION")) {
        line = reader.readLine();
      }

      while ((line = reader.readLine()) != null && !line.equals("EOF")) {
        String[] items = line.trim().split("\\s+");
        ServiceDestination serviceDestination =
            new ServiceDestination(
                items[0], Double.parseDouble(items[1]), Double.parseDouble(items[2]));
        if (nodes.contains(serviceDestination)) {
          throw new IllegalStateException(
              "Bad problem input: Repeated destination: " + serviceDestination.toString());
        }
        nodes.add(serviceDestination);
        idMap.put(serviceDestination.getId(), serviceDestination);
      }

      problem = new RoutingProblem(nodes.get(0), nodes.subList(1, nodes.size()));
      return problem;
    } catch (IOException e) {
      throw new IOException("Error reading tsp file " + file.getName() + " : " + e.getMessage());
    }
  }

  public RoutingProblem readSolution(File file) throws IOException {
    if (problem == null) {
      throw new IllegalStateException("Error reading solution: No associated problem");
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      List<ServiceDestination> nodes = new ArrayList<>();
      String line = reader.readLine();
      while (line != null && !line.equals("TOUR_SECTION")) {
        line = reader.readLine();
      }

      while ((line = reader.readLine()) != null && !line.equals("-1")) {
        String id = line.trim();
        ServiceDestination serviceDestination = idMap.get(id);
        if (nodes.contains(serviceDestination)) {
          throw new IllegalStateException(
              "Bad solution input: Repeated destination: " + serviceDestination.toString());
        }
        nodes.add(serviceDestination);
      }

      problem.setOptimalRoute(nodes.subList(1, nodes.size()));
      return problem;
    } catch (IOException e) {
      throw new IOException("Error reading tsp file " + file.getName() + " : " + e.getMessage());
    }
  }
}
