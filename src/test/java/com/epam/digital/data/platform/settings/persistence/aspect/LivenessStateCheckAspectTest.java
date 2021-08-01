package com.epam.digital.data.platform.settings.persistence.aspect;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.settings.persistence.exception.JwtValidationException;
import com.epam.digital.data.platform.settings.persistence.listener.SettingsListener;
import com.epam.digital.data.platform.settings.persistence.service.JwtInfoProvider;
import com.epam.digital.data.platform.settings.persistence.service.JwtValidationService;
import com.epam.digital.data.platform.settings.persistence.service.SettingsService;
import com.epam.digital.data.platform.starter.actuator.livenessprobe.LivenessStateHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Import({AopAutoConfiguration.class})
@SpringBootTest(classes = {SettingsListener.class, LivenessStateCheckAspect.class})
class LivenessStateCheckAspectTest {

  @Autowired
  private SettingsListener settingsListener;
  @MockBean
  private JwtValidationService jwtValidationService;
  @MockBean
  private SettingsService settingsService;
  @MockBean
  private LivenessStateHandler livenessStateHandler;

  @Test
  void expectStateHandlerIsCalledAfterKafkaListener() {
    when(jwtValidationService.isValid(any())).thenThrow(new JwtValidationException(""));

    settingsListener.read(new Request<>());

    verify(livenessStateHandler).handleResponse(any(), any());
  }
}
