package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery.UpdateDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.DiscoveryRelinquishmentForm;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.ProspectRelinquishmentForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

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
  private final UserDetailService userDetailService;
  private final SubmissionDetailPersistenceService submissionDetailPersistenceService;
  private final SubmissionPersistenceService submissionPersistenceService;
  private final RegulatorTeamService regulatorTeamService;
  private final AreaTeamService areaTeamService;
  private final IndustryTeamService industryTeamService;

  @Autowired
  public SubmissionCreateService(ResourceService resourceService,
                                 SubmissionDetailService submissionDetailService,
                                 GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                 ResourceLinkService resourceLinkService,
                                 SubmissionDetailDuplicationService submissionDetailDuplicationService,
                                 RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                                 ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                 OpportunityDetailsPersistenceService opportunityDetailsPersistenceService,
                                 UserDetailService userDetailService,
                                 SubmissionDetailPersistenceService submissionDetailPersistenceService,
                                 SubmissionPersistenceService submissionPersistenceService,
                                 RegulatorTeamService regulatorTeamService, AreaTeamService areaTeamService,
                                 IndustryTeamService industryTeamService) {
    this.resourceService = resourceService;
    this.submissionDetailService = submissionDetailService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.resourceLinkService = resourceLinkService;
    this.submissionDetailDuplicationService = submissionDetailDuplicationService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
    this.userDetailService = userDetailService;
    this.submissionDetailPersistenceService = submissionDetailPersistenceService;
    this.submissionPersistenceService = submissionPersistenceService;
    this.regulatorTeamService = regulatorTeamService;
    this.areaTeamService = areaTeamService;
    this.industryTeamService = industryTeamService;
  }

  @Transactional
  public SubmissionDetail createInitialProspect(ResourceDetailsForm form) {
    var resource = resourceService.createResource(ResourceType.PROSPECT);
    var submission = submissionPersistenceService.createSubmission(resource);
    regulatorManagedInformationPersistenceService.saveResponsibleTeamAndGeoByResource(resource, ResponsibleTeamType.EXPLORATION);
    var submissionDetail = submissionDetailPersistenceService
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_PROSPECT);
    resourceDetailsPersistenceService.createInitialResourceDetail(form, submissionDetail);
    return submissionDetail;
  }

  @Transactional
  public SubmissionDetail createUpdateProspect(UpdateProspectForm form) {
    var oldSubmissionDetail =
        submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getProspectSelectedOption()));
    var resource = oldSubmissionDetail.getSubmission().getResource();
    var submission = submissionPersistenceService.createSubmission(resource);

    var newSubmissionDetail =
        submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.PROSPECT_UPDATE);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    var newGeneralDetail = generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(newSubmissionDetail)
        .orElse(new GeneralDetail());
    newGeneralDetail.setSubmissionDetail(newSubmissionDetail);
    generalDetailsPersistenceService.updateGeneralDetailProspect(form, newGeneralDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createPostDrillDryHoleSubmission(PostDrillDryHoleForm form) {
    var oldSubmissionDetail =
        submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getProspectSelectedOption()));
    var resource = oldSubmissionDetail.getSubmission().getResource();
    var submission = submissionPersistenceService.createSubmission(resource);

    var newSubmissionDetail =
        submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.POST_DRILL_DRY_HOLE);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);
    opportunityDetailsPersistenceService.createOrUpdateOpportunityDetail(newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createDiscovery(ResourceDetailsForm form) {
    var resource = resourceService.createResource(ResourceType.DISCOVERY);
    regulatorManagedInformationPersistenceService.saveResponsibleTeamAndGeoByResource(resource, ResponsibleTeamType.EXPLORATION);
    var submission = submissionPersistenceService.createSubmission(resource);

    var submissionDetail = submissionDetailPersistenceService
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_DISCOVERY);
    resourceDetailsPersistenceService.createInitialResourceDetail(form, submissionDetail);
    opportunityDetailsPersistenceService.createOrUpdateOpportunityDetail(submissionDetail);
    return submissionDetail;
  }

  @Transactional
  public SubmissionDetail createNewDiscoveryFromProspect(StartDiscoveryForm form) {
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
        SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT
    );

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);
    opportunityDetailsPersistenceService.createOrUpdateOpportunityDetail(newSubmissionDetail);
    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createUpdateDiscovery(UpdateDiscoveryForm form) {
    var oldSubmissionDetail =
        submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getDiscoverySelectedOption()));
    var resource = oldSubmissionDetail.getSubmission().getResource();
    var submission = submissionPersistenceService.createSubmission(resource);
    var newSubmissionDetail =
        submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.UPDATE_DISCOVERY);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);
    opportunityDetailsPersistenceService.createOrUpdateOpportunityDetail(newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createProspectRelinquishment(ProspectRelinquishmentForm form) {
    return createRelinquishment(form.getProspectSelectedOption(), SubmissionType.RELINQUISHMENT_PROSPECT);
  }

  @Transactional
  public SubmissionDetail createDiscoveryRelinquishment(DiscoveryRelinquishmentForm form) {
    return createRelinquishment(form.getDiscoverySelectedOption(), SubmissionType.RELINQUISHMENT_DISCOVERY);
  }

  private SubmissionDetail createRelinquishment(String generalDetailId, SubmissionType relinquishmentType) {
    var oldSubmissionDetail = submissionDetailService
        .getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(generalDetailId));
    var resource = oldSubmissionDetail.getSubmission().getResource();
    var submission = submissionPersistenceService.createSubmission(resource);
    var newSubmissionDetail = submissionDetailPersistenceService
        .createInitialSubmissionDetail(submission, relinquishmentType);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);

    generalDetailsPersistenceService.setRelinquishmentInformationSource(newSubmissionDetail);

    return newSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createUpdateSubmissionDetail(SubmissionDetail parentSubmissionDetail) {
    var updateSubmissionDetail = submissionDetailPersistenceService
        .createUpdateSubmissionDetail(parentSubmissionDetail);

    submissionDetailDuplicationService
        .duplicateSubmissionDetailSections(parentSubmissionDetail, updateSubmissionDetail);

    return updateSubmissionDetail;
  }

  @Transactional
  public SubmissionDetail createCsrFieldSubmissionDetail(Resource resource) {
    var submission = submissionPersistenceService.createSubmission(resource);

    return submissionDetailPersistenceService
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_CSR);
  }

  @Transactional
  public SubmissionDetail createFdpFieldSubmissionDetail(Resource resource) {
    var submission = submissionPersistenceService.createSubmission(resource);

    return submissionDetailPersistenceService
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDP);
  }

  @Transactional
  public SubmissionDetail createFdpaFieldSubmissionDetail(Resource resource) {
    var submission = submissionPersistenceService.createSubmission(resource);

    return submissionDetailPersistenceService
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDPA);
  }

  @Transactional
  public SubmissionDetail createCoPFieldSubmissionDetail(Resource resource) {
    var submission = submissionPersistenceService.createSubmission(resource);

    return submissionDetailPersistenceService
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_COP);
  }

  public Map<String, String> getResourceTypeOptionsForUser() {
    var userDetail = userDetailService.getUserDetail();
    var resourceTypeOptions = ResourceType.getResourceTypes();

    var isNotIndustrySubmitter = !industryTeamService.isSubmitter(userDetail);
    var isNotExplorationTeam = !regulatorTeamService.isExploration(userDetail);
    var isNotAreaTeamMember = !areaTeamService.isAreaTeamMember(userDetail);

    if (isNotIndustrySubmitter && isNotExplorationTeam) {
      resourceTypeOptions.remove(ResourceType.PROSPECT.name());
    }

    if (isNotIndustrySubmitter && isNotExplorationTeam && isNotAreaTeamMember) {
      resourceTypeOptions.remove(ResourceType.DISCOVERY.name());
    }

    if (isNotIndustrySubmitter && isNotAreaTeamMember) {
      resourceTypeOptions.remove(ResourceType.FIELD.name());
    }

    return resourceTypeOptions;
  }
}