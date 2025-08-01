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

package org.springframework.boot.data.elasticsearch.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.elasticsearch.autoconfigure.ElasticsearchClientAutoConfiguration;
import org.springframework.boot.elasticsearch.autoconfigure.ElasticsearchReactiveClientAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.repository.config.EnableReactiveElasticsearchRepositories;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Data's Elasticsearch
 * support.
 *
 * @author Brian Clozel
 * @author Artur Konczak
 * @author Mohsin Husen
 * @since 4.0.0
 * @see EnableElasticsearchRepositories
 * @see EnableReactiveElasticsearchRepositories
 */
@AutoConfiguration(
		after = { ElasticsearchClientAutoConfiguration.class, ElasticsearchReactiveClientAutoConfiguration.class })
@ConditionalOnClass({ ElasticsearchTemplate.class })
@Import({ ElasticsearchDataConfiguration.BaseConfiguration.class,
		ElasticsearchDataConfiguration.JavaClientConfiguration.class,
		ElasticsearchDataConfiguration.ReactiveRestClientConfiguration.class })
public final class ElasticsearchDataAutoConfiguration {

}
