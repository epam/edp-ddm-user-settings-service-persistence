package com.epam.digital.data.platform.settings.persistence.service;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.settings.model.dto.SettingsReadDto;
import com.epam.digital.data.platform.settings.model.dto.SettingsUpdateInputDto;
import com.epam.digital.data.platform.settings.model.dto.SettingsUpdateOutputDto;
import com.epam.digital.data.platform.settings.persistence.model.Settings;
import com.epam.digital.data.platform.settings.persistence.repository.SettingsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {

  private final Logger log = LoggerFactory.getLogger(SettingsService.class);

  private final SettingsRepository settingsRepository;
  private final JwtInfoProvider jwtInfoProvider;

  public SettingsService(SettingsRepository settingsRepository, JwtInfoProvider jwtInfoProvider) {
    this.settingsRepository = settingsRepository;
    this.jwtInfoProvider = jwtInfoProvider;
  }

  public SettingsReadDto findUserSettings(Request<Void> input) {
    var userClaims = jwtInfoProvider.getUserClaims(input);
    var userKeycloakId = userClaims.getSubject();
    return settingsRepository
        .findByKeycloakId(UUID.fromString(userKeycloakId))
        .map(
            settings -> {
              var settingsReadDto = new SettingsReadDto();
              settingsReadDto.setSettingsId(settings.getSettingsId());
              settingsReadDto.setEmail(settings.getEmail());
              settingsReadDto.setPhone(settings.getPhone());
              settingsReadDto.setCommunicationAllowed(settings.isCommunicationAllowed());
              return settingsReadDto;
            })
        .orElseGet(() -> {
          log.info("Settings not found in DB, returning empty ones");
          return new SettingsReadDto();
        });
  }

  public SettingsUpdateOutputDto updateSettings(Request<SettingsUpdateInputDto> input) {
    var userClaims = jwtInfoProvider.getUserClaims(input);
    var userKeycloakId = userClaims.getSubject();
    var inputPayload = input.getPayload();

    var settings =
        settingsRepository.findByKeycloakId(UUID.fromString(userKeycloakId))
            .orElseGet(() -> {
              log.info("Settings not found in DB, creating empty ones");
              return new Settings();
            });
    settings.setKeycloakId(UUID.fromString(userKeycloakId));
    settings.setEmail(inputPayload.getEmail());
    settings.setPhone(inputPayload.getPhone());
    settings.setCommunicationAllowed(inputPayload.isCommunicationAllowed());

    Settings savedSettings = settingsRepository.save(settings);

    return new SettingsUpdateOutputDto(savedSettings.getSettingsId());
  }
}
