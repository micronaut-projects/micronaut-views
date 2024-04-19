/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.views.turbo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to ease the return of {@link TurboStream} in routes.
 *
 * @author Sergio del Amo
 * @since 3.3.0
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface TurboView {

    /**
     * @return The View Name for the route.
     */
    String value() default "";

    /**
     *
     * @return The Turbo Stream action. Defaults to `update`.
     */
    TurboStreamAction action() default TurboStreamAction.UPDATE;

    /**
     *
     * @return Target DOM ID.
     */
    String targetDomId() default "";

    /**
     *
     * @return Target CSS Query Selector.
     */
    String targetCssQuerySelector() default "";

    /**
     *
     * @return request-id attribute, only relevant when action=refresh
     */
    String requestId() default "";

    /**
     *
     * @return morph only the children of the element designated by the target dom id
     */
    boolean childrenOnly() default false;
}
