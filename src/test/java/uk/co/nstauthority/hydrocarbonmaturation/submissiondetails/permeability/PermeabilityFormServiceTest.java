package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class PermeabilityFormServiceTest {

  @Mock
  private PermeabilityFormValidator permeabilityFormValidator;

  @Mock
  private PermeabilityPersistenceService permeabilityPersistenceService;

  @InjectMocks
  private PermeabilityFormService permeabilityFormService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void permeabilityDetailToForm_newEntity_thenEmptyForm() {
    when(permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail)).thenReturn(Optional.empty());

    assertThat(permeabilityFormService.getPermeabilityForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getPermeabilityValuesForm().getPermeabilityMin().asBigDecimal(),
            form -> form.getPermeabilityValuesForm().getPermeabilityMax().asBigDecimal(),
            form -> form.getPermeabilityValuesForm().getPermeabilityAverage().asBigDecimal(),
            form -> form.getPermeabilityValuesForm().getAverageMethodUsed(),
            form -> form.getPermeabilityValuesForm().getPermeabilitySource(),
            form -> form.getPermeabilityValuesForm().getOtherPermeabilitySource(),
            form -> form.getPermeabilityValuesForm().getPermeabilityComments(),
            form -> form.getPermeabilityValuesForm().getKvKhAverage().asBigDecimal())
        .containsExactly(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            null,
            null,
            null,
            null,
            Optional.empty()
        );
  }

  @Test
  void permeabilityDetailToForm_filledEntity_thenEmptyForm() {
    var filledEntity = new PermeabilityDetail();
    filledEntity.setHasPermeability(true);
    filledEntity.setAverageMethodUsed(AverageMethod.ARITHMETIC);
    filledEntity.setPermeabilityAverage(BigDecimal.ONE);
    filledEntity.setPermeabilityComments("comments");
    filledEntity.setPermeabilityMax(BigDecimal.ONE);
    filledEntity.setPermeabilityMin(BigDecimal.ONE);
    filledEntity.setPermeabilityAverage(BigDecimal.ONE);
    filledEntity.setPermeabilitySource(PermeabilitySource.OTHER);
    filledEntity.setKvKhAverage(BigDecimal.ONE);
    filledEntity.setSourceOther("comments");

    when(permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(filledEntity));

    assertThat(permeabilityFormService.getPermeabilityForm(reservoirSubmissionDetail))
        .extracting(
            form -> form.getPermeabilityValuesForm().getPermeabilityMin().asBigDecimal(),
            form -> form.getPermeabilityValuesForm().getPermeabilityMax().asBigDecimal(),
            form -> form.getPermeabilityValuesForm().getPermeabilityAverage().asBigDecimal(),
            form -> form.getPermeabilityValuesForm().getAverageMethodUsed(),
            form -> form.getPermeabilityValuesForm().getPermeabilitySource(),
            form -> form.getPermeabilityValuesForm().getOtherPermeabilitySource(),
            form -> form.getPermeabilityValuesForm().getPermeabilityComments(),
            form -> form.getPermeabilityValuesForm().getKvKhAverage().asBigDecimal())
        .containsExactly(
            Optional.of(BigDecimal.ONE),
            Optional.of(BigDecimal.ONE),
            Optional.of(BigDecimal.ONE),
            AverageMethod.ARITHMETIC,
            PermeabilitySource.OTHER,
            "comments",
            "comments",
            Optional.of(BigDecimal.ONE)
        );
  }
}