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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class GeneralFluidPropertyDuplicationServiceTest {

  @Mock
  private GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;

  @Mock
  private ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @InjectMocks
  private GeneralFluidPropertyDuplicationService generalFluidPropertyDuplicationService;

  private SubmissionDetail newSubmissionDetail;
  private ReservoirSubmissionDetail newReservoirSubmissionDetail;
  private ReservoirSubmissionDetail oldReservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    newSubmissionDetail = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
    newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(newSubmissionDetail);
    oldReservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);
  }

  @Test
  void duplicateSubmissionSection_GeneralFluidPropertyExists_DoesDuplicate() {
    var generalFluidProperty = mock(GeneralFluidProperty.class);
    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(oldReservoirSubmissionDetail))
        .thenReturn(Optional.of(generalFluidProperty));

    generalFluidPropertyDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newReservoirSubmissionDetail, generalFluidProperty,
        GeneralFluidProperty.class);
  }

  @Test
  void duplicateSubmissionSection_GeneralFluidPropertyDoesNotExist_DoesNotDuplicate() {
    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(oldReservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    generalFluidPropertyDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(GeneralFluidProperty.class), eq(GeneralFluidProperty.class));
  }
}