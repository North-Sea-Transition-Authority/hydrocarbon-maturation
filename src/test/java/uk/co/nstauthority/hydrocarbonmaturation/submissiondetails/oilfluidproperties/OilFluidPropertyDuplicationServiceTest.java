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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class OilFluidPropertyDuplicationServiceTest {

  @Mock
  private OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;

  @Mock
  private ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @InjectMocks
  private OilFluidPropertyDuplicationService oilFluidPropertyDuplicationService;

  private ReservoirSubmissionDetail newSubmissionDetail;

  @BeforeEach
  void setUp() {
    newSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
  }

  @Test
  void duplicateSubmissionSection_OilFluidPropertyExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(ReservoirSubmissionDetail.class);

    var oilFluidProperty = mock(OilFluidProperty.class);
    when(oilFluidPropertyPersistenceService.getOilFluidProperty(oldSubmissionDetail))
        .thenReturn(Optional.of(oilFluidProperty));

    oilFluidPropertyDuplicationService.duplicateReservoirSection(oldSubmissionDetail, newSubmissionDetail);

    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(
        newSubmissionDetail,
        oilFluidProperty,
        OilFluidProperty.class);
  }

  @Test
  void duplicateSubmissionSection_OilFluidPropertyDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(ReservoirSubmissionDetail.class);

    when(oilFluidPropertyPersistenceService.getOilFluidProperty(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    oilFluidPropertyDuplicationService.duplicateReservoirSection(oldSubmissionDetail, newSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(OilFluidProperty.class), eq(OilFluidProperty.class));
  }
}