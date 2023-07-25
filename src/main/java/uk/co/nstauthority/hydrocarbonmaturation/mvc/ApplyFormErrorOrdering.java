package uk.co.nstauthority.hydrocarbonmaturation.mvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marker to apply the form error ordering.
 * Can be removed once all forms are set to the use the error ordering annotation
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplyFormErrorOrdering {
}
