/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.docs

import com.google.template.soy.SoyFileSet
import io.micronaut.views.ViewsConfiguration
import io.micronaut.views.soy.SoyFileSetProvider
import jakarta.inject.Singleton

/**
 * Provide a SoyFileSet
 */
@Singleton
class ExampleSoyFileSetProvider implements SoyFileSetProvider {

    private final ViewsConfiguration viewsConfiguration

    ExampleSoyFileSetProvider(ViewsConfiguration viewsConfiguration) {
        this.viewsConfiguration = viewsConfiguration
    }

    /**
     * @return Soy file set to render templates with
     */
    @Override
    SoyFileSet provideSoyFileSet() {
        return SoyFileSet.builder()
            .add(new File(
                ExampleSoyFileSetProvider.class.getClassLoader().getResource(viewsConfiguration.getFolder() + "/home.soy").getFile()))
        .build()
    }
}
