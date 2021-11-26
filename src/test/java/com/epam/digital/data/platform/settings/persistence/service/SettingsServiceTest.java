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

package com.epam.digital.data.platform.settings.persistence.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.settings.model.dto.SettingsUpdateInputDto;
import com.epam.digital.data.platform.settings.persistence.model.Settings;
import com.epam.digital.data.platform.settings.persistence.repository.SettingsRepository;
import com.epam.digital.data.platform.starter.security.dto.JwtClaimsDto;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {

  private static final UUID SETTINGS_ID = UUID.fromString("321e7654-e89b-12d3-a456-426655441111");
  private static final UUID TOKEN_SUBJECT_ID = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");
  private static final String EMAIL = "email@email.com";
  private static final String UPDATED_EMAIL = "email@email.com";
  private static final String PHONE = "0000000000";
  private static final boolean COMMUNICATION_IS_ALLOWED = true;

  private SettingsService settingsService;

  @Mock
  private JwtInfoProvider jwtInfoProvider;
  @Mock
  private SettingsRepository settingsRepository;

  @Captor
  private ArgumentCaptor<Settings> updatedSettingsCaptor;

  @BeforeEach
  void beforeEach() {
    settingsService = new SettingsService(settingsRepository, jwtInfoProvider);

    var mockClaims = new JwtClaimsDto();
    mockClaims.setSubject(TOKEN_SUBJECT_ID.toString());
    when(jwtInfoProvider.getUserClaims(any())).thenReturn(mockClaims);
  }

  @Test
  void expectUserSettingsFromDbReturnedIfExist() {
    var input = new Request<Void>(null, new SecurityContext());

    var settingsFromDb = new Settings();
    settingsFromDb.setSettingsId(SETTINGS_ID);
    settingsFromDb.setKeycloakId(TOKEN_SUBJECT_ID);
    settingsFromDb.setEmail(EMAIL);
    settingsFromDb.setPhone(PHONE);
    settingsFromDb.setCommunicationAllowed(COMMUNICATION_IS_ALLOWED);

    when(settingsRepository.findByKeycloakId(TOKEN_SUBJECT_ID))
        .thenReturn(Optional.of(settingsFromDb));

    var actual = settingsService.findUserSettings(input);

    assertThat(actual.getSettingsId()).isEqualTo(settingsFromDb.getSettingsId());
    assertThat(actual.getEmail()).isEqualTo(settingsFromDb.getEmail());
    assertThat(actual.getPhone()).isEqualTo(settingsFromDb.getPhone());
    assertThat(actual.isCommunicationAllowed()).isEqualTo(settingsFromDb.isCommunicationAllowed());
  }

  @Test
  void expectDefaultUserSettingsWhenNotExistInDB() {
    var input = new Request<Void>(null, new SecurityContext());

    when(settingsRepository.findByKeycloakId(TOKEN_SUBJECT_ID))
            .thenReturn(Optional.empty());

    var actual = settingsService.findUserSettings(input);

    assertThat(actual.getSettingsId()).isNull();
    assertThat(actual.getEmail()).isNull();
    assertThat(actual.getPhone()).isNull();
    assertThat(actual.isCommunicationAllowed()).isFalse();
  }

  @Test
  void expectUpdateExistingSettingsWhenExists() {
    var inputPayload = new SettingsUpdateInputDto();
    inputPayload.setEmail(UPDATED_EMAIL);
    inputPayload.setPhone(PHONE);
    inputPayload.setCommunicationAllowed(COMMUNICATION_IS_ALLOWED);
    var input = new Request<>(inputPayload, new SecurityContext());

    var settingsFromDb = new Settings();
    settingsFromDb.setSettingsId(SETTINGS_ID);
    settingsFromDb.setKeycloakId(TOKEN_SUBJECT_ID);
    settingsFromDb.setEmail(EMAIL);
    settingsFromDb.setPhone(PHONE);
    settingsFromDb.setCommunicationAllowed(COMMUNICATION_IS_ALLOWED);

    when(settingsRepository.findByKeycloakId(TOKEN_SUBJECT_ID))
        .thenReturn(Optional.of(settingsFromDb));
    var settingsToSave = new Settings();
    settingsToSave.setSettingsId(SETTINGS_ID);
    when(settingsRepository.save(any()))
            .thenReturn(settingsToSave);

    var actual = settingsService.updateSettings(input);

    verify(settingsRepository).save(updatedSettingsCaptor.capture());
    var capturedSettings = updatedSettingsCaptor.getValue();
    assertThat(capturedSettings.getSettingsId()).isEqualTo(SETTINGS_ID);
    assertThat(capturedSettings.getEmail()).isEqualTo(UPDATED_EMAIL);
    assertThat(capturedSettings.getPhone()).isEqualTo(PHONE);
    assertThat(capturedSettings.isCommunicationAllowed()).isEqualTo(COMMUNICATION_IS_ALLOWED);

    assertThat(actual.getSettingsId()).isEqualTo(settingsToSave.getSettingsId());
  }

  @Test
  void expectCreateNewSettingsWhenNotExists() {
    var inputPayload = new SettingsUpdateInputDto();
    inputPayload.setEmail(UPDATED_EMAIL);
    inputPayload.setCommunicationAllowed(COMMUNICATION_IS_ALLOWED);
    var input = new Request<>(inputPayload, new SecurityContext());

    when(settingsRepository.findByKeycloakId(TOKEN_SUBJECT_ID))
            .thenReturn(Optional.empty());
    var settingsToSave = new Settings();
    settingsToSave.setSettingsId(SETTINGS_ID);
    when(settingsRepository.save(any()))
            .thenReturn(settingsToSave);

    var actual = settingsService.updateSettings(input);

    verify(settingsRepository).save(updatedSettingsCaptor.capture());
    var capturedSettings = updatedSettingsCaptor.getValue();
    assertThat(capturedSettings.getSettingsId()).isNull();
    assertThat(capturedSettings.getEmail()).isEqualTo(UPDATED_EMAIL);
    assertThat(capturedSettings.getPhone()).isNull();
    assertThat(capturedSettings.isCommunicationAllowed()).isEqualTo(COMMUNICATION_IS_ALLOWED);

    assertThat(actual.getSettingsId()).isEqualTo(settingsToSave.getSettingsId());
  }
}

