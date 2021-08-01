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
