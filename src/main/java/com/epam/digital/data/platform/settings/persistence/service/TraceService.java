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

package com.epam.digital.data.platform.settings.persistence.service;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class TraceService {

  private static final String MDC_TRACE_ID_HEADER = "X-B3-TraceId";

  public String getRequestId() {
    return MDC.get(MDC_TRACE_ID_HEADER);
  }
}
