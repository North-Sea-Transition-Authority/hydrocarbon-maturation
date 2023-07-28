package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class PermeabilityPersistenceServiceTest {

  @Mock
  private PermeabilityDetailRepository permeabilityDetailRepository;

  @InjectMocks
  private PermeabilityPersistenceService permeabilityPersistenceService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void savePermeabilityDetail_verifyMethodCall() {
    when(permeabilityDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());
    var form = PermeabilityFormValidatorTest.getValidFormWithPermeability().getPermeabilityValuesForm();
    var permeabilityDryWellForm = new PermeabilityForm();
    permeabilityDryWellForm.setPermeabilityValuesForm(form);
    permeabilityDryWellForm.setHasPermeability(true);

    var captor = ArgumentCaptor.forClass(PermeabilityDetail.class);
    permeabilityPersistenceService.savePermeabilityDetail(permeabilityDryWellForm, reservoirSubmissionDetail);
    verify(permeabilityDetailRepository, times(1)).save(captor.capture());

    var savedPermeabilityDetail = captor.getValue();
    assertThat(savedPermeabilityDetail)
        .extracting(
            PermeabilityDetail::getHasPermeability,
            PermeabilityDetail::getSourceOther,
            PermeabilityDetail::getReservoirSubmissionDetail,
            PermeabilityDetail::getAverageMethodUsed,
            PermeabilityDetail::getPermeabilitySource,
            PermeabilityDetail::getPermeabilityComments,
            PermeabilityDetail::getPermeabilityAverage,
            PermeabilityDetail::getPermeabilityMax,
            PermeabilityDetail::getPermeabilityMin,
            PermeabilityDetail::getKvKhAverage)
        .containsExactly(
            permeabilityDryWellForm.getHasPermeability(),
            form.getOtherPermeabilitySource(),
            reservoirSubmissionDetail,
            form.getAverageMethodUsed(),
            form.getPermeabilitySource(),
            form.getPermeabilityComments(),
            form.getPermeabilityAverage().createBigDecimalOrNull(),
            form.getPermeabilityMax().createBigDecimalOrNull(),
            form.getPermeabilityMin().createBigDecimalOrNull(),
            form.getKvKhAverage().createBigDecimalOrNull()
        );
  }

  @Test
  void savePermeabilityDetail_otherSource_verifyMethodCall() {
    when(permeabilityDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());
    var form = PermeabilityFormValidatorTest.getValidFormWithPermeability().getPermeabilityValuesForm();
    var permeabilityDryWellForm = new PermeabilityForm();

    form.setPermeabilitySource(PermeabilitySource.OTHER);
    form.setOtherPermeabilitySource("other source");

    permeabilityDryWellForm.setPermeabilityValuesForm(form);
    permeabilityDryWellForm.setHasPermeability(true);

    var captor = ArgumentCaptor.forClass(PermeabilityDetail.class);
    permeabilityPersistenceService.savePermeabilityDetail(permeabilityDryWellForm, reservoirSubmissionDetail);
    verify(permeabilityDetailRepository, times(1)).save(captor.capture());

    var savedPermeabilityDetail = captor.getValue();
    assertThat(savedPermeabilityDetail)
        .extracting(
            PermeabilityDetail::getHasPermeability,
            PermeabilityDetail::getSourceOther,
            PermeabilityDetail::getReservoirSubmissionDetail,
            PermeabilityDetail::getAverageMethodUsed,
            PermeabilityDetail::getPermeabilitySource,
            PermeabilityDetail::getPermeabilityComments,
            PermeabilityDetail::getPermeabilityAverage,
            PermeabilityDetail::getPermeabilityMax,
            PermeabilityDetail::getPermeabilityMin,
            PermeabilityDetail::getKvKhAverage)
        .containsExactly(
            permeabilityDryWellForm.getHasPermeability(),
            form.getOtherPermeabilitySource(),
            reservoirSubmissionDetail,
            form.getAverageMethodUsed(),
            form.getPermeabilitySource(),
            form.getPermeabilityComments(),
            form.getPermeabilityAverage().createBigDecimalOrNull(),
            form.getPermeabilityMax().createBigDecimalOrNull(),
            form.getPermeabilityMin().createBigDecimalOrNull(),
            form.getKvKhAverage().createBigDecimalOrNull()
        );
  }

  @Test
  void getPermeabilityDetail_verifyMethodCall() {
    permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail);

    verify(permeabilityDetailRepository, times(1))
        .findByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }
}