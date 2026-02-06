package uk.co.nstauthority.hydrocarbonmaturation.enumutil;

import uk.co.fivium.digitalenummaterialisationlibrary.enummaterialisation.MaterialisableEnum;

/**
 * Interface which allows implementing classes to expose their human readable representation consistently.
*/
public interface Displayable extends MaterialisableEnum {

  String getDisplayName();

  String name();

  default String getEnumName() {
    return name();
  }

  default String getDescription() {
    return "";
  }

  default String getSecondaryOptionName() {
    return "";
  }
}
