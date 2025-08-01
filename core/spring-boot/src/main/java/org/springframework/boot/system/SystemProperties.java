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

package org.springframework.boot.system;

import org.jspecify.annotations.Nullable;

/**
 * Access to system properties.
 *
 * @author Phillip Webb
 * @since 2.0.0
 */
public final class SystemProperties {

	private SystemProperties() {
	}

	public static @Nullable String get(String... properties) {
		for (String property : properties) {
			try {
				String override = System.getProperty(property);
				override = (override != null) ? override : System.getenv(property);
				if (override != null) {
					return override;
				}
			}
			catch (Throwable ex) {
				System.err.println("Could not resolve '" + property + "' as system property: " + ex);
			}
		}
		return null;
	}

}
