package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import java.time.LocalDate;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@Service
public class OrganisationCorrectionService {

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  @Autowired
  public OrganisationCorrectionService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                       OrganisationUnitQueryService organisationUnitQueryService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  @Transactional
  public void updateOrganisationNameForDate(SubmissionDetail submissionDetail, LocalDate acceptedDate) {
    var resourceDetails = resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail);
    resourceDetails.ifPresent(resourceDetail -> {
      if (!Objects.isNull(resourceDetail.getOrganisationId())) {
        var organisationUnit = organisationUnitQueryService.findOrganisationUnit(resourceDetail.getOrganisationId());
        if (organisationUnit.isPresent()) {
          var organisationNameAtDate = organisationUnitQueryService.getOrganisationUnitNameForDate(
              organisationUnit.get(),
              acceptedDate
          );
          organisationNameAtDate.ifPresent(name -> {
            resourceDetail.setOrganisationName(name);
            resourceDetailsPersistenceService.saveResourceDetail(resourceDetail);
          });
        }
      }
    });
  }
}
