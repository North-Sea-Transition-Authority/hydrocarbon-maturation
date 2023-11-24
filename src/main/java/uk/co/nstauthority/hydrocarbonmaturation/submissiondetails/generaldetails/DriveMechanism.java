package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

public enum DriveMechanism implements Displayable {

  DEPLETION(
      "Depletion",
      "Includes solution gas drive and gas cap drive (in oil fields with no or limited aquifer support)",
      10),
  WATER_INJECTION(
      "Water Injection",
      "",
      20),
  GAS_INJECTION(
      "Gas Injection",
      "",
      30),
  WATER_AND_GAS_INJECTION(
      "Water and Gas Injection",
      "Includes both WAG and water injection in one part and gas injection in another (as these are both " +
          "pressure support mechanisms)",
      40),
  AQUIFER_SUPPORT(
      "Aquifer Support",
      "",
      50),
  COMBINED(
      "Combined",
      "Should be selected if different drive mechanisms are being employed in different parts of the field " +
          "(either by fault panel or by reservoir/layer) e.g. injection for pressure support in one area and depletion " +
          "in another",
      60),
  OTHER(
      "Other",
      "",
      70);

  private final String displayName;
  private final String description;
  private final int displayOrder;

  DriveMechanism(String displayName, String description, int displayOrder) {
    this.displayName = displayName;
    this.description = description;
    this.displayOrder = displayOrder;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  public static Map<String, Map<String, String>> getDriveMechanismOptionsWithHintText() {
    return DisplayableEnumOptionUtil.getDisplayableOptionsWithSeparateNameAndDescription(DriveMechanism.class);
  }
}
