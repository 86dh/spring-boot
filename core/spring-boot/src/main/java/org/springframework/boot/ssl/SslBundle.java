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

package org.springframework.boot.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.jspecify.annotations.Nullable;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

/**
 * A bundle of trust material that can be used to establish an SSL connection.
 *
 * @author Scott Frederick
 * @author Moritz Halbritter
 * @since 3.1.0
 */
public interface SslBundle {

	/**
	 * The default protocol to use.
	 */
	String DEFAULT_PROTOCOL = "TLS";

	/**
	 * Return the {@link SslStoreBundle} that can be used to access this bundle's key and
	 * trust stores.
	 * @return the {@code SslStoreBundle} instance for this bundle
	 */
	SslStoreBundle getStores();

	/**
	 * Return a reference to the key that should be used for this bundle or
	 * {@link SslBundleKey#NONE}.
	 * @return a reference to the SSL key that should be used
	 */
	SslBundleKey getKey();

	/**
	 * Return {@link SslOptions} that should be applied when establishing the SSL
	 * connection.
	 * @return the options that should be applied
	 */
	SslOptions getOptions();

	/**
	 * Return the protocol to use when establishing the connection. Values should be
	 * supported by {@link SSLContext#getInstance(String)}.
	 * @return the SSL protocol
	 * @see SSLContext#getInstance(String)
	 */
	String getProtocol();

	/**
	 * Return the {@link SslManagerBundle} that can be used to access this bundle's
	 * {@link KeyManager key} and {@link TrustManager trust} managers.
	 * @return the {@code SslManagerBundle} instance for this bundle
	 */
	SslManagerBundle getManagers();

	/**
	 * Factory method to create a new {@link SSLContext} for this bundle.
	 * @return a new {@link SSLContext} instance
	 */
	default SSLContext createSslContext() {
		return getManagers().createSslContext(getProtocol());
	}

	/**
	 * Factory method to create a new {@link SslBundle} instance.
	 * @param stores the stores or {@code null}
	 * @return a new {@link SslBundle} instance
	 */
	static SslBundle of(@Nullable SslStoreBundle stores) {
		return of(stores, null, null);
	}

	/**
	 * Factory method to create a new {@link SslBundle} instance.
	 * @param stores the stores or {@code null}
	 * @param key the key or {@code null}
	 * @return a new {@link SslBundle} instance
	 */
	static SslBundle of(@Nullable SslStoreBundle stores, @Nullable SslBundleKey key) {
		return of(stores, key, null);
	}

	/**
	 * Factory method to create a new {@link SslBundle} instance.
	 * @param stores the stores or {@code null}
	 * @param key the key or {@code null}
	 * @param options the options or {@code null}
	 * @return a new {@link SslBundle} instance
	 */
	static SslBundle of(@Nullable SslStoreBundle stores, @Nullable SslBundleKey key, @Nullable SslOptions options) {
		return of(stores, key, options, null);
	}

	/**
	 * Factory method to create a new {@link SslBundle} instance.
	 * @param stores the stores or {@code null}
	 * @param key the key or {@code null}
	 * @param options the options or {@code null}
	 * @param protocol the protocol or {@code null}
	 * @return a new {@link SslBundle} instance
	 */
	static SslBundle of(@Nullable SslStoreBundle stores, @Nullable SslBundleKey key, @Nullable SslOptions options,
			@Nullable String protocol) {
		return of(stores, key, options, protocol, null);
	}

	/**
	 * Factory method to create a new {@link SslBundle} instance.
	 * @param stores the stores or {@code null}
	 * @param key the key or {@code null}
	 * @param options the options or {@code null}
	 * @param protocol the protocol or {@code null}
	 * @param managers the managers or {@code null}
	 * @return a new {@link SslBundle} instance
	 */
	static SslBundle of(@Nullable SslStoreBundle stores, @Nullable SslBundleKey key, @Nullable SslOptions options,
			@Nullable String protocol, @Nullable SslManagerBundle managers) {
		SslManagerBundle managersToUse = (managers != null) ? managers : SslManagerBundle.from(stores, key);
		return new SslBundle() {

			@Override
			public SslStoreBundle getStores() {
				return (stores != null) ? stores : SslStoreBundle.NONE;
			}

			@Override
			public SslBundleKey getKey() {
				return (key != null) ? key : SslBundleKey.NONE;
			}

			@Override
			public SslOptions getOptions() {
				return (options != null) ? options : SslOptions.NONE;
			}

			@Override
			public String getProtocol() {
				return (!StringUtils.hasText(protocol)) ? DEFAULT_PROTOCOL : protocol;
			}

			@Override
			public SslManagerBundle getManagers() {
				return managersToUse;
			}

			@Override
			public String toString() {
				ToStringCreator creator = new ToStringCreator(this);
				creator.append("key", getKey());
				creator.append("options", getOptions());
				creator.append("protocol", getProtocol());
				creator.append("stores", getStores());
				return creator.toString();
			}

		};
	}

	/**
	 * Factory method to create a new {@link SslBundle} which uses the system defaults.
	 * @return a new {@link SslBundle} instance
	 * @since 3.5.0
	 */
	static SslBundle systemDefault() {
		try {
			KeyManagerFactory keyManagerFactory = KeyManagerFactory
				.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(null, null);
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init((KeyStore) null);
			SSLContext sslContext = SSLContext.getDefault();
			return of(null, null, null, null, new SslManagerBundle() {
				@Override
				public KeyManagerFactory getKeyManagerFactory() {
					return keyManagerFactory;
				}

				@Override
				public TrustManagerFactory getTrustManagerFactory() {
					return trustManagerFactory;
				}

				@Override
				public SSLContext createSslContext(String protocol) {
					return sslContext;
				}
			});
		}
		catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException ex) {
			throw new IllegalStateException("Could not initialize system default SslBundle: " + ex.getMessage(), ex);
		}
	}

}
