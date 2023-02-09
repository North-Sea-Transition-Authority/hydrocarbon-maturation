package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ResourceDetailsFormServiceTest {

  @Mock
  private ResourceDetailsFormValidator resourceDetailsFormValidator;

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private RegulatorTeamService regulatorTeamService;

  @Mock
  private UserDetailService userDetailService;

  @InjectMocks
  private ResourceDetailsFormService resourceDetailsFormService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.PROSPECT_UPDATE);
  }

  @Test
  void resourceDetailEntityToForm_whenEntityPresent_assertPopulatedForm() {
    var resourceDetail = createResourceDetail();
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));

    assertThat(resourceDetailsFormService.getResourceDetailsForm(submissionDetail))
        .extracting(
            ResourceDetailsForm::getResourceName,
            ResourceDetailsForm::getResponsibleOrganisationId,
            ResourceDetailsForm::getOnshoreOffshore
        )
        .containsExactly(
            resourceDetail.getResourceName(),
            resourceDetail.getOrganisationId().toString(),
            resourceDetail.getOnshoreOffshore()
        );
  }

  @Test
  void resourceDetailEntityToForm_newEntity_thenEmptyForm() {
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var form = resourceDetailsFormService.getResourceDetailsForm(submissionDetail);
    assertThat(form).hasAllNullFieldsOrProperties();
  }

  @Test
  void validate_whenCreateProspect_whenRegulator_verifyMethodCall() {
    var form = new ResourceDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var validationType = ValidationType.FULL;
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    when(regulatorTeamService.isRegulatorUser(any()))
        .thenReturn(true);
    var captor = ArgumentCaptor.forClass(
        ResourceDetailsFormValidatorHints.class);
    resourceDetailsFormService.validate(form, bindingResult, validationType, submissionDetail);

    verify(resourceDetailsFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());

    assertThat(captor.getValue())
        .extracting(
            ResourceDetailsFormValidatorHints::validationType,
            ResourceDetailsFormValidatorHints::resourceType,
            ResourceDetailsFormValidatorHints::isRegulator,
            ResourceDetailsFormValidatorHints::latestApprovedResourceDetail)
        .containsExactly(
            validationType,
            submissionDetail.getResourceId().getType(),
            true,
            null
        );
  }

  @Test
  void validate_whenHasApprovedSubmission_whenNotRegulator_verifyMethodCall() {
    var form = new ResourceDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var validationType = ValidationType.FULL;
    var approvedResourceDetail = new ResourceDetail();
    var approvedSubmissionDetail = new SubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    when(regulatorTeamService.isRegulatorUser(any()))
        .thenReturn(false);
    when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
        .thenReturn(Optional.of(approvedSubmissionDetail));
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(approvedSubmissionDetail))
        .thenReturn(Optional.of(approvedResourceDetail));
    var captor = ArgumentCaptor.forClass(
        ResourceDetailsFormValidatorHints.class);
    resourceDetailsFormService.validate(form, bindingResult, validationType, submissionDetail);

    verify(resourceDetailsFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());

    assertThat(captor.getValue())
        .extracting(
            ResourceDetailsFormValidatorHints::validationType,
            ResourceDetailsFormValidatorHints::resourceType,
            ResourceDetailsFormValidatorHints::isRegulator,
            ResourceDetailsFormValidatorHints::latestApprovedResourceDetail)
        .containsExactly(
            validationType,
            submissionDetail.getResourceId().getType(),
            false,
            approvedResourceDetail
        );
  }

  @Test
  void getPreSelectedOrganisationUnit_assertReturn() {
    var expectedMap = Map.of("1", "1");
    when(organisationUnitQueryService.getPreSelectedOrganisationUnit(1))
        .thenReturn(expectedMap);
    var result = resourceDetailsFormService
        .getPreSelectedOrganisationUnit("1");
    assertThat(result).isEqualTo(expectedMap);
  }

  private ResourceDetail createResourceDetail() {
    var resourceDetail = new ResourceDetail();
    resourceDetail.setResourceName("name");
    resourceDetail.setOrganisationId(1);
    resourceDetail.setOnshoreOffshore(OnshoreOffshore.ONSHORE);
    resourceDetail.setOrganisationName("Shell");
    resourceDetail.setOrganisationRegisteredNumber("22");
    return resourceDetail;
  }
}