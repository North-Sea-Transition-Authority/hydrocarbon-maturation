package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;

@ExtendWith(MockitoExtension.class)
class ConsideringCcusDuplicationServiceTest {

  @Mock
  private ConsideringCcusDetailRepository consideringCcusDetailRepository;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private ConsideringCcusDuplicationService consideringCcusDuplicationService;

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void duplicationSubmissionSection_submissionsAreNonFieldSubmission_doesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    consideringCcusDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verifyNoInteractions(submissionDuplicationPersistenceService);
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void duplicationSubmissionSection_newSubmissionIsNonFieldSubmission_doesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    consideringCcusDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verifyNoInteractions(submissionDuplicationPersistenceService);
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void duplicateSumissionSection_fieldSubmission_detailDoesNotExists_doesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    consideringCcusDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verifyNoInteractions(submissionDuplicationPersistenceService);
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void duplicateSumissionSection_fieldSubmission_detailExists_doesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var consideringCcusDetail = mock(ConsideringCcusDetail.class);
    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(consideringCcusDetail));

    consideringCcusDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(newSubmissionDetail, consideringCcusDetail, ConsideringCcusDetail.class);
    verifyNoMoreInteractions(submissionDuplicationPersistenceService);
  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .map(Arguments::of);
  }

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }
}