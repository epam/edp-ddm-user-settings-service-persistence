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
