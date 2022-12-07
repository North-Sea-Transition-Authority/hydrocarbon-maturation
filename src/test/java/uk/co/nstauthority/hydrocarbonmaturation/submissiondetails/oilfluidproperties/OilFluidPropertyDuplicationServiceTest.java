package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

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
class OilFluidPropertyDuplicationServiceTest {

  @Mock
  private OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private OilFluidPropertyDuplicationService oilFluidPropertyDuplicationService;

  private SubmissionDetail newSubmissionDetail;

  @BeforeEach
  void setUp() {
    newSubmissionDetail = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
  }

  @Test
  void duplicateSubmissionSection_OilFluidPropertyExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);

    var oilFluidProperty = mock(OilFluidProperty.class);
    when(oilFluidPropertyPersistenceService.getOilFluidProperty(oldSubmissionDetail))
        .thenReturn(Optional.of(oilFluidProperty));

    oilFluidPropertyDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, oilFluidProperty,
        OilFluidProperty.class);
  }

  @Test
  void duplicateSubmissionSection_OilFluidPropertyDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);

    when(oilFluidPropertyPersistenceService.getOilFluidProperty(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    oilFluidPropertyDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(OilFluidProperty.class), eq(OilFluidProperty.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"UPDATE_DISCOVERY"}, mode = EnumSource.Mode.EXCLUDE)
  void duplicateSubmissionSection_OilFluidPropertyDoesNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    newSubmissionDetail.setSubmissionType(submissionType);

    oilFluidPropertyDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(OilFluidProperty.class), eq(OilFluidProperty.class));
  }
}