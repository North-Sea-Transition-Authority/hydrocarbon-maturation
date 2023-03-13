package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@ExtendWith(MockitoExtension.class)
class OperatorSubmissionSearchSelectorServiceTest {
  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();
  private static final int USER_ORG_UNIT = 1;

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private UserDetailService userDetailService;

  @Mock
  private IndustryTeamService industryTeamService;

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @InjectMocks
  private OperatorSubmissionSearchSelectorService operatorSubmissionSearchSelectorService;

  @BeforeEach
  void setUp() {
    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(industryTeamService
        .getOrganisationUnitIdsForUserWithIndustryTeamRoles(USER, Set.of(IndustryTeamRole.SUBMITTER)))
        .thenReturn(List.of(USER_ORG_UNIT));
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"RELINQUISHMENT_PROSPECT", "RELINQUISHMENT_DISCOVERY"},
      mode = EnumSource.Mode.EXCLUDE)
  void getResourceDetailsForSubmission_whenOneWithNotExtantLicence_whenNotRelinquishment_assertReturnOne(
      SubmissionType submissionType) {
    var resourceDetailWithUserOrgUnitAndExtantLicence = createResourceDetail(USER_ORG_UNIT, submissionType);
    var generalDetailWithExtantLicence = createGeneralDetail(
        resourceDetailWithUserOrgUnitAndExtantLicence.getSubmissionDetail());
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(
        resourceDetailWithUserOrgUnitAndExtantLicence.getSubmissionDetail()))
        .thenReturn(Optional.of(generalDetailWithExtantLicence));
    when(generalDetailsPersistenceService.isOnAnExtantLicence(generalDetailWithExtantLicence))
        .thenReturn(true);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetailWithUserOrgUnitAndExtantLicence.getSubmissionDetail()))
        .thenReturn(resourceDetailWithUserOrgUnitAndExtantLicence);

    var resourceDetailWithUserOrgUnitAndNotExtantLicence =
        createResourceDetail(USER_ORG_UNIT, submissionType);
    var generalDetailWithoutExtantLicence = createGeneralDetail(
        resourceDetailWithUserOrgUnitAndNotExtantLicence.getSubmissionDetail());
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(
        resourceDetailWithUserOrgUnitAndNotExtantLicence.getSubmissionDetail()))
        .thenReturn(Optional.of(generalDetailWithoutExtantLicence));
    when(generalDetailsPersistenceService.isOnAnExtantLicence(generalDetailWithoutExtantLicence))
        .thenReturn(false);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(
        resourceDetailWithUserOrgUnitAndNotExtantLicence.getSubmissionDetail()))
        .thenReturn(resourceDetailWithUserOrgUnitAndNotExtantLicence);

    assertThat(operatorSubmissionSearchSelectorService.getResourceDetailsForSubmissions(
        Stream.of(
            resourceDetailWithUserOrgUnitAndExtantLicence.getSubmissionDetail(),
            resourceDetailWithUserOrgUnitAndNotExtantLicence.getSubmissionDetail()),
        submissionType)
        .toList())
        .containsExactly(resourceDetailWithUserOrgUnitAndExtantLicence);
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void getResourceDetailsForSubmission_whenOneWithNotResponsibleOrg_assertReturnOne(
      SubmissionType submissionType) {
    var resourceDetailWithUserOrgUnitAndExtantLicence = createResourceDetail(USER_ORG_UNIT, submissionType);
    var generalDetailWithExtantLicence = createGeneralDetail(
        resourceDetailWithUserOrgUnitAndExtantLicence.getSubmissionDetail());

    if (submissionType != SubmissionType.RELINQUISHMENT_DISCOVERY
        && submissionType != SubmissionType.RELINQUISHMENT_PROSPECT) {
      when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(
          resourceDetailWithUserOrgUnitAndExtantLicence.getSubmissionDetail()))
          .thenReturn(Optional.of(generalDetailWithExtantLicence));
      when(generalDetailsPersistenceService.isOnAnExtantLicence(generalDetailWithExtantLicence))
          .thenReturn(true);
    }

    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetailWithUserOrgUnitAndExtantLicence.getSubmissionDetail()))
        .thenReturn(resourceDetailWithUserOrgUnitAndExtantLicence);

    var nonUserResponsibleOrg = 3;
    var resourceDetailWithUserOrgUnitAndNotExtantLicence =
        createResourceDetail(nonUserResponsibleOrg, submissionType);

    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(
        resourceDetailWithUserOrgUnitAndNotExtantLicence.getSubmissionDetail()))
        .thenReturn(resourceDetailWithUserOrgUnitAndNotExtantLicence);

    assertThat(operatorSubmissionSearchSelectorService.getResourceDetailsForSubmissions(
            Stream.of(
                resourceDetailWithUserOrgUnitAndExtantLicence.getSubmissionDetail(),
                resourceDetailWithUserOrgUnitAndNotExtantLicence.getSubmissionDetail()),
            submissionType)
        .toList())
        .containsExactly(resourceDetailWithUserOrgUnitAndExtantLicence);
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void getResourceDetailsForSubmission_whenNoResponsibleOrg_assertEmpty(
      SubmissionType submissionType) {
    var resourceDetailWithNullResponsibleOrg =
        createResourceDetail(null, submissionType);

    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(resourceDetailWithNullResponsibleOrg.getSubmissionDetail()))
        .thenReturn(resourceDetailWithNullResponsibleOrg);

    assertThat(operatorSubmissionSearchSelectorService.getResourceDetailsForSubmissions(
            Stream.of(
                resourceDetailWithNullResponsibleOrg.getSubmissionDetail()),
            submissionType))
        .isEmpty();
  }

  private GeneralDetail createGeneralDetail(SubmissionDetail submissionDetail) {
    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);
    return generalDetail;
  }

  private ResourceDetail createResourceDetail(Integer responsibleOrganisationId, SubmissionType submissionType) {
    var resourceDetail = new ResourceDetail();
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    resourceDetail.setSubmissionDetail(submissionDetail);
    resourceDetail.setOrganisationId(responsibleOrganisationId);
    return resourceDetail;
  }
}