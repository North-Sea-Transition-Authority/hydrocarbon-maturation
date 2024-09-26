package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@ExtendWith(MockitoExtension.class)
class ResourceDetailsFormServiceTest {

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;

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