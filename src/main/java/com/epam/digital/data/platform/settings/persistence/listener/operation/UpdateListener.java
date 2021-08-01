package com.epam.digital.data.platform.settings.persistence.listener.operation;

import com.epam.digital.data.platform.model.core.kafka.Request;
import com.epam.digital.data.platform.model.core.kafka.Response;

public interface UpdateListener<I, O> {
  Response<I> update(Request<O> input);
}
