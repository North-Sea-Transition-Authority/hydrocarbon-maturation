package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery.UpdateDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.DiscoveryRelinquishmentForm;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.ProspectRelinquishmentForm;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRole;

@Service
public class SubmissionCreateService {

  private final ResourceService resourceService;
  private final SubmissionDetailService submissionDetailService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final ResourceLinkService resourceLinkService;
  private final SubmissionDetailDuplicationService submissionDetailDuplicationService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;
  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;
  private final SubmissionPersistenceService submissionPersistenceService;
  private final ReservoirPersistenceService reservoirPersistenceService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final TeamQueryService teamQueryService;

  @Autowired
  public SubmissionCreateService(ResourceService resourceService,
                                 SubmissionDetailService submissionDetailService,
                                 GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                 ResourceLinkService resourceLinkService,
                                 SubmissionDetailDuplicationService submissionDetailDuplicationService,
                                 RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                                 ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                 OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                 SubmissionDetailPersistenceService submissionDetailPersistenceService,
                                 SubmissionPersistenceService submissionPersistenceService,
                                 ReservoirPersistenceService reservoirPersistenceService,
                                 ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                                 TeamQueryService teamQueryService) {
    this.resourceService = resourceService;
    this.submissionDetailService = submissionDetailService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.resourceLinkService = resourceLinkService;
    this.submissionDetailDuplicationService = submissionDetailDuplicationService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.submissionPersistenceService = submissionPersistenceService;
    this.reservoirPersistenceService = reservoirPersistenceService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.teamQueryService = teamQueryService;
  }

  @Transactional
  public SubmissionDetail createInitialProspect(ResourceDetailsForm form, User user) {
    var resource = resourceService.createResource(ResourceType.PROSPECT);
    var submission = submissionPersistenceService.createSubmission(resource);
    regulatorManagedInformationPersistenceService.saveResponsibleTeamAndGeoByResource(resource, ResponsibleTeamType.EXPLORATION);
    var submissionDetail = submissionDetailPersistenceService
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_PROSPECT, user);

    resourceDetailsPersistenceService.createInitialResourceDetail(form, submissionDetail);

