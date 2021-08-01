package com.epam.digital.data.platform.settings.persistence.aspect;

import org.aspectj.lang.annotation.Pointcut;

public interface KafkaGenericListenerAspect {

  @Pointcut(
      "execution(public * com.epam.digital.data.platform.settings.persistence.listener.operation.ReadListener.read(..))")
  default void kafkaRead() {}

  @Pointcut(
      "execution(public * com.epam.digital.data.platform.settings.persistence.listener.operation.UpdateListener.update(..))")
  default void kafkaUpdate() {}
}
