package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class PermeabilityFormServiceTest {

  @Mock
  private PermeabilityPersistenceService permeabilityPersistenceService;

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @InjectMocks
  private PermeabilityFormService permeabilityFormService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);

  @Test
  void permeabilityDetailToForm_newEntity_thenEmptyForm() {
    when(permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail)).thenReturn(Optional.empty());

    var permeabilityForm = permeabilityFormService.getPermeabilityForm(reservoirSubmissionDetail);
    assertThat(permeabilityForm.getPermeabilityValuesForm())
        .extracting(
            form -> form.getPermeabilityMin().asBigDecimal(),
            form -> form.getPermeabilityMax().asBigDecimal(),
            form -> form.getPermeabilityAverage().asBigDecimal(),
            PermeabilityValuesForm::getAverageMethodUsed,
            PermeabilityValuesForm::getPermeabilitySource,
            PermeabilityValuesForm::getOtherPermeabilitySource,
            PermeabilityValuesForm::getPermeabilityComments,
            form -> form.getKvKhAverage().asBigDecimal())
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

    verifyNoInteractions(probabilityDistributionFormService);
  }

  @Test
  void permeabilityDetailToForm_filledEntity_thenFilledForm() {
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

    var permeabilityForm = permeabilityFormService.getPermeabilityForm(reservoirSubmissionDetail);
    assertThat(permeabilityForm.getPermeabilityValuesForm())
        .extracting(
            form -> form.getPermeabilityMin().asBigDecimal(),
            form -> form.getPermeabilityMax().asBigDecimal(),
            form -> form.getPermeabilityAverage().asBigDecimal(),
            PermeabilityValuesForm::getAverageMethodUsed,
            PermeabilityValuesForm::getPermeabilitySource,
            PermeabilityValuesForm::getOtherPermeabilitySource,
            PermeabilityValuesForm::getPermeabilityComments,
            form -> form.getKvKhAverage().asBigDecimal())
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

    verifyNoInteractions(probabilityDistributionFormService);
  }

  @Test
  void permeabilityDetailToForm_newEntity_field_thenEmptyForm() {
    var fieldSubmissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    var fieldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(fieldSubmissionDetail);

    when(permeabilityPersistenceService.getPermeabilityDetail(fieldReservoirSubmissionDetail)).thenReturn(Optional.empty());

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        fieldReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.FIELD_PERMEABILITY))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        fieldReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.FIELD_HORIZONTAL_PERMEABILITY))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        fieldReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.FIELD_VERTICAL_PERMEABILITY))
        .thenReturn(new ProbabilityDistributionForm());

    var permeabilityForm = permeabilityFormService.getPermeabilityForm(fieldReservoirSubmissionDetail);
    assertThat(permeabilityForm.getPermeabilityValuesForm())
        .extracting(
            form -> form.getPermeabilityMin().asBigDecimal(),
            form -> form.getPermeabilityMax().asBigDecimal(),
            form -> form.getPermeabilityAverage().asBigDecimal(),
            PermeabilityValuesForm::getAverageMethodUsed,
            PermeabilityValuesForm::getPermeabilitySource,
            PermeabilityValuesForm::getOtherPermeabilitySource,
            PermeabilityValuesForm::getPermeabilityComments,
            form -> form.getKvKhAverage().asBigDecimal(),
            form -> form.getPermeability().getProbabilityDistributionType(),
            form -> form.getHorizontalPermeability().getProbabilityDistributionType(),
            form -> form.getVerticalPermeability().getProbabilityDistributionType())
        .containsExactly(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            null,
            null,
            null,
            null,
            Optional.empty(),
            null,
            null,
            null
        );
  }

  @Test
  void permeabilityDetailToForm_filledEntity_field_thenFilledForm() {
    var fieldSubmissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    var fieldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(fieldSubmissionDetail);

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

    when(permeabilityPersistenceService.getPermeabilityDetail(fieldReservoirSubmissionDetail))
        .thenReturn(Optional.of(filledEntity));

    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm();
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        fieldReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.FIELD_PERMEABILITY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        fieldReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.FIELD_HORIZONTAL_PERMEABILITY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        fieldReservoirSubmissionDetail, ProbabilityDistributionFieldInstance.FIELD_VERTICAL_PERMEABILITY))
        .thenReturn(probabilityDistributionForm);

    var permeabilityForm = permeabilityFormService.getPermeabilityForm(fieldReservoirSubmissionDetail);
    assertThat(permeabilityForm.getPermeabilityValuesForm())
        .extracting(
            form -> form.getPermeabilityMin().asBigDecimal(),
            form -> form.getPermeabilityMax().asBigDecimal(),
            form -> form.getPermeabilityAverage().asBigDecimal(),
            PermeabilityValuesForm::getAverageMethodUsed,
            PermeabilityValuesForm::getPermeabilitySource,
            PermeabilityValuesForm::getOtherPermeabilitySource,
            PermeabilityValuesForm::getPermeabilityComments,
            form -> form.getKvKhAverage().asBigDecimal(),
            form -> form.getPermeability().getProbabilityDistributionType(),
            form -> form.getPermeability().getCustom().getConstant(),
            form -> form.getPermeability().getCustom().getComments(),
            form -> form.getHorizontalPermeability().getProbabilityDistributionType(),
            form -> form.getHorizontalPermeability().getCustom().getConstant(),
            form -> form.getHorizontalPermeability().getCustom().getComments(),
            form -> form.getVerticalPermeability().getProbabilityDistributionType(),
            form -> form.getVerticalPermeability().getCustom().getConstant(),
            form -> form.getVerticalPermeability().getCustom().getComments())
        .containsExactly(
            Optional.of(BigDecimal.ONE),
            Optional.of(BigDecimal.ONE),
            Optional.of(BigDecimal.ONE),
            AverageMethod.ARITHMETIC,
            PermeabilitySource.OTHER,
            "comments",
            "comments",
            Optional.of(BigDecimal.ONE),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getCustom().getConstant(),
            probabilityDistributionForm.getCustom().getComments()
        );
  }
}