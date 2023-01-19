package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IsMemberOfTeamOrHasPermission {
  RolePermission[] permissions() default {};
}