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

package com.epam.digital.data.platform.settings.persistence.aspect;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.settings.persistence.audit.AuditableListener;
import com.epam.digital.data.platform.settings.persistence.audit.KafkaAuditProcessor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

  private final KafkaAuditProcessor kafkaAuditProcessor;

  public AuditAspect(KafkaAuditProcessor kafkaAuditProcessor) {
    this.kafkaAuditProcessor = kafkaAuditProcessor;
  }

  @Around("@annotation(com.epam.digital.data.platform.settings.persistence.audit.AuditableListener) && args(request)")
  Object kafkaAdvice(ProceedingJoinPoint joinPoint, Request<?> request) throws Throwable {
    var signature = (MethodSignature) joinPoint.getSignature();
    var operation = signature.getMethod().getAnnotation(AuditableListener.class).value();
    return kafkaAuditProcessor.process(joinPoint, operation, request);
  }
}
