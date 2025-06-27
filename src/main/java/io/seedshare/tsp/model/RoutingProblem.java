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
package io.seedshare.tsp.model;

import java.util.List;

/** Represents a specific instance of a TSP problem */
public class RoutingProblem {
  private ServiceDestination distributionCenter;
  private List<ServiceDestination> serviceDestinations;

  public RoutingProblem(
      ServiceDestination distributionCenter, List<ServiceDestination> serviceDestinations) {
    this.distributionCenter = distributionCenter;
    this.serviceDestinations = serviceDestinations;
  }

  public ServiceDestination getDistributionCenter() {
    return distributionCenter;
  }

  public List<ServiceDestination> getServiceDestinations() {
    return serviceDestinations;
  }
}
