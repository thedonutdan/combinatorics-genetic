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
import java.util.List;

/** Reads a euclidian distance problem from .tsp files in the format found in TSPLIB */
public class TSPLIBProblemReader implements ProblemReader {
  public RoutingProblem readProblem(File file) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      List<ServiceDestination> problem = new ArrayList<>();
      String line = reader.readLine();
      while (line != null && !line.equals("NODE_COORD_SECTION")) {
        line = reader.readLine();
      }

      while ((line = reader.readLine()) != null && !line.equals("EOF")) {
        String[] items = line.trim().split("\\s+");
        ServiceDestination serviceDestination =
            new ServiceDestination(
                items[0], Integer.parseInt(items[1]), Integer.parseInt(items[2]));
        if (problem.contains(serviceDestination)) {
          throw new IllegalStateException(
              "Bad input: Repeated destination: " + serviceDestination.toString());
        }
        problem.add(serviceDestination);
      }

      return new RoutingProblem(problem.get(0), problem.subList(1, problem.size()));
    } catch (IOException e) {
      throw new IOException("Error reading tsp file " + file.getName() + " : " + e.getMessage());
    }
  }
}
