package com.epam.digital.data.platform.settings.persistence.aspect;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Response;
import com.epam.digital.data.platform.model.core.kafka.Status;
import com.epam.digital.data.platform.settings.persistence.service.KafkaEventsFacade;
import com.epam.digital.data.platform.starter.audit.model.EventType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Aspect
@Component
public class AuditKafkaEventsAspect implements KafkaGenericListenerAspect {

  private static final Set<Status> SECURITY_AUDITABLE_STATUSES = Collections.singleton(Status.JWT_INVALID);

  private static final String READ = "KAFKA REQUEST READ";
  private static final String UPDATE = "KAFKA REQUEST UPDATE";

  static final String BEFORE = "BEFORE";
  static final String AFTER = "AFTER";

  private final KafkaEventsFacade kafkaEventsFacade;

  public AuditKafkaEventsAspect(KafkaEventsFacade kafkaEventsFacade) {
    this.kafkaEventsFacade = kafkaEventsFacade;
  }

  @Around("kafkaRead() && args(.., request)")
  Object auditKafkaRead(ProceedingJoinPoint joinPoint, Request<?> request) throws Throwable {
    return prepareAndSendKafkaAudit(joinPoint, request, READ);
  }

  @Around("kafkaUpdate() && args(.., request)")
  Object auditKafkaUpdate(ProceedingJoinPoint joinPoint, Request<?> request) throws Throwable {
    return prepareAndSendKafkaAudit(joinPoint, request, UPDATE);
  }

  private Object prepareAndSendKafkaAudit(ProceedingJoinPoint joinPoint, Request<?> request,
      String action) throws Throwable {

    String methodName = joinPoint.getSignature().getName();
    kafkaEventsFacade.sendKafkaAudit(EventType.USER_ACTION, methodName, request, action, BEFORE, null);

    Object result = joinPoint.proceed();
    var resultStatus = ((Response<?>) result).getStatus();

    EventType responseEventType;
    if (SECURITY_AUDITABLE_STATUSES.contains(resultStatus)) {
      responseEventType = EventType.SECURITY_EVENT;
    } else {
      responseEventType = EventType.USER_ACTION;
    }
    kafkaEventsFacade.sendKafkaAudit(
        responseEventType, methodName, request, action, AFTER, resultStatus.toString());
    return result;
  }
}
