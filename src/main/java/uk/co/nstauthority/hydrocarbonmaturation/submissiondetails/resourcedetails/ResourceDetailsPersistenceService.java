package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import java.time.LocalDate;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlockPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

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
  public void saveResourceDetails(ResourceDetailsForm form, SubmissionDetail submissionDetail, ResourceDetail resourceDetail) {
    var originalOnshoreOffshore = resourceDetail.getOnshoreOffshore();
    resourceDetail.setResourceName(form.getResourceName());
    resourceDetail.setOnshoreOffshore(form.getOnshoreOffshore());
    resourceDetail.setSubmissionDetail(submissionDetail);

    // Save the organisation unit
    var organisationId = form.getResponsibleOrganisationId();
    if (Objects.isNull(organisationId)) {
      resourceDetail.setOrganisationId(null);
      resourceDetail.setOrganisationName(null);
      resourceDetail.setOrganisationRegisteredNumber(null);
    } else {

      var organisationUnitDtoOptional = organisationUnitQueryService.findOrganisationUnit(Integer.valueOf(organisationId));
      if (organisationUnitDtoOptional.isPresent()) {
        var organisationUnit = organisationUnitDtoOptional.get();
        var organisationNameDate = submissionDetail.getAcceptedTimestamp() != null
            ? DateUtil.createLocalDate(submissionDetail.getAcceptedTimestamp())
            : LocalDate.now();
        var organisationUnitName = organisationUnitQueryService.getOrganisationUnitNameForDate(
            organisationUnit,
            organisationNameDate
        ).map(String::valueOf).orElse(null);

        resourceDetail.setOrganisationName(organisationUnitName);
        resourceDetail.setOrganisationId(organisationUnit.organisationUnitId());
        resourceDetail.setOrganisationRegisteredNumber(organisationUnit.companyHouseNumber());
      }
    }

    resourceDetailsRepository.save(resourceDetail);

    // If the resource onshore offshore has been updated we must remove all the current quad blocks
    // for the resource as they are specific to the old resource
    if (originalOnshoreOffshore != resourceDetail.getOnshoreOffshore()) {
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