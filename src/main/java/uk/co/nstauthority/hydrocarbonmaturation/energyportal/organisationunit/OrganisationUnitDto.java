package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

public record OrganisationUnitDto(Integer organisationUnitId,
                                  String organisationUnitName,
                                  String companyHouseNumber) implements SearchSelectable {

  public String getDisplayText() {
    if (companyHouseNumber != null) {
      return "%s (%s)".formatted(organisationUnitName, companyHouseNumber);
    }
    return organisationUnitName;
  }

  @Override
  public String getSelectionId() {
    return organisationUnitId.toString();
  }

  @Override
  public String getSelectionText() {
    return getDisplayText();
  }

  public static OrganisationUnitDto fromOrganisationUnit(OrganisationUnit organisationUnit) {
    return new OrganisationUnitDto(
        organisationUnit.getOrganisationUnitId(),
        organisationUnit.getName(),
        organisationUnit.getRegisteredNumber()
    );
  }
}
