package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery.UpdateDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.RelinquishmentForm;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Service
public class SubmissionCreateService {

  private final ResourceService resourceService;
  private final SubmissionDetailService submissionDetailService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final ResourceLinkService resourceLinkService;
  private final SubmissionDetailDuplicationService submissionDetailDuplicationService;
  private final ResponsibleTeamPersistenceService responsibleTeamPersistenceService;
  private final PermissionService permissionService;
  private final UserDetailService userDetailService;

  @Autowired
  public SubmissionCreateService(ResourceService resourceService,
                                 SubmissionDetailService submissionDetailService,
                                 GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                 ResourceLinkService resourceLinkService,
                                 SubmissionDetailDuplicationService submissionDetailDuplicationService,
                                 ResponsibleTeamPersistenceService responsibleTeamPersistenceService,
                                 PermissionService permissionService,
                                 UserDetailService userDetailService) {
    this.resourceService = resourceService;
    this.submissionDetailService = submissionDetailService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.resourceLinkService = resourceLinkService;
    this.submissionDetailDuplicationService = submissionDetailDuplicationService;
    this.responsibleTeamPersistenceService = responsibleTeamPersistenceService;
    this.permissionService = permissionService;
    this.userDetailService = userDetailService;
  }

  @Transactional
  public SubmissionDetail createInitialProspect() {
    var resource = resourceService.createResource(ResourceType.PROSPECT);
    responsibleTeamPersistenceService.saveResponsibleTeamForResource(resource, ResponsibleTeamType.EXPLORATION);
    return submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.CREATE_PROSPECT);
  }

  @Transactional
  public SubmissionDetail createUpdateProspect(UpdateProspectForm form) {
    var oldSubmissionDetail =
        submissionDetailService.getSubmissionDetailByGeneralDetailOrThrow(UUID.fromString(form.getProspectSelectedOption()));
    var resource = oldSubmissionDetail.getResourceId();

    var newSubmissionDetail =
        submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.PROSPECT_UPDATE);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    var newGeneralDetail = generalDetailsPersistenceService.getGeneralDetailOrThrow(newSubmissionDetail);
    generalDetailsPersistenceService.updateGeneralDetailProspect(form, newGeneralDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createPostDrillDryHoleSubmission(PostDrillDryHoleForm form) {
    var oldSubmissionDetail =
        submissionDetailService.getSubmissionDetailByGeneralDetailOrThrow(UUID.fromString(form.getProspectSelectedOption()));
    var resource = oldSubmissionDetail.getResourceId();

    var newSubmissionDetail =
        submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.POST_DRILL_DRY_HOLE);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createDiscovery() {
    var resource = resourceService.createResource(ResourceType.DISCOVERY);
    responsibleTeamPersistenceService.saveResponsibleTeamForResource(resource, ResponsibleTeamType.EXPLORATION);
    return submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.CREATE_DISCOVERY);
  }

  @Transactional
  public SubmissionDetail createNewDiscoveryFromProspect(StartDiscoveryForm form) {
    var discoveryResource = resourceService.createResource(ResourceType.DISCOVERY);
    responsibleTeamPersistenceService.saveResponsibleTeamForResource(discoveryResource, ResponsibleTeamType.EXPLORATION);
    var oldSubmissionDetail =
        submissionDetailService.getSubmissionDetailByGeneralDetailOrThrow(UUID.fromString(form.getProspectSelectedOption()));
    var prospectResource = oldSubmissionDetail.getResourceId();

    resourceLinkService.createNewResourceLink(prospectResource, discoveryResource);

    var newSubmissionDetail =
        submissionDetailService.createInitialSubmissionDetail(discoveryResource, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createUpdateDiscovery(UpdateDiscoveryForm form) {
    var oldSubmissionDetail =
        submissionDetailService.getSubmissionDetailByGeneralDetailOrThrow(UUID.fromString(form.getDiscoverySelectedOption()));
    var resource = oldSubmissionDetail.getResourceId();

    var newSubmissionDetail =
        submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.UPDATE_DISCOVERY);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createRelinquishment(RelinquishmentForm form) {
    String generalDetailId;
    SubmissionType relinquishmentType;
    switch (form.getChosenResourceType()) {
      case PROSPECT -> {
        generalDetailId = form.getProspectSelectedOption();
        relinquishmentType = SubmissionType.RELINQUISHMENT_PROSPECT;
      }
      case DISCOVERY -> {
        generalDetailId = form.getDiscoverySelectedOption();
        relinquishmentType = SubmissionType.RELINQUISHMENT_DISCOVERY;
      }
      default -> throw new ActionNotAllowedException("You must create a relinquishment off of a discovery or prospect.");
    }
    var oldSubmissionDetail = submissionDetailService
        .getSubmissionDetailByGeneralDetailOrThrow(UUID.fromString(generalDetailId));
    var resource = oldSubmissionDetail.getResourceId();

    var newSubmissionDetail = submissionDetailService
        .createInitialSubmissionDetail(resource, relinquishmentType);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    return newSubmissionDetail;
  }

  public Map<String, String> getSubmissionTypeOptionsForUser() {
    var userDetail = userDetailService.getUserDetail();
    var submissionTypeOptions = SubmissionType.getSubmissionTypes();

    //this ensures only one relinquishment option appears in the startSubmissionController
    submissionTypeOptions
        .remove(SubmissionType.RELINQUISHMENT_DISCOVERY.name());
    if (!permissionService.hasPermission(
        userDetail, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT))) {
      submissionTypeOptions
          .remove(SubmissionType.RELINQUISHMENT_PROSPECT.name());
    }
    return submissionTypeOptions;
  }
}
