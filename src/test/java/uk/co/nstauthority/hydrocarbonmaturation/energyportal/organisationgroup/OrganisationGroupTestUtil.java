package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationgroup;

public class OrganisationGroupTestUtil {
  private OrganisationGroupTestUtil() {
  }

  public static OrganisationGroupDto createOrganisationGroupDto(int organisationGroupId, String organisationGroupName) {
    var organisationGroupDto = new OrganisationGroupDto();
    organisationGroupDto.setOrganisationGroupId(organisationGroupId);
    organisationGroupDto.setOrganisationGroupName(organisationGroupName);
    return organisationGroupDto;
  }
}
