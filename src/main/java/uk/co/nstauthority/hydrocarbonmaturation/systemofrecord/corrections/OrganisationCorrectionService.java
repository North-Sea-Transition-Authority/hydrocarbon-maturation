package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationNameHistoryDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

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
    var resourceDetailsOpt = resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail);

    resourceDetailsOpt.ifPresent(resourceDetail -> {
      var organisationNameAtDate = getOrganisationUnitNameForDate(resourceDetail, acceptedDate);
      organisationNameAtDate.ifPresent(name -> {
        resourceDetail.setOrganisationName(name);
        resourceDetailsPersistenceService.saveResourceDetail(resourceDetail);
      });
    });
  }

  public Optional<String> getOrganisationUnitNameForDate(ResourceDetail resourceDetail, LocalDate acceptedDate) {
    var organisationUnitId = resourceDetail.getOrganisationId();
    if (!Objects.isNull(organisationUnitId)) {
      var organisationUnit = organisationUnitQueryService.findOrganisationUnit(organisationUnitId);
      if (organisationUnit.isPresent()) {
        var nameHistory =
            DateUtil.findLatestItemForGivenStartDate(
                organisationUnit.get().organisationNameHistoryDtoList(),
                acceptedDate.atStartOfDay(),
                organisationNameHistoryDto -> organisationNameHistoryDto.startDate().atStartOfDay()
            );

        return nameHistory.map(OrganisationNameHistoryDto::name);
      }
    }
    return Optional.empty();
  }
}
