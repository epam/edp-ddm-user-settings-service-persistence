/*
 * Copyright 2021 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.digital.data.platform.settings.persistence.aspect;

import com.epam.digital.data.platform.model.core.kafka.Response;
import com.epam.digital.data.platform.model.core.kafka.Status;
import com.epam.digital.data.platform.starter.actuator.livenessprobe.LivenessStateHandler;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Aspect
@Component
public class LivenessStateCheckAspect implements KafkaResponseListenerAspect {

  private static final EnumSet<Status> LIVENESS_UNHEALTHY_STATUSES = EnumSet.of(
      Status.THIRD_PARTY_SERVICE_UNAVAILABLE);

  private final LivenessStateHandler livenessStateHandler;

  public LivenessStateCheckAspect(LivenessStateHandler livenessStateHandler) {
    this.livenessStateHandler = livenessStateHandler;
  }

  @Override
  public void handleKafkaResponse(Response<?> response) {
    livenessStateHandler
        .handleResponse(response.getStatus(), LIVENESS_UNHEALTHY_STATUSES::contains);
  }
}
