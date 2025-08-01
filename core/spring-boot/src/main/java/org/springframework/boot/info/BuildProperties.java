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

package org.springframework.boot.info;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import org.jspecify.annotations.Nullable;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.info.BuildProperties.BuildPropertiesRuntimeHints;
import org.springframework.context.annotation.ImportRuntimeHints;

/**
 * Provide build-related information such as group and artifact.
 *
 * @author Stephane Nicoll
 * @since 1.4.0
 */
@ImportRuntimeHints(BuildPropertiesRuntimeHints.class)
public class BuildProperties extends InfoProperties {

	/**
	 * Create an instance with the specified entries.
	 * @param entries the information to expose
	 */
	public BuildProperties(Properties entries) {
		super(processEntries(entries));
	}

	/**
	 * Return the groupId of the project or {@code null}.
	 * @return the group
	 */
	public @Nullable String getGroup() {
		return get("group");
	}

	/**
	 * Return the artifactId of the project or {@code null}.
	 * @return the artifact
	 */
	public @Nullable String getArtifact() {
		return get("artifact");
	}

	/**
	 * Return the name of the project or {@code null}.
	 * @return the name
	 */
	public @Nullable String getName() {
		return get("name");
	}

	/**
	 * Return the version of the project or {@code null}.
	 * @return the version
	 */
	public @Nullable String getVersion() {
		return get("version");
	}

	/**
	 * Return the timestamp of the build or {@code null}.
	 * <p>
	 * If the original value could not be parsed properly, it is still available with the
	 * {@code time} key.
	 * @return the build time
	 * @see #get(String)
	 */
	public @Nullable Instant getTime() {
		return getInstant("time");
	}

	private static Properties processEntries(Properties properties) {
		coerceDate(properties, "time");
		return properties;
	}

	private static void coerceDate(Properties properties, String key) {
		String value = properties.getProperty(key);
		if (value != null) {
			try {
				String updatedValue = String
					.valueOf(DateTimeFormatter.ISO_INSTANT.parse(value, Instant::from).toEpochMilli());
				properties.setProperty(key, updatedValue);
			}
			catch (DateTimeException ex) {
				// Ignore and store the original value
			}
		}
	}

	static class BuildPropertiesRuntimeHints implements RuntimeHintsRegistrar {

		@Override
		public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
			hints.resources().registerPattern("META-INF/build-info.properties");
		}

	}

}
