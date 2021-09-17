package com.epam.digital.data.platform.settings.persistence.service;

import org.keycloak.representations.idm.PublishedRealmRepresentation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "keycloak-client", url = "${keycloak.url}")
public interface KeycloakRestClient {

  @GetMapping("/auth/realms/{realm}")
  PublishedRealmRepresentation getRealmRepresentation(@PathVariable("realm") String realm);
}
