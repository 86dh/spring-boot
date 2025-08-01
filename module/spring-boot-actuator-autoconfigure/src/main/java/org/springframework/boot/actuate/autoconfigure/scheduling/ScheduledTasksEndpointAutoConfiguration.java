/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.autoconfigure.scheduling;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.scheduling.ScheduledTasksEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.config.ScheduledTaskHolder;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link ScheduledTasksEndpoint}.
 *
 * @author Andy Wilkinson
 * @since 2.0.0
 */
@AutoConfiguration
@ConditionalOnAvailableEndpoint(ScheduledTasksEndpoint.class)
public final class ScheduledTasksEndpointAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	ScheduledTasksEndpoint scheduledTasksEndpoint(ObjectProvider<ScheduledTaskHolder> holders) {
		return new ScheduledTasksEndpoint(holders.orderedStream().toList());
	}

}
