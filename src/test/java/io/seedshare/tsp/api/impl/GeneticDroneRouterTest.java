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

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static org.assertj.core.api.Assertions.assertThat;

import io.seedshare.tsp.model.ServiceDestination;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeneticDroneRouterTest {

  private ServiceDestination dc;
  private List<ServiceDestination> destinations;
  private GeneticDroneRouter router;

  @BeforeEach
  public void setup() {
    dc = new ServiceDestination("warehouse1", 14, 14);
    destinations = new LinkedList<>();
    // each point is further from dc
    destinations.add(new ServiceDestination("A", 13, 12));
    destinations.add(new ServiceDestination("B", 8, 9));
    destinations.add(new ServiceDestination("C", 6, 7));
    destinations.add(new ServiceDestination("D", 5, 5));
    destinations.add(new ServiceDestination("E", 3, 5));
    destinations.add(new ServiceDestination("F", 1, 0));
    router = new GeneticDroneRouter(GeneticDroneRouterTest::euclideanDistance, 100, 30);
  }

  @Test
  public void test_route() {
    List<ServiceDestination> route = router.route(dc, destinations);
    assertThat(route)
        .isNotNull()
        .isNotEmpty()
        .hasSize(6)
        .containsExactlyInAnyOrderElementsOf(destinations);
  }

  private static Double euclideanDistance(ServiceDestination sd1, ServiceDestination sd2) {
    double xdif = abs(sd1.getX() - sd2.getX());
    double ydif = abs(sd1.getY() - sd2.getY());
    return sqrt(xdif * xdif + ydif * ydif);
  }
}
