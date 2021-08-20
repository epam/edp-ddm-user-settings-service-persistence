package com.epam.digital.data.platform.settings.persistence.audit;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.settings.persistence.audit.AuditKafkaEventsAspect;
import com.epam.digital.data.platform.settings.persistence.listener.SettingsListener;
import com.epam.digital.data.platform.settings.persistence.service.JwtValidationService;
import com.epam.digital.data.platform.settings.persistence.audit.KafkaEventsFacade;
import com.epam.digital.data.platform.settings.persistence.service.SettingsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import({AopAutoConfiguration.class})
@SpringBootTest(
    classes = {
      AuditKafkaEventsAspect.class,
      SettingsListener.class,
    })
class AuditKafkaEventsAspectTest {

  @Autowired
  private SettingsListener settingsListener;
  @MockBean
  private JwtValidationService jwtValidationService;
  @MockBean
  private SettingsService settingsService;
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
