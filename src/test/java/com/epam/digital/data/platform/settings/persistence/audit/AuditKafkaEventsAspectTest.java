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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.settings.persistence.aspect.AuditAspect;
import com.epam.digital.data.platform.settings.persistence.listener.SettingsListener;
import com.epam.digital.data.platform.settings.persistence.service.JwtValidationService;
import com.epam.digital.data.platform.settings.persistence.service.SettingsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@Import({AopAutoConfiguration.class})
@SpringBootTest(
    classes = {
        AuditAspect.class,
        KafkaAuditProcessor.class,
        SettingsListener.class,
    })
@MockBean(SettingsService.class)
class AuditKafkaEventsAspectTest {

  @Autowired
  private SettingsListener settingsListener;
  
  @MockBean
  private JwtValidationService jwtValidationService;
  @MockBean
  private KafkaEventsFacade kafkaEventsFacade;

  @Test
  void expectAuditAspectBeforeAndAfterReadMethodWhenNoException() {

    settingsListener.read(new Request<>());

    verify(kafkaEventsFacade, times(2)).sendKafkaAudit(any(), any(), any(), any(), any(), any());
  }

  @Test
  void expectAuditAspectOnlyBeforeWhenExceptionOnReadMethod() {
    when(jwtValidationService.isValid(any())).thenThrow(new RuntimeException());

    assertThrows(RuntimeException.class, () -> settingsListener.read(new Request<>()));

    verify(kafkaEventsFacade).sendKafkaAudit(any(), any(), any(), any(), any(), any());
  }

  @Test
  void expectAuditAspectBeforeAndAfterUpdateMethodWhenNoException() {

    settingsListener.update(new Request<>());

    verify(kafkaEventsFacade, times(2)).sendKafkaAudit(any(), any(), any(), any(), any(), any());
  }

  @Test
  void expectAuditAspectOnlyBeforeWhenExceptionOnUpdateMethod() {
    when(jwtValidationService.isValid(any())).thenThrow(new RuntimeException());

    assertThrows(RuntimeException.class, () -> settingsListener.update(new Request<>()));

    verify(kafkaEventsFacade).sendKafkaAudit(any(), any(), any(), any(), any(), any());
  }
}
