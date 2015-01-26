/**
 * Copyright (C) 2013-2014 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.http.templating.helpers;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LiveReloadHelperTest {

    @Test
    public void should_return_nothing_in_prodmode() {
        assertThat(new LiveReloadHelper(true).livereload(null,null)).isEmpty();
    }

    @Test
    public void should_return_livereloadscript_in_devmode() {
        assertThat(new LiveReloadHelper(false).livereload(null,null)).contains("/livereload.js");
    }

}