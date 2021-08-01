package com.epam.digital.data.platform.settings.persistence.listener;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Status;
import com.epam.digital.data.platform.settings.model.dto.SettingsReadDto;
import com.epam.digital.data.platform.settings.model.dto.SettingsUpdateInputDto;
import com.epam.digital.data.platform.settings.model.dto.SettingsUpdateOutputDto;
import com.epam.digital.data.platform.settings.persistence.exception.JwtValidationException;
import com.epam.digital.data.platform.settings.persistence.service.JwtValidationService;
import com.epam.digital.data.platform.settings.persistence.service.SettingsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = SettingsListener.class)
class SettingsListenerTest {

  @Autowired
  private SettingsListener settingsListener;

  @MockBean
  private SettingsService settingsService;
  @MockBean
  private JwtValidationService jwtValidationService;

  @Test
  void expectSuccessFindSettingsResponse() {
    var input = new Request<Void>();

    var responsePayload = new SettingsReadDto();

    when(jwtValidationService.isValid(input)).thenReturn(true);
    when(settingsService.findUserSettings(input)).thenReturn(responsePayload);

    var actual = settingsListener.read(input);

    assertThat(actual.getPayload()).isEqualTo(responsePayload);
    assertThat(actual.getStatus()).isEqualTo(Status.SUCCESS);
    assertThat(actual.getDetails()).isNull();
  }

  @Test
  void expectJwtInvalidResponseIfJwtExceptionOnRead() {
    var input = new Request<Void>();

    when(jwtValidationService.isValid(input)).thenThrow(new JwtValidationException(""));

    var actual = settingsListener.read(input);

    assertThat(actual.getPayload()).isNull();
    assertThat(actual.getStatus()).isEqualTo(Status.JWT_INVALID);
    assertThat(actual.getDetails()).isNull();
  }

  @Test
  void expectJwtInvalidResponseIfInvalidJwtOnRead() {
    var input = new Request<Void>();

    when(jwtValidationService.isValid(input)).thenReturn(false);

    var actual = settingsListener.read(input);

    assertThat(actual.getPayload()).isNull();
    assertThat(actual.getStatus()).isEqualTo(Status.JWT_INVALID);
    assertThat(actual.getDetails()).isNull();
  }

  @Test
  void expectSuccessUpdateSettingsResponse() {
    var input = new Request<SettingsUpdateInputDto>();

    var responsePayload = new SettingsUpdateOutputDto();

    when(jwtValidationService.isValid(input)).thenReturn(true);
    when(settingsService.updateSettings(input)).thenReturn(responsePayload);

    var actual = settingsListener.update(input);

    assertThat(actual.getPayload()).isEqualTo(responsePayload);
    assertThat(actual.getStatus()).isEqualTo(Status.SUCCESS);
    assertThat(actual.getDetails()).isNull();
  }

  @Test
  void expectJwtInvalidResponseIfJwtExceptionOnUpdate() {
    var input = new Request<SettingsUpdateInputDto>();

    when(jwtValidationService.isValid(input)).thenThrow(new JwtValidationException(""));

    var actual = settingsListener.update(input);

    assertThat(actual.getPayload()).isNull();
    assertThat(actual.getStatus()).isEqualTo(Status.JWT_INVALID);
    assertThat(actual.getDetails()).isNull();
  }

  @Test
  void expectJwtInvalidResponseIfInvalidOnUpdate() {
    var input = new Request<SettingsUpdateInputDto>();

    when(jwtValidationService.isValid(input)).thenReturn(false);

    var actual = settingsListener.update(input);

    assertThat(actual.getPayload()).isNull();
    assertThat(actual.getStatus()).isEqualTo(Status.JWT_INVALID);
    assertThat(actual.getDetails()).isNull();
  }
}
