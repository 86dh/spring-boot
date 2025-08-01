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

package org.springframework.boot.jetty.autoconfigure.actuate.web.server;

import org.eclipse.jetty.server.Server;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.actuate.autoconfigure.web.server.ConditionalOnManagementPort;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextFactory;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.jetty.autoconfigure.servlet.JettyServletWebServerAutoConfiguration;
import org.springframework.boot.web.server.servlet.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for a Jetty-based servlet management context.
 *
 * @author Andy Wilkinson
 * @since 4.0.0
 */
@AutoConfiguration
@ConditionalOnClass({ Server.class, ManagementContextFactory.class })
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnManagementPort(ManagementPortType.DIFFERENT)
public final class JettyServletManagementContextAutoConfiguration {

	@Bean
	static ManagementContextFactory servletWebChildContextFactory() {
		return new ManagementContextFactory(WebApplicationType.SERVLET, ServletWebServerFactory.class,
				JettyServletWebServerAutoConfiguration.class);
	}

}
