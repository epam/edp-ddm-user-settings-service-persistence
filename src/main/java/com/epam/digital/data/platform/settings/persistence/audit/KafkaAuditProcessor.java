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

package com.epam.digital.data.platform.settings.persistence.audit;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Response;
import com.epam.digital.data.platform.model.core.kafka.Status;
import com.epam.digital.data.platform.settings.persistence.audit.AuditableListener.Operation;
import com.epam.digital.data.platform.starter.audit.model.EventType;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

@Component
public class KafkaAuditProcessor {

  private static final Set<Status> SECURITY_AUDITABLE_STATUSES = Collections.singleton(
      Status.JWT_INVALID);

  static final Map<Operation, String> actionMap = Map.of(
      Operation.READ, "KAFKA REQUEST READ",
      Operation.UPDATE, "KAFKA REQUEST UPDATE");

  static final String BEFORE = "BEFORE";
  static final String AFTER = "AFTER";

  private final KafkaEventsFacade kafkaEventsFacade;

  public KafkaAuditProcessor(KafkaEventsFacade kafkaEventsFacade) {
    this.kafkaEventsFacade = kafkaEventsFacade;
  }

  public Object process(ProceedingJoinPoint joinPoint, Operation operation, Request<?> request)
      throws Throwable {
    return prepareAndSendKafkaAudit(joinPoint, operation, request);
  }

  private Object prepareAndSendKafkaAudit(ProceedingJoinPoint joinPoint, Operation operation,
      Request<?> request) throws Throwable {

    String action = actionMap.get(operation);
    String methodName = joinPoint.getSignature().getName();

    kafkaEventsFacade.sendKafkaAudit(
        EventType.USER_ACTION, methodName, request, action, BEFORE, null);

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
