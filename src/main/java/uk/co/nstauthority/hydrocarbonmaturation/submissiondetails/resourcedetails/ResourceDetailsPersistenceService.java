package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class ResourceDetailsPersistenceService {

  private final ResourceDetailsRepository resourceDetailsRepository;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  @Autowired
  ResourceDetailsPersistenceService(ResourceDetailsRepository resourceDetailsRepository,
                                    OrganisationUnitQueryService organisationUnitQueryService) {
    this.resourceDetailsRepository = resourceDetailsRepository;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  @Transactional
  public void saveResourceDetails(ResourceDetailsForm form, SubmissionDetail submissionDetail) {
    var resourceDetails = getResourceDetailBySubmissionDetail(submissionDetail).orElse(new ResourceDetail());
    resourceDetails.setResourceName(form.getResourceName());
    resourceDetails.setOnshoreOffshore(form.getOnshoreOffshore());
    resourceDetails.setSubmissionDetail(submissionDetail);
    var organisationId = form.getResponsibleOrganisationId();
    if (Objects.isNull(organisationId)) {
      resourceDetails.setOrganisationId(null);
      resourceDetails.setOrganisationName(null);
      resourceDetails.setOrganisationRegisteredNumber(null);
    } else {
      var organisationUnit = organisationUnitQueryService.findOrganisationUnit(Integer.valueOf(organisationId));
      organisationUnit.ifPresent(unit -> {
        resourceDetails.setOrganisationName(unit.getName());
        resourceDetails.setOrganisationId(unit.getOrganisationUnitId());
        resourceDetails.setOrganisationRegisteredNumber(unit.getRegisteredNumber());
      });
    }
    resourceDetailsRepository.save(resourceDetails);
  }

  @Transactional
  public void createInitialResourceDetail(ResourceDetailsForm form,
                                          SubmissionDetail submissionDetail) {
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setResourceName(form.getResourceName());
    resourceDetail.setOnshoreOffshore(form.getOnshoreOffshore());
    if (form.getResponsibleOrganisationId() != null) {
      var organisationUnit = organisationUnitQueryService.findOrganisationUnit(
          Integer.valueOf(form.getResponsibleOrganisationId()));
      organisationUnit.ifPresent(unit -> {
        resourceDetail.setOrganisationName(unit.getName());
        resourceDetail.setOrganisationId(unit.getOrganisationUnitId());
        resourceDetail.setOrganisationRegisteredNumber(unit.getRegisteredNumber());
      });
    }

    resourceDetailsRepository.save(resourceDetail);
  }

  public Optional<ResourceDetail> getResourceDetailBySubmissionDetail(SubmissionDetail submissionDetail) {
    return resourceDetailsRepository.findBySubmissionDetail(submissionDetail);
  }

  public ResourceDetail getResourceDetailOrThrow(SubmissionDetail submissionDetail) {
    return getResourceDetailBySubmissionDetail(submissionDetail)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            String.format("Could not find resource detail for submission detail %s", submissionDetail.getId())));
  }

  public ResourceDetail getResourceDetailByIdOrThrow(UUID resourceDetailId) {
    return resourceDetailsRepository.findById(resourceDetailId)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            String.format("Could not find resource detail by ID %s", resourceDetailId)));
  }

  public List<ResourceDetail> getResourceDetailsByName(String name) {
    return resourceDetailsRepository.findByResourceNameContainingIgnoreCase(name);
  }
}
