package uk.co.nstauthority.hydrocarbonmaturation.authorisation.rules;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.ResourceAccessService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.operator.FieldOperatorQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;

@ExtendWith(MockitoExtension.class)
class OperatorResourceAccessInterceptorRuleTest {

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  @Mock
  private FieldPersistenceService fieldPersistenceService;
  @Mock
  private ResourceAccessService resourceAccessService;
  @Mock
  private FieldOperatorQueryService fieldOperatorQueryService;

  @InjectMocks
  private OperatorResourceAccessInterceptorRule operatorTeamMembershipInterceptorRule;

  @Test
  void hasRequiredTeamMembershipPermissions_whenIsIndustrySubmitterInResponsibleTeam_assertRulePass() {
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);

    var expectedOrganisationUnitId = 1;
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(expectedOrganisationUnitId);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
            .thenReturn(Optional.of(resourceDetail));
    when(resourceAccessService.userHasAccessToResourceForOrganisation(
        user,
        Set.of(Role.INDUSTRY_SUBMIT),
        expectedOrganisationUnitId
    )).thenReturn(true);
    var securityRuleResult = operatorTeamMembershipInterceptorRule
        .hasRequiredResourceAccessPermissions(
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isTrue();
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenIsIndustrySubmitterInResponsibleTeam_fieldSubmission_assertRulePass() {
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);

    var expectedOrganisationUnitId = 1;
    var organisationUnit = new OrganisationUnit();
    organisationUnit.setOrganisationUnitId(expectedOrganisationUnitId);
    var fieldId = 1;
    var field = new Field();
    field.setFieldIdentifier(fieldId);
    when(fieldPersistenceService.getFieldOrThrow(submissionDetail.getSubmission().getResource()))
            .thenReturn(field);
    when(fieldOperatorQueryService.getFieldOperatorByFieldIdentifier(fieldId))
        .thenReturn(Optional.of(organisationUnit));
    when(resourceAccessService.userHasAccessToResourceForOrganisation(
        user,
        Set.of(Role.INDUSTRY_SUBMIT),
        expectedOrganisationUnitId
    )).thenReturn(true);
    var securityRuleResult = operatorTeamMembershipInterceptorRule
        .hasRequiredResourceAccessPermissions(
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isTrue();
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenIsNotIndustrySubmitterInResponsibleTeam_assertRuleFail() {
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);

    var unexpectedOrganisationUnitId = 1;
    var resourceDetail = new ResourceDetail();
    resourceDetail.setOrganisationId(unexpectedOrganisationUnitId);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
            .thenReturn(Optional.of(resourceDetail));
    when(resourceAccessService.userHasAccessToResourceForOrganisation(
        user,
        Set.of(Role.INDUSTRY_SUBMIT),
        unexpectedOrganisationUnitId
    )).thenReturn(false);
    var securityRuleResult = operatorTeamMembershipInterceptorRule
        .hasRequiredResourceAccessPermissions(
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isFalse();
    assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void whenFieldHasNoResponsibleOrganisation_assertRulePass() {
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);

    var expectedOrganisationUnitId = 1;
    var organisationUnit = new OrganisationUnit();
    organisationUnit.setOrganisationUnitId(expectedOrganisationUnitId);
    var fieldId = 1;
    var field = new Field();
    field.setFieldIdentifier(fieldId);
    when(fieldPersistenceService.getFieldOrThrow(submissionDetail.getSubmission().getResource()))
        .thenReturn(field);
    when(fieldOperatorQueryService.getFieldOperatorByFieldIdentifier(fieldId))
        .thenReturn(Optional.empty());
    var securityRuleResult = operatorTeamMembershipInterceptorRule
        .hasRequiredResourceAccessPermissions(
            user,
            submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isFalse();
    assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void hasRequiredTeamMembershipPermissions_whenSubmissionIsNotIndustry_assertRuleFailForbidden() {
    var user = UserTestUtil.Builder().build();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmitterType(SubmitterType.REGULATOR);

    var securityRuleResult = operatorTeamMembershipInterceptorRule
        .hasRequiredResourceAccessPermissions(user, submissionDetail);
    assertThat(securityRuleResult.hasRulePassed()).isFalse();
    assertThat(securityRuleResult.failureStatus()).isEqualTo(HttpStatus.FORBIDDEN);
  }
}