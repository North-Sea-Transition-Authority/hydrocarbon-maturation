package uk.co.nstauthority.hydrocarbonmaturation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByRegulatorUsersInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermissionInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.OperatorResourceAccessInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.RegulatorResourceAccessInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.RegulatorResponsibleTeamInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.ResourceAccessInterceptorFactory;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.ResourceAccessInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionDetailVersionInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionStatusInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules.SubmissionTypeInterceptorRule;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.WebMvcConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.PermissionManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryPermissionManagementHandlerInterceptor;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorPermissionManagementHandlerInterceptor;

@Import(value = {
    WebMvcConfiguration.class,
    PermissionManagementHandlerInterceptor.class,
    RegulatorPermissionManagementHandlerInterceptor.class,
    IndustryPermissionManagementHandlerInterceptor.class,
    AccessibleByRegulatorUsersInterceptor.class,
    HasPermissionInterceptor.class,
    SubmissionStatusInterceptorRule.class,
    RegulatorResponsibleTeamInterceptorRule.class,
    ResourceAccessInterceptorRule.class,
    ResourceAccessInterceptorFactory.class,
    SubmissionDetailVersionInterceptorRule.class,
    OperatorResourceAccessInterceptorRule.class,
    RegulatorResourceAccessInterceptorRule.class,
    SubmissionTypeInterceptorRule.class,
    PermissionService.class})
public abstract class AbstractSubmissionControllerTest extends AbstractControllerTest{

  @MockBean
  protected ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @MockBean
  protected RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @MockBean
  protected OrganisationUnitQueryService organisationUnitQueryService;

  @MockBean
  protected IndustryTeamService industryTeamService;

  @MockBean
  protected PermissionService permissionService;

  @MockBean
  protected FieldPersistenceService fieldPersistenceService;

  @Autowired
  protected OperatorResourceAccessInterceptorRule operatorTeamMembershipInterceptorRule;

  @Autowired
  protected SubmissionDetailVersionInterceptorRule submissionDetailVersionInterceptorRule;

  @Autowired
  protected RegulatorResourceAccessInterceptorRule regulatorTeamMembershipInterceptorRule;

  @Autowired
  protected ResourceAccessInterceptorRule resourceAccessInterceptorRule;

  @MockBean
  protected ResourceAccessInterceptorFactory resourceAccessInterceptorFactory;

  @Autowired
  protected ControllerHelperService controllerHelperService;

  protected static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  protected static final UUID SUBMISSION_DETAIL_ID = UUID.randomUUID();
  protected SubmissionDetail submissionDetail;

  protected ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp(TestInfo testInfo) {
    doCallRealMethod().when(userDetailService).getUserDetail();
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT,
        SUBMISSION_DETAIL_ID);
    submissionDetail.setApprovedTimestamp(Instant.now());
    when(submissionDetailService.getSubmissionDetail(SUBMISSION_DETAIL_ID)).thenReturn(Optional.of(submissionDetail));
    when(submissionDetailService.getSubmissionDetailForIdOrError(UUID.fromString(submissionDetail.getId().toString())))
        .thenReturn(submissionDetail);
    submissionDetail.setId(SUBMISSION_DETAIL_ID);

    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

    var securityTestAnnotation = testInfo
        .getTestMethod().map(method -> method.getAnnotation(SecurityTest.class));
    if (securityTestAnnotation.isEmpty()) {
      //if the test doesn't have a @SecurityTest annotation, ensure the security passes
      var responsibleTeam = new RegulatorManagedInformation();
      when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy())
          .thenReturn(regulatorTeamMembershipInterceptorRule);
      responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(any()))
          .thenReturn(responsibleTeam);
      when(permissionService.hasPermission(any(), any()))
          .thenReturn(true);
    }
  }

  protected void setUpWhenUserIsResponsibleOrganisation_passSecurity(ServiceUserDetail user) {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy())
        .thenReturn(operatorTeamMembershipInterceptorRule);
    var responsibleTeam = new RegulatorManagedInformation();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(submissionDetail.getSubmission().getResource());
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(responsibleTeam);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    when(industryTeamService.getOrganisationUnitIdsForUserWithIndustryTeamRoles(
        user, Set.of(IndustryTeamRole.SUBMITTER)))
        .thenReturn(List.of(1));
  }

  protected void setUpWhenUserIsResponsibleOrganisation_passSecurity(ServiceUserDetail user, SubmissionDetail submissionDetail) {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy())
        .thenReturn(operatorTeamMembershipInterceptorRule);
    var responsibleTeam = new RegulatorManagedInformation();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(submissionDetail.getSubmission().getResource());
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(responsibleTeam);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    when(industryTeamService.getOrganisationUnitIdsForUserWithIndustryTeamRoles(
        user, Set.of(IndustryTeamRole.SUBMITTER)))
        .thenReturn(List.of(1));
  }

  protected void setUpWhenUserIsResponsibleTeam_passSecurity() {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy())
        .thenReturn(regulatorTeamMembershipInterceptorRule);
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    regulatorManagedInformation.setResource(submissionDetail.getSubmission().getResource());

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(regulatorManagedInformation);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);
  }

  protected void setUpWhenUserIsResponsibleTeam_forField_passSecurity(ServiceUserDetail user, SubmissionDetail submissionDetail) {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy())
        .thenReturn(regulatorTeamMembershipInterceptorRule);
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
    regulatorManagedInformation.setResource(submissionDetail.getSubmission().getResource());

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(regulatorManagedInformation);
    when(permissionService.hasPermission(user, Set.of(RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
  }

  protected void setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(ServiceUserDetail user) {
    when(resourceAccessInterceptorFactory.getResourceAccessInterceptorStrategy())
        .thenReturn(operatorTeamMembershipInterceptorRule);
    var responsibleTeam = new RegulatorManagedInformation();
    responsibleTeam.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    responsibleTeam.setResource(submissionDetail.getSubmission().getResource());
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(
        submissionDetail.getSubmission().getResource()))
        .thenReturn(responsibleTeam);
    when(permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(false);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(1);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(resourceDetail.getOrganisationId()))
        .thenReturn(List.of(1));
    when(industryTeamService.isSubmitterForOrganisationGroups(user, List.of(1)))
        .thenReturn(false);
  }

  protected void setUpWhenIsLatestExtantSubmissionVersion_passSecurity(SubmissionDetail submissionDetail) {
    if (submissionDetail.getVersionNumber() > 1 || submissionDetail.getStatus() != SubmissionDetailStatus.DRAFT) {
      switch (submissionDetail.getStatus()) {
        case APPROVED -> when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
            submissionDetail,
            Set.of(SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED)))
            .thenReturn(true);
        case DRAFT -> when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
            submissionDetail,
            Set.of(SubmissionDetailStatus.DRAFT, SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED)))
            .thenReturn(true);
        case SUBMITTED -> when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
            submissionDetail,
            Set.of(SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.APPROVED))).thenReturn(true);
        default -> {
        }
      }
    }
  }
}
