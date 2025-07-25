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

package org.springframework.boot.mustache.servlet.view;

import java.util.Collections;

import com.samskivert.mustache.Mustache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.testsupport.classpath.resources.WithResource;
import org.springframework.boot.web.context.servlet.AnnotationConfigServletWebApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link MustacheView}.
 *
 * @author Dave Syer
 */
class MustacheViewTests {

	private final MockHttpServletRequest request = new MockHttpServletRequest();

	private final MockHttpServletResponse response = new MockHttpServletResponse();

	private final AnnotationConfigServletWebApplicationContext context = new AnnotationConfigServletWebApplicationContext();

	@BeforeEach
	void init() {
		this.context.refresh();
		MockServletContext servletContext = new MockServletContext();
		this.context.setServletContext(servletContext);
		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
	}

	@Test
	@WithResource(name = "template.html", content = "Hello {{World}}")
	void viewResolvesHandlebars() throws Exception {
		MustacheView view = new MustacheView();
		view.setCompiler(Mustache.compiler());
		view.setUrl("classpath:template.html");
		view.setApplicationContext(this.context);
		view.render(Collections.singletonMap("World", "Spring"), this.request, this.response);
		assertThat(this.response.getContentAsString().trim()).isEqualTo("Hello Spring");
	}

}
