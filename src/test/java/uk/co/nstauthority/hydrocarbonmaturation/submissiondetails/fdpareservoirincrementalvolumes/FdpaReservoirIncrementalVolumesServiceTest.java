package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpareservoirincrementalvolumes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class FdpaReservoirIncrementalVolumesServiceTest {

  static final ProbabilityDistributionForm OIL_FORM = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
      ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA);
  static final ProbabilityDistributionForm GAS_FORM = ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
      ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA);
  static final String COMMENT = "SOME COMMENT";

  @Mock
  private FdpaReservoirIncrementalVolumesRepository incrementalVolumesRepository;

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @InjectMocks
  private FdpaReservoirIncrementalVolumesService fdpaReservoirIncrementalVolumesService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        SubmissionType.CREATE_FDPA);
  }

  @Nested
  class submissionDetailToForm {

    @Test
    void whenChangeToInPlaceVolumes_oilCase() {
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
          .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);

      var incrementalVolumes = new FdpaReservoirIncrementalVolume();
      incrementalVolumes.setChangeToInPlaceVolumes(true);
      incrementalVolumes.setComment(COMMENT);

      when(incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
          .thenReturn(Optional.of(incrementalVolumes));
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA))
          .thenReturn(OIL_FORM);

      var generatedForm = fdpaReservoirIncrementalVolumesService.submissionDetailToForm(reservoirSubmissionDetail);
      assertThat(generatedForm)
          .extracting(
              FdpaReservoirIncrementalVolumesForm::getChangeToInPlaceVolumes,
              FdpaReservoirIncrementalVolumesForm::getComment,
              FdpaReservoirIncrementalVolumesForm::getIncrementalIncreaseOil
          )
          .containsExactly(
              true,
              COMMENT,
              OIL_FORM
          );
      assertNestedFormEmpty(generatedForm.getIncrementalIncreaseGas());
    }

    @Test
    void whenChangeToInPlaceVolumes_gasCase() {
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
          .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);

      var incrementalVolumes = new FdpaReservoirIncrementalVolume();
      incrementalVolumes.setChangeToInPlaceVolumes(true);
      incrementalVolumes.setComment(COMMENT);

      when(incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
          .thenReturn(Optional.of(incrementalVolumes));
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA))
          .thenReturn(GAS_FORM);

      var generatedForm = fdpaReservoirIncrementalVolumesService.submissionDetailToForm(reservoirSubmissionDetail);
      assertThat(generatedForm)
          .extracting(
              FdpaReservoirIncrementalVolumesForm::getChangeToInPlaceVolumes,
              FdpaReservoirIncrementalVolumesForm::getComment,
              FdpaReservoirIncrementalVolumesForm::getIncrementalIncreaseGas
          )
          .containsExactly(
              true,
              COMMENT,
              GAS_FORM
          );
      assertNestedFormEmpty(generatedForm.getIncrementalIncreaseOil());
    }

    @Test
    void whenChangeToInPlaceVolumes_combinedCase() {
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
          .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);

      var incrementalVolumes = new FdpaReservoirIncrementalVolume();
      incrementalVolumes.setChangeToInPlaceVolumes(true);
      incrementalVolumes.setComment(COMMENT);

      when(incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
          .thenReturn(Optional.of(incrementalVolumes));
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA))
          .thenReturn(OIL_FORM);
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA))
          .thenReturn(GAS_FORM);

      var generatedForm = fdpaReservoirIncrementalVolumesService.submissionDetailToForm(reservoirSubmissionDetail);
      assertThat(generatedForm)
          .extracting(
              FdpaReservoirIncrementalVolumesForm::getChangeToInPlaceVolumes,
              FdpaReservoirIncrementalVolumesForm::getComment,
              FdpaReservoirIncrementalVolumesForm::getIncrementalIncreaseOil,
              FdpaReservoirIncrementalVolumesForm::getIncrementalIncreaseGas
          )
          .containsExactly(
              true,
              COMMENT,
              OIL_FORM,
              GAS_FORM
          );
    }

    @Test
    void whenNoChangeToInPlaceVolumes() {
      var incrementalVolumes = new FdpaReservoirIncrementalVolume();
      incrementalVolumes.setChangeToInPlaceVolumes(false);

      when(incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
          .thenReturn(Optional.of(incrementalVolumes));
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
          .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA))
          .thenReturn(new ProbabilityDistributionForm());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA))
          .thenReturn(new ProbabilityDistributionForm());

      var generatedForm = fdpaReservoirIncrementalVolumesService.submissionDetailToForm(reservoirSubmissionDetail);
      assertThat(generatedForm)
          .extracting(
              FdpaReservoirIncrementalVolumesForm::getChangeToInPlaceVolumes,
              FdpaReservoirIncrementalVolumesForm::getComment
          )
          .containsExactly(
              false,
              null
          );
      assertNestedFormEmpty(generatedForm.getIncrementalIncreaseOil());
      assertNestedFormEmpty(generatedForm.getIncrementalIncreaseGas());
    }

    @Test
    void whenNoIncrementalVolumesEntity() {
      when(incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
          .thenReturn(Optional.empty());
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
          .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA))
          .thenReturn(new ProbabilityDistributionForm());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA))
          .thenReturn(new ProbabilityDistributionForm());

      var generatedForm = fdpaReservoirIncrementalVolumesService.submissionDetailToForm(reservoirSubmissionDetail);
      assertThat(generatedForm)
          .extracting(
              FdpaReservoirIncrementalVolumesForm::getChangeToInPlaceVolumes,
              FdpaReservoirIncrementalVolumesForm::getComment
          )
          .containsExactly(
              null,
              null
          );
      assertNestedFormEmpty(generatedForm.getIncrementalIncreaseOil());
      assertNestedFormEmpty(generatedForm.getIncrementalIncreaseGas());
    }

    private void assertNestedFormEmpty(ProbabilityDistributionForm probabilityDistributionForm) {
      assertThat(probabilityDistributionForm.getCustom())
          .extracting(
              form -> form.getP90().createBigDecimalOrNull(),
              form -> form.getP50().createBigDecimalOrNull(),
              form -> form.getMean().createBigDecimalOrNull(),
              form -> form.getP10().createBigDecimalOrNull()
          )
          .containsOnlyNulls();
    }
  }

  @Nested
  class saveFdpaReservoirIncrementalVolume {

    ArgumentCaptor<FdpaReservoirIncrementalVolume> volumeArgumentCaptor
        = ArgumentCaptor.forClass(FdpaReservoirIncrementalVolume.class);

    @Test
    void whenChangeToInPlaceVolumes_oilCase() {
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
          .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);
      when(incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
          .thenReturn(Optional.empty());

      var form = getPopulatedForm();

      fdpaReservoirIncrementalVolumesService.saveFdpaReservoirIncrementalVolume(form, reservoirSubmissionDetail);

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA,
              OIL_FORM,
              reservoirSubmissionDetail
          );
      verify(probabilityDistributionPersistenceService, times(1))
          .deleteProbabilityDistribution(
              ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA,
              reservoirSubmissionDetail
          );

      verify(incrementalVolumesRepository, times(1))
          .save(volumeArgumentCaptor.capture());
      assertThat(volumeArgumentCaptor.getValue())
          .extracting(
              FdpaReservoirIncrementalVolume::getChangeToInPlaceVolumes,
              FdpaReservoirIncrementalVolume::getComment,
              FdpaReservoirIncrementalVolume::getReservoirSubmissionDetail
          )
          .containsExactly(
              true,
              COMMENT,
              reservoirSubmissionDetail
          );
    }

    @Test
    void whenChangeToInPlaceVolumes_gasCase() {
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
          .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);
      when(incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
          .thenReturn(Optional.empty());

      var form = getPopulatedForm();

      fdpaReservoirIncrementalVolumesService.saveFdpaReservoirIncrementalVolume(form, reservoirSubmissionDetail);

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA,
              GAS_FORM,
              reservoirSubmissionDetail
          );
      verify(probabilityDistributionPersistenceService, times(1))
          .deleteProbabilityDistribution(
              ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA,
              reservoirSubmissionDetail
          );

      verify(incrementalVolumesRepository, times(1))
          .save(volumeArgumentCaptor.capture());
      assertThat(volumeArgumentCaptor.getValue())
          .extracting(
              FdpaReservoirIncrementalVolume::getChangeToInPlaceVolumes,
              FdpaReservoirIncrementalVolume::getComment,
              FdpaReservoirIncrementalVolume::getReservoirSubmissionDetail
          )
          .containsExactly(
              true,
              COMMENT,
              reservoirSubmissionDetail
          );
    }

    @Test
    void whenChangeToInPlaceVolumes_combinedCase() {
      var incrementalVolume = new FdpaReservoirIncrementalVolume();
      incrementalVolume.setChangeToInPlaceVolumes(false);

      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
          .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
      when(incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
          .thenReturn(Optional.of(incrementalVolume));

      var form = getPopulatedForm();

      fdpaReservoirIncrementalVolumesService.saveFdpaReservoirIncrementalVolume(form, reservoirSubmissionDetail);

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA,
              OIL_FORM,
              reservoirSubmissionDetail
          );
      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA,
              GAS_FORM,
              reservoirSubmissionDetail
          );

      verify(incrementalVolumesRepository, times(1))
          .save(volumeArgumentCaptor.capture());
      assertThat(volumeArgumentCaptor.getValue())
          .extracting(
              FdpaReservoirIncrementalVolume::getChangeToInPlaceVolumes,
              FdpaReservoirIncrementalVolume::getComment,
              FdpaReservoirIncrementalVolume::getReservoirSubmissionDetail
          )
          .containsExactly(
              true,
              COMMENT,
              reservoirSubmissionDetail
          );
    }

    @Test
    void whenNoChangeToInPlaceVolumes() {
      when(incrementalVolumesRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
          .thenReturn(Optional.empty());

      var form = getPopulatedForm();
      form.setChangeToInPlaceVolumes(false);

      fdpaReservoirIncrementalVolumesService.saveFdpaReservoirIncrementalVolume(form, reservoirSubmissionDetail);

      verify(probabilityDistributionPersistenceService, never())
          .saveProbabilityDistributionWhenNotNull(
              any(),
              any(),
              any(ReservoirSubmissionDetail.class)
          );
      verify(probabilityDistributionPersistenceService, times(1))
          .deleteProbabilityDistributions(
              List.of(
                  ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA,
                  ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA
              ),
              reservoirSubmissionDetail
          );

      verify(incrementalVolumesRepository, times(1))
          .save(volumeArgumentCaptor.capture());
      assertThat(volumeArgumentCaptor.getValue())
          .extracting(
              FdpaReservoirIncrementalVolume::getChangeToInPlaceVolumes,
              FdpaReservoirIncrementalVolume::getComment,
              FdpaReservoirIncrementalVolume::getReservoirSubmissionDetail
          )
          .containsExactly(
              false,
              null,
              reservoirSubmissionDetail
          );
    }

    private FdpaReservoirIncrementalVolumesForm getPopulatedForm() {
      var form = new FdpaReservoirIncrementalVolumesForm();
      form.setChangeToInPlaceVolumes(true);
      form.setIncrementalIncreaseOil(OIL_FORM);
      form.setIncrementalIncreaseGas(GAS_FORM);
      form.setComment(COMMENT);
      return form;
    }
  }
}