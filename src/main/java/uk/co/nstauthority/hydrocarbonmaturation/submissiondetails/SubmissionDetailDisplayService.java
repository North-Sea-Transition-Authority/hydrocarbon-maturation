package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;

@Service
public class SubmissionDetailDisplayService {
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final LicenceQueryService licenceQueryService;
  static final String NOT_PROVIDED = "Not provided";

  @Autowired
  public SubmissionDetailDisplayService(OrganisationUnitQueryService organisationUnitQueryService,
                                        LicenceQueryService licenceQueryService) {
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.licenceQueryService = licenceQueryService;
  }

  public String getTeamResponsibleDisplay(ResponsibleTeamType responsibleTeamType) {
    return responsibleTeamType.getDisplayName();
  }

  public String getOrganisationResponsibleDisplayById(Integer organisationResponsibleId) {
    if (organisationResponsibleId == null) {
      return NOT_PROVIDED;
    }

    var organisationUnitDto = organisationUnitQueryService.findOrganisationUnit(organisationResponsibleId)
        .orElse(null);

    return getOrganisationResponsibleDisplay(organisationUnitDto);
  }

  private String getOrganisationResponsibleDisplay(@Nullable OrganisationUnitDto organisationUnitDto) {
    return Optional.ofNullable(organisationUnitDto)
        .map(OrganisationUnitDto::organisationUnitName)
        .orElse(NOT_PROVIDED);
  }

  public String getOrganisationResponsibleDisplayFromMap(Map<Integer, OrganisationUnitDto> organisationUnitMap,
                                                         Integer organisationResponsibleId) {
    return Optional.ofNullable(organisationResponsibleId)
        .map(organisationUnitMap::get)
        .map(this::getOrganisationResponsibleDisplay)
        .orElse(NOT_PROVIDED);
  }

  public List<String> getLicencesForDisplay(List<Integer> licenceIds) {
    if (CollectionUtils.isEmpty(licenceIds)) {
      return List.of(NOT_PROVIDED);
    }

    var licences = licenceIds.stream()
        .map(Object::toString)
        .toList();

    return licenceQueryService.getLicencesByIds(licences).stream()
        .map(LicenceView::licenceRef)
        .toList();
  }

  public List<String> getLicencesForDisplayFromMap(Map<Integer, LicenceView> licencesMap, List<Integer> licencesTheResourceIsOn) {
    if (CollectionUtils.isEmpty(licencesTheResourceIsOn)) {
      return List.of(NOT_PROVIDED);
    }

    return licencesTheResourceIsOn.stream()
        .map(licencesMap::get)
        .filter(Objects::nonNull)
        .map(LicenceView::licenceRef)
        .toList();
  }

  public String getCentreOfInterestBlockDisplay(String centreOfInterestQuadBlock) {
    return Optional.ofNullable(centreOfInterestQuadBlock)
        .map(ofInterestQuadBlock -> " (" + ofInterestQuadBlock + ")")
        .orElse("");
  }

  public String getResourceNameAndCentreOfInterestBlockDisplay(String resourceName,
                                                               String centreOfInterestQuadBlock) {
    return resourceName + getCentreOfInterestBlockDisplay(centreOfInterestQuadBlock);
  }
}