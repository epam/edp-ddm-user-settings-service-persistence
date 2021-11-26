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
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.settings.persistence.audit.KafkaEventsFacade;
import com.epam.digital.data.platform.settings.persistence.service.JwtInfoProvider;
import com.epam.digital.data.platform.settings.persistence.service.TraceService;
import com.epam.digital.data.platform.starter.audit.model.AuditEvent;
import com.epam.digital.data.platform.starter.audit.model.AuditUserInfo;
import com.epam.digital.data.platform.starter.audit.model.EventType;
import com.epam.digital.data.platform.starter.audit.service.AuditService;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaEventsFacadeTest {

  private static final String APP_NAME = "application";
  private static final String REQUEST_ID = "1";
  private static final String METHOD_NAME = "method";
  private static final String ACTION = "CREATE";
  private static final String STEP = "BEFORE";
  private static final String USER_DRFO = "1010101014";
  private static final String USER_KEYCLOAK_ID = "496fd2fd-3497-4391-9ead-41410522d06f";
  private static final String USER_NAME = "Сидоренко Василь Леонідович";
  private static final LocalDateTime CURR_TIME = LocalDateTime.of(2021, 4, 1, 11, 50);
  private static final String RESULT = "RESULT";

  private static final String ACCESS_TOKEN = "Token";

  private KafkaEventsFacade kafkaEventsFacade;
  @Mock
  private JwtInfoProvider jwtInfoProvider;
  @Mock
  private AuditService auditService;
  @Mock
  private TraceService traceService;
  @Mock
  private Clock clock;

  @BeforeEach
  void beforeEach() {
    kafkaEventsFacade =
        new KafkaEventsFacade(auditService, APP_NAME, clock, traceService, jwtInfoProvider);

    when(traceService.getRequestId()).thenReturn(REQUEST_ID);
    when(clock.millis())
        .thenReturn(CURR_TIME.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
  }

  @Test
  void expectCorrectAuditEventWhenJwtAbsent() {
    Map<String, Object> context = Map.of("action", ACTION, "step", STEP, "result", RESULT);
    when(auditService.createContext(ACTION, STEP, null, null, null, RESULT)).thenReturn(context);

    kafkaEventsFacade.sendKafkaAudit(
        EventType.USER_ACTION, METHOD_NAME, mockRequest(null), ACTION, STEP, RESULT);

    ArgumentCaptor<AuditEvent> auditEventCaptor = ArgumentCaptor.forClass(AuditEvent.class);

    verify(auditService).sendAudit(auditEventCaptor.capture());
    AuditEvent actualEvent = auditEventCaptor.getValue();

    var expectedEvent = AuditEvent.AuditEventBuilder.anAuditEvent()
            .application(APP_NAME)
            .name("Kafka request. Method: method")
            .requestId(REQUEST_ID)
            .sourceInfo(null)
            .userInfo(null)
            .currentTime(clock.millis())
            .eventType(EventType.USER_ACTION)
            .context(context)
            .build();
    assertThat(actualEvent).usingRecursiveComparison().isEqualTo(expectedEvent);
  }

  @Test
  void expectCorrectAuditEventWhenJwtPresent() {
    JwtClaimsDto userClaims = new JwtClaimsDto();
    userClaims.setFullName(USER_NAME);
    userClaims.setSubject(USER_KEYCLOAK_ID);
    userClaims.setDrfo(USER_DRFO);
    when(jwtInfoProvider.getUserClaims(any())).thenReturn(userClaims);

    Map<String, Object> context = Map.of("action", ACTION, "step", STEP, "result", RESULT);
    when(auditService.createContext(ACTION, STEP, null, null, null, RESULT)).thenReturn(context);

    kafkaEventsFacade.sendKafkaAudit(
        EventType.USER_ACTION, METHOD_NAME, mockRequest(ACCESS_TOKEN), ACTION, STEP, RESULT);

    ArgumentCaptor<AuditEvent> auditEventCaptor = ArgumentCaptor.forClass(AuditEvent.class);

    verify(auditService).sendAudit(auditEventCaptor.capture());
    AuditEvent actualEvent = auditEventCaptor.getValue();

    var expectedEvent = AuditEvent.AuditEventBuilder.anAuditEvent()
            .application(APP_NAME)
            .name("Kafka request. Method: method")
            .requestId(REQUEST_ID)
            .sourceInfo(null)
            .userInfo(new AuditUserInfo(USER_NAME, USER_KEYCLOAK_ID, USER_DRFO))
            .currentTime(clock.millis())
            .eventType(EventType.USER_ACTION)
            .context(context)
            .build();
    assertThat(actualEvent).usingRecursiveComparison().isEqualTo(expectedEvent);
  }

  private Request mockRequest(String jwt) {
    SecurityContext sc = new SecurityContext(jwt, null, null);
    return new Request(null, null, sc);
  }
}
