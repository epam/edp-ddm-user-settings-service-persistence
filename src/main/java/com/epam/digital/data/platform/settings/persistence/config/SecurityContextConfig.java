package com.epam.digital.data.platform.settings.persistence.config;

import com.epam.digital.data.platform.settings.persistence.service.KeycloakRestClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {KeycloakRestClient.class})
public class SecurityContextConfig {

}
