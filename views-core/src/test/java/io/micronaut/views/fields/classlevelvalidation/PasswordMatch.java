package io.micronaut.views.fields.classlevelvalidation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated type must have a recipient.
 * @author Sergio del Amo
 * @since 1.0.0
 */
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface PasswordMatch {
    /**
     * AnyRecipient message.
     */
    String MESSAGE = "io.micronaut.views.fields.classlevelvalidation.PasswordMatch.message";

    /**
     * @return message The error message
     */
    String message() default "{" + MESSAGE + "}";

    /**
     * @return Groups to control the order in which constraints are evaluated,
     * or to perform validation of the partial state of a JavaBean.
     */
    Class<?>[] groups() default {};

    /**
     * @return Payloads used by validation clients to associate some metadata information with a given constraint declaration
     */
    Class<? extends Payload>[] payload() default {};

}
