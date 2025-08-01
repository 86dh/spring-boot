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

package org.springframework.boot;

import java.io.PrintStream;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

/**
 * Default Banner implementation which writes the 'Spring' banner.
 *
 * @author Phillip Webb
 */
class SpringBootBanner implements Banner {

	private static final String BANNER = """
			  .   ____          _            __ _ _
			 /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\
			( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\
			 \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )
			  '  |____| .__|_| |_|_| |_\\__, | / / / /
			 =========|_|==============|___/=/_/_/_/
			""";

	private static final String SPRING_BOOT = " :: Spring Boot :: ";

	private static final int STRAP_LINE_SIZE = 42;

	@Override
	public void printBanner(Environment environment, @Nullable Class<?> sourceClass, PrintStream printStream) {
		printStream.println();
		printStream.println(BANNER);
		String version = String.format(" (v%s)", SpringBootVersion.getVersion());
		String padding = " ".repeat(Math.max(0, STRAP_LINE_SIZE - (version.length() + SPRING_BOOT.length())));
		printStream.println(AnsiOutput.toString(AnsiColor.GREEN, SPRING_BOOT, AnsiColor.DEFAULT, padding,
				AnsiStyle.FAINT, version));
		printStream.println();
	}

}
