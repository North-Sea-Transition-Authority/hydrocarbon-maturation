package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@ExtendWith(MockitoExtension.class)
class ChanceOfSuccessDuplicationServiceTest {
  @Mock
  private ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private ChanceOfSuccessDuplicationService chanceOfSuccessDuplicationService;

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "PROSPECT_UPDATE", "CREATE_PROSPECT", "RELINQUISHMENT_PROSPECT",
          "UPDATE_DISCOVERY", "CREATE_DISCOVERY", "CREATE_DISCOVERY_FROM_PROSPECT", "RELINQUISHMENT_DISCOVERY"
      }
  )
  void duplicateSubmissionSection_ChanceOfSuccessExists_DoesDuplicate(SubmissionType submissionType) {
    var genericResourceType = ResourceType.DISCOVERY;
    var oldSubmissionDetail = getSubmissionDetail(submissionType, genericResourceType);
    var newSubmissionDetail = getSubmissionDetail(submissionType, genericResourceType);

    var chanceOfSuccess = mock(ChanceOfSuccess.class);
    when(chanceOfSuccessPersistenceService.getChanceOfSuccess(oldSubmissionDetail))
        .thenReturn(Optional.of(chanceOfSuccess));

    chanceOfSuccessDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, chanceOfSuccess,
        ChanceOfSuccess.class);
  }

  @Test
  void duplicateSubmissionSection_ChanceOfSuccessDoesNotExist_DoesNotDuplicate() {
    var genericResourceType = ResourceType.DISCOVERY;
    var genericSubmissionType = SubmissionType.UPDATE_DISCOVERY;
    var oldSubmissionDetail = getSubmissionDetail(genericSubmissionType, genericResourceType);
    var newSubmissionDetail = getSubmissionDetail(genericSubmissionType, genericResourceType);

    when(chanceOfSuccessPersistenceService.getChanceOfSuccess(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    chanceOfSuccessDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ChanceOfSuccess.class), eq(ChanceOfSuccess.class));
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "PROSPECT_UPDATE", "CREATE_PROSPECT", "RELINQUISHMENT_PROSPECT",
          "UPDATE_DISCOVERY", "CREATE_DISCOVERY", "CREATE_DISCOVERY_FROM_PROSPECT", "RELINQUISHMENT_DISCOVERY"
      },
      mode = EnumSource.Mode.EXCLUDE
  )
  void duplicateSubmissionSection_DoesNotDuplicateToNonApplicableSubmissionType(SubmissionType submissionType) {
    var genericResourceType = ResourceType.DISCOVERY;
    var oldSubmissionDetail = getSubmissionDetail(submissionType, genericResourceType);
    var newSubmissionDetail = getSubmissionDetail(submissionType, genericResourceType);

    chanceOfSuccessDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(chanceOfSuccessPersistenceService, never())
        .getChanceOfSuccess(oldSubmissionDetail);
    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ChanceOfSuccess.class), eq(ChanceOfSuccess.class));
  }

  @Test
  void duplicateSubmissionSection_whenFromProspectToDiscovery_doesNotDuplicate() {
    var genericSubmissionType = SubmissionType.UPDATE_DISCOVERY;
    var oldSubmissionDetail = getSubmissionDetail(genericSubmissionType, ResourceType.PROSPECT);
    var newSubmissionDetail = getSubmissionDetail(genericSubmissionType, ResourceType.DISCOVERY);

    chanceOfSuccessDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ChanceOfSuccess.class), eq(ChanceOfSuccess.class));
  }

  private SubmissionDetail getSubmissionDetail(SubmissionType submissionType, ResourceType resourceType) {
    var resource = new Resource();
    resource.setType(resourceType);

    var submission = new Submission();
    submission.setResource(resource);

    var submissionDetail = new SubmissionDetail();
    submissionDetail.setSubmission(submission);
    submissionDetail.setSubmissionType(submissionType);

    return submissionDetail;
  }
}