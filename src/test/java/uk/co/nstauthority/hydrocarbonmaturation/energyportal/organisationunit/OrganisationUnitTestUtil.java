package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import java.util.List;
import uk.co.fivium.energyportalapi.generated.types.OrganisationGroup;
import uk.co.fivium.energyportalapi.generated.types.OrganisationNameHistory;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class OrganisationUnitTestUtil {

  private OrganisationUnitTestUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static Builder Builder() {
    return new Builder();
  }

  public static class Builder {
    private Integer organisationUnitId = 1;
    private String organisationName = "Fivium";
    private String organisationNumber = "25";
    private List<OrganisationGroup> organisationGroups = null;
    private boolean isDuplicate = false;
    private List<OrganisationNameHistory> organisationNameHistory = null;

    private Builder(){}

    public Builder withOrganisationId(Integer organisationId) {
      this.organisationUnitId = organisationId;
      return this;
    }

    public Builder withOrganisationName(String organisationName) {
      this.organisationName = organisationName;
      return this;
    }

    public Builder withOrganisationNumber(String organisationNumber) {
      this.organisationNumber = organisationNumber;
      return this;
    }

    public Builder withOrganisationGroups(List<OrganisationGroup> organisationGroups) {
      this.organisationGroups = organisationGroups;
      return this;
    }

    public Builder withIsDuplicate(boolean isDuplicate) {
      this.isDuplicate = isDuplicate;
      return this;
    }

    public Builder withOrganisationNameHistory(List<OrganisationNameHistory> organisationNameHistory) {
      this.organisationNameHistory = organisationNameHistory;
      return this;
    }

    public OrganisationUnit build() {
      var organisationUnit = new OrganisationUnit();
      organisationUnit.setOrganisationUnitId(organisationUnitId);
      organisationUnit.setRegisteredNumber(organisationNumber);
      organisationUnit.setName(organisationName);
      organisationUnit.setOrganisationGroups(organisationGroups);
      organisationUnit.setIsDuplicate(isDuplicate);
      organisationUnit.setOrganisationNameHistory(organisationNameHistory);
      return organisationUnit;
    }
  }
}
