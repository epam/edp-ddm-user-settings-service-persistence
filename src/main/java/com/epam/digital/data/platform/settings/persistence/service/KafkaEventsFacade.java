package com.epam.digital.data.platform.settings.persistence.service;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.starter.audit.model.EventType;
import com.epam.digital.data.platform.starter.audit.service.AbstractAuditFacade;
import com.epam.digital.data.platform.starter.audit.service.AuditService;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventsFacade extends AbstractAuditFacade {

  static final String KAFKA_REQUEST = "Kafka request. Method: ";

  private final TraceService traceService;
  private final JwtInfoProvider jwtInfoProvider;

  public KafkaEventsFacade(
      @Value("${spring.application.name:user-settings-service-persistence}") String appName,
      AuditService auditService,
      TraceService traceService,
      Clock clock, JwtInfoProvider jwtInfoProvider) {
    super(appName, auditService, clock);
    this.traceService = traceService;
    this.jwtInfoProvider = jwtInfoProvider;
  }

  public void sendKafkaAudit(EventType eventType, String methodName, Request<?> request,
      String action, String step, String result) {
    var event = createBaseAuditEvent(
        eventType, KAFKA_REQUEST + methodName, traceService.getRequestId());

    var context = auditService.createContext(action, step, null, null, null, result);
    event.setContext(context);
    setUserInfoToEvent(request, event);

    auditService.sendAudit(event.build());
  }

  private void setUserInfoToEvent(Request<?> request, GroupedAuditEventBuilder event) {
    String jwt = request.getSecurityContext().getAccessToken();
    if (jwt != null) {
      var userClaims = jwtInfoProvider.getUserClaims(request);
      event.setUserInfo(userClaims.getDrfo(), userClaims.getFullName());
    }
  }
}
