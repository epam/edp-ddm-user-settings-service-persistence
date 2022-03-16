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
import com.epam.digital.data.platform.model.core.kafka.SecurityContext;
import com.epam.digital.data.platform.model.core.kafka.Status;
import com.epam.digital.data.platform.settings.model.dto.SettingsReadDto;
import com.epam.digital.data.platform.settings.model.dto.SettingsUpdateInputDto;
import com.epam.digital.data.platform.settings.model.dto.SettingsUpdateOutputDto;
import com.epam.digital.data.platform.settings.persistence.TestUtils;
import com.epam.digital.data.platform.settings.persistence.service.TraceService;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092",
        "port=9092"})
class SettingsListenerIT {

    private static final UUID SETTINGS_ID = UUID.fromString("321e7654-e89b-12d3-a456-426655441111");
    private static final String DIGITAL_SIGNATURE = "digital_signature";
    private static final String DIGITAL_SIGNATURE_DERIVED = "digital_signature_derived";
    private static final String TRACE_REQUEST_ID = "1";
    private static final String SETTINGS_EMAIL = "settings1@gmail.com";
    private static final String PHONE = "0951111111";

    @Autowired
    private SettingsListener settingsListener;

    @MockBean
    private TraceService traceService;

    @Test
    void shouldUpdateSettings() throws IOException {
        SettingsUpdateInputDto settings = new SettingsUpdateInputDto();
        settings.setEmail(SETTINGS_EMAIL);
        settings.setPhone(PHONE);
        settings.setCommunicationAllowed(false);
        Request<SettingsUpdateInputDto> request = new Request<>();
        request.setPayload(settings);
        request.setSecurityContext(securityContext());

        Mockito.when(traceService.getRequestId()).thenReturn(TRACE_REQUEST_ID);

        Response<SettingsUpdateOutputDto> update = settingsListener.update(request);
        SettingsUpdateOutputDto payload = update.getPayload();
        assertNotNull(payload);
        assertThat(update.getStatus()).isEqualTo(Status.SUCCESS);
        assertThat(payload.getSettingsId()).isEqualTo(SETTINGS_ID);

    }

    @Test
    void shouldFindSettings() throws IOException {
        Request<Void> input = new Request<>();
        input.setSecurityContext(securityContext());
        Mockito.when(traceService.getRequestId()).thenReturn(TRACE_REQUEST_ID);

        Response<SettingsReadDto> read = settingsListener.read(input);
        SettingsReadDto payload = read.getPayload();
        assertNotNull(payload);
        assertThat(payload.getSettingsId()).isEqualTo(SETTINGS_ID);
        assertThat(read.getStatus()).isEqualTo(Status.SUCCESS);
    }

    private SecurityContext securityContext() throws IOException {
        var token = TestUtils.readClassPathResource("/token.txt");
        var context = new SecurityContext();
        context.setAccessToken(token);
        context.setDigitalSignature(DIGITAL_SIGNATURE);
        context.setDigitalSignatureDerived(DIGITAL_SIGNATURE_DERIVED);
        return context;
    }
}
