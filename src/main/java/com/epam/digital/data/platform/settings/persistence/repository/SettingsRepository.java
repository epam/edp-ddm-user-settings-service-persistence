package com.epam.digital.data.platform.settings.persistence.repository;

import com.epam.digital.data.platform.settings.persistence.model.Settings;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface SettingsRepository extends CrudRepository<Settings, UUID> {
  Optional<Settings> findByKeycloakId(UUID keycloakId);
}
