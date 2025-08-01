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

package org.springframework.boot.metrics.autoconfigure.export.statsd;

import java.time.Duration;

import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdFlavor;
import io.micrometer.statsd.StatsdProtocol;
import org.jspecify.annotations.Nullable;

import org.springframework.boot.metrics.autoconfigure.export.properties.PropertiesConfigAdapter;

/**
 * Adapter to convert {@link StatsdProperties} to a {@link StatsdConfig}.
 *
 * @author Jon Schneider
 * @since 4.0.0
 */
public class StatsdPropertiesConfigAdapter extends PropertiesConfigAdapter<StatsdProperties> implements StatsdConfig {

	public StatsdPropertiesConfigAdapter(StatsdProperties properties) {
		super(properties);
	}

	@Override
	public @Nullable String get(String s) {
		return null;
	}

	@Override
	public String prefix() {
		return "management.statsd.metrics.export";
	}

	@Override
	public StatsdFlavor flavor() {
		return getRequired(StatsdProperties::getFlavor, StatsdConfig.super::flavor);
	}

	@Override
	public boolean enabled() {
		return getRequired(StatsdProperties::isEnabled, StatsdConfig.super::enabled);
	}

	@Override
	public String host() {
		return getRequired(StatsdProperties::getHost, StatsdConfig.super::host);
	}

	@Override
	public int port() {
		return getRequired(StatsdProperties::getPort, StatsdConfig.super::port);
	}

	@Override
	public StatsdProtocol protocol() {
		return getRequired(StatsdProperties::getProtocol, StatsdConfig.super::protocol);
	}

	@Override
	public int maxPacketLength() {
		return getRequired(StatsdProperties::getMaxPacketLength, StatsdConfig.super::maxPacketLength);
	}

	@Override
	public Duration pollingFrequency() {
		return getRequired(StatsdProperties::getPollingFrequency, StatsdConfig.super::pollingFrequency);
	}

	@Override
	public Duration step() {
		return getRequired(StatsdProperties::getStep, StatsdConfig.super::step);
	}

	@Override
	public boolean publishUnchangedMeters() {
		return getRequired(StatsdProperties::isPublishUnchangedMeters, StatsdConfig.super::publishUnchangedMeters);
	}

	@Override
	public boolean buffered() {
		return getRequired(StatsdProperties::isBuffered, StatsdConfig.super::buffered);
	}

}
