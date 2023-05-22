package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;

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

  public String getOrganisationResponsibleDisplay(Integer organisationResponsibleId) {
    if (organisationResponsibleId == null) {
      return NOT_PROVIDED;
    }

    return organisationUnitQueryService.findOrganisationUnit(organisationResponsibleId)
        .flatMap(organisationUnit -> {
          if (organisationUnit.getRegisteredNumber() != null && organisationUnit.getName() != null) {
            return Optional.of(organisationUnit.getName() + " (" + organisationUnit.getRegisteredNumber() + ")");
          }

          return Optional.ofNullable(organisationUnit.getName());
        })
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