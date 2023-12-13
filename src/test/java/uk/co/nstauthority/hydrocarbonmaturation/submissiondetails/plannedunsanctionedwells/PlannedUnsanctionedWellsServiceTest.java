package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class PlannedUnsanctionedWellsServiceTest {
  @Mock
  private PlannedUnsanctionedWellsRepository plannedUnsanctionedWellsRepository;
  @InjectMocks
  private PlannedUnsanctionedWellsService plannedUnsanctionedWellsService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @Test
  void submissionDetailToForm_existing() {
    var plannedUnsanctionedWells = new PlannedUnsanctionedWells();
    plannedUnsanctionedWells.setTotalCapex(BigDecimal.valueOf(5));
    plannedUnsanctionedWells.setNumberOfPlannedUnsanctionedWells(BigDecimal.valueOf(10));

    when(plannedUnsanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(plannedUnsanctionedWells));

    assertThat(plannedUnsanctionedWellsService.submissionDetailToForm(submissionDetail))
        .extracting(
            plannedUnsanctionedWellsForm ->
                plannedUnsanctionedWellsForm.getTotalCapex().createBigDecimalOrNull(),
            plannedUnsanctionedWellsForm1 ->
                plannedUnsanctionedWellsForm1.getTotalNumberOfPlannedUnsanctionedWells().createBigDecimalOrNull()
        ).containsExactly(
            plannedUnsanctionedWells.getTotalCapex(),
            plannedUnsanctionedWells.getNumberOfPlannedUnsanctionedWells()
        );
  }

  @Test
  void submissionDetailToForm_noneExisting() {
    when(plannedUnsanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    assertThat(plannedUnsanctionedWellsService.submissionDetailToForm(submissionDetail))
        .extracting(
            plannedUnsanctionedWellsForm ->
                plannedUnsanctionedWellsForm.getTotalCapex().createBigDecimalOrNull(),
            plannedUnsanctionedWellsForm1 ->
                plannedUnsanctionedWellsForm1.getTotalNumberOfPlannedUnsanctionedWells().createBigDecimalOrNull()
        ).containsOnlyNulls();
  }

  @Test
  void savePlannedUnsanctionedWells_noneExisting() {
    var form = new PlannedUnsanctionedWellsForm();
    form.setTotalNumberOfPlannedUnsanctionedWells(DecimalInput.createDecimalInput(10));
    form.setTotalCapex(DecimalInput.createDecimalInput(30));

    when(plannedUnsanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var captor = ArgumentCaptor.forClass(PlannedUnsanctionedWells.class);

    plannedUnsanctionedWellsService.savePlannedUnsanctionedWells(form, submissionDetail);

    verify(plannedUnsanctionedWellsRepository, times(1)).save(captor.capture());
    verifyNoMoreInteractions(plannedUnsanctionedWellsRepository);

    assertThat(captor.getValue())
        .extracting(
            PlannedUnsanctionedWells::getNumberOfPlannedUnsanctionedWells,
            PlannedUnsanctionedWells::getTotalCapex,
            PlannedUnsanctionedWells::getSubmissionDetail
        ).containsExactly(
            form.getTotalNumberOfPlannedUnsanctionedWells().createBigDecimalOrNull(),
            form.getTotalCapex().createBigDecimalOrNull(),
            submissionDetail
        );
  }

  @Test
  void savePlannedUnsanctionedWells_existing() {
    var form = new PlannedUnsanctionedWellsForm();
    form.setTotalNumberOfPlannedUnsanctionedWells(DecimalInput.createDecimalInput(10));
    form.setTotalCapex(DecimalInput.createDecimalInput(30));

    var existingPlannedUnsanctionedWells = new PlannedUnsanctionedWells();
    existingPlannedUnsanctionedWells.setTotalCapex(BigDecimal.valueOf(10));

    when(plannedUnsanctionedWellsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(existingPlannedUnsanctionedWells));

    var captor = ArgumentCaptor.forClass(PlannedUnsanctionedWells.class);

    plannedUnsanctionedWellsService.savePlannedUnsanctionedWells(form, submissionDetail);

    verify(plannedUnsanctionedWellsRepository, times(1)).save(captor.capture());
    verifyNoMoreInteractions(plannedUnsanctionedWellsRepository);

    assertThat(captor.getValue())
        .extracting(
            PlannedUnsanctionedWells::getNumberOfPlannedUnsanctionedWells,
            PlannedUnsanctionedWells::getTotalCapex,
            PlannedUnsanctionedWells::getSubmissionDetail
        ).containsExactly(
            form.getTotalNumberOfPlannedUnsanctionedWells().createBigDecimalOrNull(),
            existingPlannedUnsanctionedWells.getTotalCapex(),
            submissionDetail
        );
  }
}