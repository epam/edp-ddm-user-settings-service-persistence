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

package com.epam.digital.data.platform.settings.persistence.exception;

import com.epam.digital.data.platform.model.core.kafka.Status;

public class RequestProcessingException extends RuntimeException {

  private final Status kafkaResponseStatus;
  private String details;

  public RequestProcessingException(String message, Status kafkaResponseStatus) {
    super(message);
    this.kafkaResponseStatus = kafkaResponseStatus;
  }

  public RequestProcessingException(String message, Throwable cause, Status kafkaResponseStatus) {
    super(message, cause);
    this.kafkaResponseStatus = kafkaResponseStatus;
  }

  public Status getKafkaResponseStatus() {
    return kafkaResponseStatus;
  }

  public String getDetails() {
    return details;
  }
}
