package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class ResourceDetailsFormService {

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  @Autowired
  ResourceDetailsFormService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                             OrganisationUnitQueryService organisationUnitQueryService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  ResourceDetailsForm getResourceDetailsForm(SubmissionDetail submissionDetail) {
    return resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)
        .map(this::resourceDetailEntityToForm)
        .orElse(new ResourceDetailsForm());
  }

  public Map<String, String> getPreSelectedOrganisationUnit(String responsibleOrganisationId) {
    if (StringUtils.isBlank(responsibleOrganisationId)) {
      return Map.of();
    }
    return organisationUnitQueryService.getPreSelectedOrganisationUnit(Integer.valueOf(responsibleOrganisationId));
  }

  private ResourceDetailsForm resourceDetailEntityToForm(ResourceDetail resourceDetail) {
    var form = new ResourceDetailsForm();
    form.setResourceName(resourceDetail.getResourceName());
    form.setOnshoreOffshore(resourceDetail.getOnshoreOffshore());
    var organisationId = resourceDetail.getOrganisationId();

    if (!Objects.isNull(organisationId)) {
      form.setResponsibleOrganisationId(organisationId.toString());
    }

    return form;
  }
}
