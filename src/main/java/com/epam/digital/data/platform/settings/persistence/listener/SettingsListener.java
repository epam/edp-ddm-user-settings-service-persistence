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

package com.epam.digital.data.platform.settings.persistence.listener;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Response;
import com.epam.digital.data.platform.model.core.kafka.Status;
import com.epam.digital.data.platform.settings.model.dto.SettingsReadByKeycloakIdInputDto;
import com.epam.digital.data.platform.settings.model.dto.SettingsReadDto;
import com.epam.digital.data.platform.settings.model.dto.SettingsUpdateInputDto;
import com.epam.digital.data.platform.settings.model.dto.SettingsUpdateOutputDto;
import com.epam.digital.data.platform.settings.persistence.audit.AuditableListener;
import com.epam.digital.data.platform.settings.persistence.audit.AuditableListener.Operation;
import com.epam.digital.data.platform.settings.persistence.exception.RequestProcessingException;
import com.epam.digital.data.platform.settings.persistence.service.JwtValidationService;
import com.epam.digital.data.platform.settings.persistence.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class SettingsListener {

  private final Logger log = LoggerFactory.getLogger(SettingsListener.class);

  private final SettingsService settingsService;
  private final JwtValidationService jwtValidationService;

  public SettingsListener(
      SettingsService settingsService,
      JwtValidationService jwtValidationService) {
    this.settingsService = settingsService;
    this.jwtValidationService = jwtValidationService;
  }

  @AuditableListener(Operation.READ)
  @KafkaListener(
      topics = "\u0023{kafkaProperties.topics['read-settings']}",
      groupId = "\u0023{kafkaProperties.consumer.groupId}",
      containerFactory = "concurrentKafkaListenerContainerFactory")
  @SendTo
  public Response<SettingsReadDto> read(Request<Void> input) {
    log.info("Read event received");

    var response = new Response<SettingsReadDto>();

    try {
      if (!jwtValidationService.isValid(input)) {
        response.setStatus(Status.JWT_INVALID);
        return response;
      }

      var userSettings = settingsService.findUserSettings(input);
      response.setPayload(userSettings);
      response.setStatus(Status.SUCCESS);
    } catch (RequestProcessingException e) {
      log.error("Exception while request processing", e);
      response.setStatus(e.getKafkaResponseStatus());
      response.setDetails(e.getDetails());
    }

    return response;
  }

  @AuditableListener(Operation.READ)
  @KafkaListener(
          topics = "\u0023{kafkaProperties.topics['read-settings-by-keycloak-id']}",
          groupId = "\u0023{kafkaProperties.consumer.groupId}",
          containerFactory = "concurrentKafkaListenerContainerFactory")
  @SendTo
  public Response<SettingsReadDto> readByKeycloakId(Request<SettingsReadByKeycloakIdInputDto> input) {
    log.info("Read event received");

    var response = new Response<SettingsReadDto>();

    try {
      if (!jwtValidationService.isValid(input)) {
        response.setStatus(Status.JWT_INVALID);
        return response;
      }

      var userSettings = settingsService.findUserSettingsByKeycloakId(input);
      response.setPayload(userSettings);
      response.setStatus(Status.SUCCESS);
    } catch (RequestProcessingException e) {
      log.error("Exception while request processing", e);
      response.setStatus(e.getKafkaResponseStatus());
      response.setDetails(e.getDetails());
    }

    return response;
  }

  @AuditableListener(Operation.UPDATE)
  @KafkaListener(
      topics = "\u0023{kafkaProperties.topics['update-settings']}",
      groupId = "\u0023{kafkaProperties.consumer.groupId}",
      containerFactory = "concurrentKafkaListenerContainerFactory")
  @SendTo
  public Response<SettingsUpdateOutputDto> update(Request<SettingsUpdateInputDto> input) {
    log.info("Update event received");

    var response = new Response<SettingsUpdateOutputDto>();

    try {
      if (!jwtValidationService.isValid(input)) {
        response.setStatus(Status.JWT_INVALID);
        return response;
      }

      var outputDto = settingsService.updateSettings(input);
      response.setPayload(outputDto);
      response.setStatus(Status.SUCCESS);
    } catch (RequestProcessingException e) {
      log.error("Exception while request processing", e);
      response.setStatus(e.getKafkaResponseStatus());
      response.setDetails(e.getDetails());
    }

    return response;
  }
}
