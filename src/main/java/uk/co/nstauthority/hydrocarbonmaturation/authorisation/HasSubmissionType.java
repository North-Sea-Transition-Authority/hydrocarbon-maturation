package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Security
public @interface HasSubmissionType {
  SecuritySubmissionTypes submissionTypes();
  Mode mode() default Mode.INCLUDE;

  enum Mode {
    INCLUDE,
    EXCLUDE
  }
}