    // create single reservoir for prospect to fit the reservoir model for all resources
    // other resource types have screens to create reservoirs
    var resourceName = resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail).getResourceName();
    var reservoir = reservoirPersistenceService.createReservoir(resourceName);
    saveReservoirSubmissionDetails(reservoir, submissionDetail);

    return submissionDetail;
  }

  @Transactional
  public SubmissionDetail createUpdateProspect(UpdateProspectForm form, User user) {
    var oldSubmissionDetail =
        submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getProspectSelectedOption()));
    var resource = oldSubmissionDetail.getSubmission().getResource();
    var submission = submissionPersistenceService.createSubmission(resource);

    var newSubmissionDetail =
        submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.PROSPECT_UPDATE, user);

    createReservoirSubmissionDetailsWithTypeChecks(newSubmissionDetail, oldSubmissionDetail);

    submissionDetailDuplicationService.duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    var newGeneralDetail = generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(newSubmissionDetail)
        .orElse(new GeneralDetail());
    newGeneralDetail.setSubmissionDetail(newSubmissionDetail);
    generalDetailsPersistenceService.updateGeneralDetailProspect(form, newGeneralDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createPostDrillDryHoleSubmission(PostDrillDryHoleForm form, User user) {
    var oldSubmissionDetail =
        submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getProspectSelectedOption()));
    var resource = oldSubmissionDetail.getSubmission().getResource();
    var submission = submissionPersistenceService.createSubmission(resource);

    var newSubmissionDetail =
        submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.POST_DRILL_DRY_HOLE, user);

    createReservoirSubmissionDetailsWithTypeChecks(newSubmissionDetail, oldSubmissionDetail);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);
    opportunityDetailsPersistenceService.createOrUpdateOpportunityDetail(newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createDiscovery(ResourceDetailsForm form, User user) {
    var resource = resourceService.createResource(ResourceType.DISCOVERY);
    regulatorManagedInformationPersistenceService.saveResponsibleTeamAndGeoByResource(resource, ResponsibleTeamType.EXPLORATION);
    var submission = submissionPersistenceService.createSubmission(resource);

    var submissionDetail = submissionDetailPersistenceService
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_DISCOVERY, user);
    resourceDetailsPersistenceService.createInitialResourceDetail(form, submissionDetail);
    opportunityDetailsPersistenceService.createOrUpdateOpportunityDetail(submissionDetail);

    return submissionDetail;
  }

  @Transactional
  public SubmissionDetail createNewDiscoveryFromProspect(StartDiscoveryForm form, User user) {
    var discoveryResource = resourceService.createResource(ResourceType.DISCOVERY);
    regulatorManagedInformationPersistenceService.saveResponsibleTeamAndGeoByResource(
        discoveryResource, ResponsibleTeamType.EXPLORATION);
    var oldSubmissionDetail =
        submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getProspectSelectedOption()));
    var prospectResource = oldSubmissionDetail.getSubmission().getResource();
    var submission = submissionPersistenceService.createSubmission(discoveryResource);

    resourceLinkService.createNewProspectDiscoveryResourceLink(prospectResource, discoveryResource);

    var newSubmissionDetail = submissionDetailPersistenceService.createInitialSubmissionDetail(
        submission,
        SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
        user
    );

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);
    opportunityDetailsPersistenceService.createOrUpdateOpportunityDetail(newSubmissionDetail);
    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createUpdateDiscovery(UpdateDiscoveryForm form, User user) {
    var oldSubmissionDetail =
        submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getDiscoverySelectedOption()));
    var resource = oldSubmissionDetail.getSubmission().getResource();
    var submission = submissionPersistenceService.createSubmission(resource);
    var newSubmissionDetail =
        submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.UPDATE_DISCOVERY, user);

    createReservoirSubmissionDetailsWithTypeChecks(newSubmissionDetail, oldSubmissionDetail);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);
    opportunityDetailsPersistenceService.createOrUpdateOpportunityDetail(newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createProspectRelinquishment(ProspectRelinquishmentForm form, User user) {
    return createRelinquishment(form.getProspectSelectedOption(), SubmissionType.RELINQUISHMENT_PROSPECT, user);
  }

  @Transactional
  public SubmissionDetail createDiscoveryRelinquishment(DiscoveryRelinquishmentForm form, User user) {
    return createRelinquishment(form.getDiscoverySelectedOption(), SubmissionType.RELINQUISHMENT_DISCOVERY, user);
  }

  private SubmissionDetail createRelinquishment(String generalDetailId, SubmissionType relinquishmentType, User user) {
    var oldSubmissionDetail = submissionDetailService
        .getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(generalDetailId));
    var resource = oldSubmissionDetail.getSubmission().getResource();
    var submission = submissionPersistenceService.createSubmission(resource);
    var newSubmissionDetail = submissionDetailPersistenceService
        .createInitialSubmissionDetail(submission, relinquishmentType, user);

    createReservoirSubmissionDetailsWithTypeChecks(newSubmissionDetail, oldSubmissionDetail);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    generalDetailsPersistenceService.setRelinquishmentInformationSource(newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createUpdateSubmissionDetail(SubmissionDetail previousSubmissionDetail, User user) {
    var updateSubmissionDetail = submissionDetailPersistenceService.createUpdateSubmissionDetail(previousSubmissionDetail, user);

    createReservoirSubmissionDetailsWithTypeChecks(updateSubmissionDetail, previousSubmissionDetail);

    submissionDetailDuplicationService.duplicateSubmissionDetailSections(previousSubmissionDetail, updateSubmissionDetail);

    return updateSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createCorrectionSubmissionDetail(SubmissionDetail previousSubmissionDetail, User user) {
    var correctionSubmissionDetail = submissionDetailPersistenceService.createCorrectionSubmissionDetail(
        previousSubmissionDetail,
        user
    );

    createReservoirSubmissionDetails(correctionSubmissionDetail, previousSubmissionDetail);

    submissionDetailDuplicationService.duplicateSubmissionDetailSections(previousSubmissionDetail, correctionSubmissionDetail);

    return correctionSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createCsrFieldSubmissionDetail(Resource resource, User user) {
    var newSubmission = submissionPersistenceService.createSubmission(resource);

    var newSubmissionDetail = submissionDetailPersistenceService
        .createInitialSubmissionDetail(newSubmission, SubmissionType.CREATE_CSR, user);

    createReservoirDetailsAndDuplicate(resource, newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createFdpFieldSubmissionDetail(Resource resource, User user) {
    var submission = submissionPersistenceService.createSubmission(resource);

    var newSubmissionDetail = submissionDetailPersistenceService
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDP, user);

    createReservoirDetailsAndDuplicate(resource, newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createFdpaFieldSubmissionDetail(Resource resource, User user) {
    var submission = submissionPersistenceService.createSubmission(resource);

    var newSubmissionDetail = submissionDetailPersistenceService
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDPA, user);

    createReservoirDetailsAndDuplicate(resource, newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createCoPFieldSubmissionDetail(Resource resource, User user) {
    var submission = submissionPersistenceService.createSubmission(resource);

    var newSubmissionDetail = submissionDetailPersistenceService
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_COP, user);

    createReservoirDetailsAndDuplicate(resource, newSubmissionDetail);

    return newSubmissionDetail;
  }

  public Map<String, String> getResourceTypeOptionsForUser(User user) {
    var userRoles = teamQueryService.getTeamRolesForUser(user.wuaId())
        .stream()
        .map(TeamRole::getRole)
        .collect(Collectors.toSet());
    var resourceTypesForUser = SubmissionType.getResourceTypesForRoles(userRoles);

    return DisplayableEnumOptionUtil.getDisplayableOptions(resourceTypesForUser);
  }

  private void createReservoirSubmissionDetailsWithTypeChecks(SubmissionDetail newSubmissionDetail,
                                                              SubmissionDetail oldSubmissionDetail) {
    if (newSubmissionDetail.getSubmissionType() == SubmissionType.CREATE_DISCOVERY) {
      throw new ActionNotAllowedException(
          "Cannot create a reservoir submission detail for a create discovery or field submission");
    }

    createReservoirSubmissionDetails(newSubmissionDetail, oldSubmissionDetail);
  }

  private void createReservoirSubmissionDetails(SubmissionDetail newSubmissionDetail,
                                                SubmissionDetail oldSubmissionDetail) {
    reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail)
        .forEach(reservoirSubmissionDetail -> saveReservoirSubmissionDetails(
            reservoirSubmissionDetail.getReservoir(),
            newSubmissionDetail)
        );
  }

  private void saveReservoirSubmissionDetails(Reservoir reservoir, SubmissionDetail submissionDetail) {
    reservoirSubmissionDetailPersistenceService.saveReservoirSubmissionDetail(reservoir, submissionDetail);
  }

  private void createReservoirDetailsAndDuplicate(Resource resource, SubmissionDetail newSubmissionDetail) {
    var optionalOldSubmissionDetail = submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource);
    if (optionalOldSubmissionDetail.isPresent()) {
      var oldSubmissionDetail = optionalOldSubmissionDetail.get();

      createReservoirSubmissionDetailsWithTypeChecks(newSubmissionDetail, oldSubmissionDetail);

      submissionDetailDuplicationService
          .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);
    }
  }
}