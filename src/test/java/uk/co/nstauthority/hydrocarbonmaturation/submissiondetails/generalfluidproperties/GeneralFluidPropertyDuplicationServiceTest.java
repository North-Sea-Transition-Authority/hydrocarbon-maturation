package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

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
class GeneralFluidPropertyDuplicationServiceTest {

  @Mock
  private GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private GeneralFluidPropertyDuplicationService generalFluidPropertyDuplicationService;

  private SubmissionDetail newSubmissionDetail;

  @BeforeEach
  void setUp() {
    newSubmissionDetail = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
  }

  @Test
  void duplicateSubmissionSection_GeneralFluidPropertyExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);

    var generalFluidProperty = mock(GeneralFluidProperty.class);
    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(oldSubmissionDetail))
        .thenReturn(Optional.of(generalFluidProperty));

    generalFluidPropertyDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, generalFluidProperty,
        GeneralFluidProperty.class);
  }

  @Test
  void duplicateSubmissionSection_GeneralFluidPropertyDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);

    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    generalFluidPropertyDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(GeneralFluidProperty.class), eq(GeneralFluidProperty.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"UPDATE_DISCOVERY"}, mode = EnumSource.Mode.EXCLUDE)
  void duplicateSubmissionSection_GeneralFluidPropertyDoesNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    newSubmissionDetail.setSubmissionType(submissionType);

    generalFluidPropertyDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(GeneralFluidProperty.class), eq(GeneralFluidProperty.class));
  }
}