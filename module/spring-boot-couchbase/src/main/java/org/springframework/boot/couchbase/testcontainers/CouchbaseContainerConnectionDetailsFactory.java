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

package org.springframework.boot.couchbase.testcontainers;

import org.jspecify.annotations.Nullable;
import org.testcontainers.couchbase.CouchbaseContainer;

import org.springframework.boot.couchbase.autoconfigure.CouchbaseConnectionDetails;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionDetailsFactory;
import org.springframework.boot.testcontainers.service.connection.ContainerConnectionSource;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

/**
 * {@link ContainerConnectionDetailsFactory} to create {@link CouchbaseConnectionDetails}
 * from a {@link ServiceConnection @ServiceConnection}-annotated
 * {@link CouchbaseContainer}.
 *
 * @author Moritz Halbritter
 * @author Andy Wilkinson
 * @author Phillip Webb
 */
class CouchbaseContainerConnectionDetailsFactory
		extends ContainerConnectionDetailsFactory<CouchbaseContainer, CouchbaseConnectionDetails> {

	@Override
	protected CouchbaseConnectionDetails getContainerConnectionDetails(
			ContainerConnectionSource<CouchbaseContainer> source) {
		return new CouchbaseContainerConnectionDetails(source);
	}

	/**
	 * {@link CouchbaseConnectionDetails} backed by a {@link ContainerConnectionSource}.
	 */
	private static final class CouchbaseContainerConnectionDetails
			extends ContainerConnectionDetails<CouchbaseContainer> implements CouchbaseConnectionDetails {

		private CouchbaseContainerConnectionDetails(ContainerConnectionSource<CouchbaseContainer> source) {
			super(source);
		}

		@Override
		public String getUsername() {
			return getContainer().getUsername();
		}

		@Override
		public String getPassword() {
			return getContainer().getPassword();
		}

		@Override
		public String getConnectionString() {
			return getContainer().getConnectionString();
		}

		@Override
		public @Nullable SslBundle getSslBundle() {
			return super.getSslBundle();
		}

	}

}
