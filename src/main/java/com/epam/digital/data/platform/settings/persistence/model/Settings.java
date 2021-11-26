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

package com.epam.digital.data.platform.settings.persistence.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table
public class Settings {
  @Id
  private UUID settingsId;
  private UUID keycloakId;
  private String email;
  private String phone;
  @Column("communication_is_allowed")
  private boolean communicationAllowed;

  public UUID getSettingsId() {
    return settingsId;
  }

  public void setSettingsId(UUID settingsId) {
    this.settingsId = settingsId;
  }

  public UUID getKeycloakId() {
    return keycloakId;
  }

  public void setKeycloakId(UUID keycloakId) {
    this.keycloakId = keycloakId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public boolean isCommunicationAllowed() {
    return communicationAllowed;
  }

  public void setCommunicationAllowed(boolean communicationAllowed) {
    this.communicationAllowed = communicationAllowed;
  }
}
