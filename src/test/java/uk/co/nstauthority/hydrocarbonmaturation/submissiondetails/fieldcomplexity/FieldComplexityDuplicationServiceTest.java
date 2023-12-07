package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;

@ExtendWith(MockitoExtension.class)
class FieldComplexityDuplicationServiceTest {

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Mock
  private FieldComplexityRepository fieldComplexityRepository;

  @InjectMocks
  private FieldComplexityDuplicationService fieldComplexityDuplicationService;

  private SubmissionDetail newSubmissionDetail;

  @BeforeEach
  void setUp() {
    newSubmissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void duplicateSubmissionSection_FieldComplexityExists_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var fieldComplexity = mock(FieldComplexity.class);
    when(fieldComplexityRepository.findBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(fieldComplexity));

    fieldComplexityDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, fieldComplexity,
        FieldComplexity.class);
  }


  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void duplicateSubmissionSection_FieldSubmission_FieldComplexityDoesNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    when(fieldComplexityRepository.findBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    fieldComplexityDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(FieldComplexity.class), eq(FieldComplexity.class));
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void duplicateSubmissionSection_NonFieldSubmission_DoesNotDuplicate(SubmissionType submissionType ) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    fieldComplexityDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(FieldComplexity.class), eq(FieldComplexity.class));
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