package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class RegulatorSubmissionSearchSelectorServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  private ResponsibleTeamService responsibleTeamService;

  @InjectMocks
  private RegulatorSubmissionSearchSelectorService regulatorSubmissionSearchSelectorService;

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void getResourceDetailsForSubmissions_whenInResponsibleTeam_assertReturn(SubmissionType submissionType) {

    var submissionDetail1 = new SubmissionDetail();
    var submissionDetail2 = new SubmissionDetail();
    submissionDetail1.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    submissionDetail2.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    var resourceDetail1 = new ResourceDetail();
    var resourceDetail2 = new ResourceDetail();
    resourceDetail1.setSubmissionDetail(submissionDetail1);
    resourceDetail2.setSubmissionDetail(submissionDetail2);

    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail1))
        .thenReturn(resourceDetail1);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail2))
        .thenReturn(resourceDetail2);

    when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail1))
        .thenReturn(true);
    when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail2))
        .thenReturn(true);


    assertThat(regulatorSubmissionSearchSelectorService
        .getResourceDetailsForSubmissionsWithTeam(Stream.of(submissionDetail1, submissionDetail2),
            submissionType, USER)
        .toList())
        .containsExactly(resourceDetail1, resourceDetail2);
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void getResourceDetailsForSubmissions_whenNotInResponsibleTeam_assertEmpty(SubmissionType submissionType) {

    var submissionDetail = new SubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    when(responsibleTeamService.isInResponsibleTeam(USER, submissionDetail))
        .thenReturn(false);

    assertThat(regulatorSubmissionSearchSelectorService
        .getResourceDetailsForSubmissionsWithTeam(Stream.of(submissionDetail), submissionType, USER)
        .toList())
        .isEmpty();
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void getResourceDetailsForSubmissions_whenProspect_assertReturnedAndNoResponsibleTeamCheck(SubmissionType submissionType) {
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);

    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail))
        .thenReturn(resourceDetail);

    assertThat(regulatorSubmissionSearchSelectorService
        .getResourceDetailsForSubmissionsWithTeam(Stream.of(submissionDetail), submissionType, USER)
        .toList())
        .containsExactly(resourceDetail);

    verifyNoInteractions(regulatorManagedInformationPersistenceService);
    verifyNoInteractions(responsibleTeamService);
  }
}