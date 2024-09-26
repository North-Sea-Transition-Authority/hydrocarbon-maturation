package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlockPersistenceService;

@Service
public class ResourceDetailsPersistenceService {

  private final ResourceDetailsRepository resourceDetailsRepository;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;

  @Autowired
  ResourceDetailsPersistenceService(ResourceDetailsRepository resourceDetailsRepository,
                                    OrganisationUnitQueryService organisationUnitQueryService,
                                    GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                    QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService) {
    this.resourceDetailsRepository = resourceDetailsRepository;
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
  }

  @Transactional
  public void saveResourceDetails(ResourceDetailsForm form, SubmissionDetail submissionDetail) {
    var resourceDetails = getResourceDetailBySubmissionDetail(submissionDetail).orElse(new ResourceDetail());
    var originalOnshoreOffshore = resourceDetails.getOnshoreOffshore();
    resourceDetails.setResourceName(form.getResourceName());
    resourceDetails.setOnshoreOffshore(form.getOnshoreOffshore());
    resourceDetails.setSubmissionDetail(submissionDetail);

    // Save the organisation unit
    // Corrections don't allow the organisation to be changed and is set at the time of starting the correction
    if (BooleanUtils.isNotTrue(submissionDetail.isCorrection())) {
      var organisationId = form.getResponsibleOrganisationId();
      if (Objects.isNull(organisationId)) {
        resourceDetails.setOrganisationId(null);
        resourceDetails.setOrganisationName(null);
        resourceDetails.setOrganisationRegisteredNumber(null);
      } else {
        var organisationUnit = organisationUnitQueryService.findOrganisationUnit(Integer.valueOf(organisationId));
        organisationUnit.ifPresent(unit -> {
          resourceDetails.setOrganisationName(unit.organisationUnitName());
          resourceDetails.setOrganisationId(unit.organisationUnitId());
          resourceDetails.setOrganisationRegisteredNumber(unit.companyHouseNumber());
        });
      }
    }
    resourceDetailsRepository.save(resourceDetails);

    // If the resource onshore offshore has been updated we must remove all the current quad blocks
    // for the resource as they are specific to the old resource
    if (originalOnshoreOffshore != resourceDetails.getOnshoreOffshore()) {
      generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
          .ifPresent(quadrantAndBlockPersistenceService::removeCurrentQuadsAndBlocks);
    }
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
        resourceDetail.setOrganisationName(unit.organisationUnitName());
        resourceDetail.setOrganisationId(unit.organisationUnitId());
        resourceDetail.setOrganisationRegisteredNumber(unit.companyHouseNumber());
      });
    }

    resourceDetailsRepository.save(resourceDetail);
  }

  @Transactional
  public void saveResourceDetail(ResourceDetail resourceDetail) {
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

  @Transactional
  public void updateOrganisationName(SubmissionDetail submissionDetail) {
    getResourceDetailBySubmissionDetail(submissionDetail)
        .ifPresent(resourceDetail -> {
          if (resourceDetail.getOrganisationId() != null) {
            organisationUnitQueryService.findOrganisationUnit(resourceDetail.getOrganisationId())
                .ifPresent(organisationUnit -> {
                  resourceDetail.setOrganisationName(organisationUnit.organisationUnitName());
                  resourceDetail.setOrganisationRegisteredNumber(organisationUnit.companyHouseNumber());
                  resourceDetailsRepository.save(resourceDetail);
                });
          }
        });
  }

  public String getOrganisationDisplayFromResourceDetails(ResourceDetail detail) {
    var organisationName = detail.getOrganisationName();
    var organisationRegisteredNumber = detail.getOrganisationRegisteredNumber();
    var organisationDisplay = "";
    if (organisationName != null) {
      if (organisationRegisteredNumber != null) {
        organisationDisplay = "%s (%s)".formatted(organisationName,
            organisationRegisteredNumber);
      } else {
        organisationDisplay = organisationName;
      }
    }
    return organisationDisplay;
  }

  public String getOrganisationNameFromPortal(ResourceDetail detail) {
    return organisationUnitQueryService.getOrganisationUnitNameAndNumberByIdOrEmpty(detail.getOrganisationId());
  }
}