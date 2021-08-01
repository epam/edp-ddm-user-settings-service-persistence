package com.epam.digital.data.platform.settings.persistence.listener.operation;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Response;

public interface ReadListener<I, O> {
  Response<I> read(Request<O> input);
}
