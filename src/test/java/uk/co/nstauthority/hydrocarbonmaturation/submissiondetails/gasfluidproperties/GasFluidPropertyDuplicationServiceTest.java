package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;

@ExtendWith(MockitoExtension.class)
class GasFluidPropertyDuplicationServiceTest {

  @Mock
  private GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private GasFluidPropertyDuplicationService gasFluidPropertyDuplicationService;

  private SubmissionDetail newSubmissionDetail;

  @BeforeEach
  void setUp() {
    newSubmissionDetail = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
  }

  @Test
  void duplicateSubmissionSection_GasFluidPropertyExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);

    var gasFluidProperty = mock(GasFluidProperty.class);
    when(gasFluidPropertyPersistenceService.getGasFluidProperty(oldSubmissionDetail))
        .thenReturn(Optional.of(gasFluidProperty));

    gasFluidPropertyDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, gasFluidProperty,
        GasFluidProperty.class);
  }

  @Test
  void duplicateSubmissionSection_GasFluidPropertyDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);

    when(gasFluidPropertyPersistenceService.getGasFluidProperty(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    gasFluidPropertyDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(GasFluidProperty.class), eq(GasFluidProperty.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"UPDATE_DISCOVERY"}, mode = EnumSource.Mode.EXCLUDE)
  void duplicateSubmissionSection_GasFluidPropertyDoesNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    newSubmissionDetail.setSubmissionType(submissionType);

    gasFluidPropertyDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(GasFluidProperty.class), eq(GasFluidProperty.class));
  }
}