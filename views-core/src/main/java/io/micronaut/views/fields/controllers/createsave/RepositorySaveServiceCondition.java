/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.views.fields.controllers.createsave;


import io.micronaut.context.Qualifier;
import io.micronaut.context.condition.Condition;
import io.micronaut.context.condition.ConditionContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.inject.QualifiedBeanType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class RepositorySaveServiceCondition implements Condition {

    private static final Class<?> TYPE = SaveService.class;
    private static final Logger LOG = LoggerFactory.getLogger(RepositorySaveServiceCondition.class);
    @Override
    public boolean matches(ConditionContext context) {
        if (context.getBeanResolutionContext() == null) {
            return true;
        }
        Optional<Qualifier> qualifierOptional = getCurrentQualifier(context);
        if (qualifierOptional.isPresent()) {
            Qualifier qualifier = qualifierOptional.get();

                if (LOG.isDebugEnabled()) {
                    LOG.debug("there is already a {} with qualifier {}", TYPE, qualifier);
                }


        }
        return true;
    }

    @NonNull
    private static Optional<Qualifier> getCurrentQualifier(@NonNull ConditionContext<?> context) {
        if (context.getBeanResolutionContext() != null) {
            Qualifier<?> qualifier = context.getBeanResolutionContext().getCurrentQualifier();
            if (qualifier == null) {
                qualifier = ((QualifiedBeanType<?>) context.getComponent()).getDeclaredQualifier();
            }
            return Optional.ofNullable(qualifier);
        }
        return Optional.empty();
    }

}
