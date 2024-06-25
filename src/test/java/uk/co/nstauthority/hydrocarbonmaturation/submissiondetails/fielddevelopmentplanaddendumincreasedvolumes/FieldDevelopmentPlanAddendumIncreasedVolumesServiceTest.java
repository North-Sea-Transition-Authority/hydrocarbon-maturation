package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fielddevelopmentplanaddendumincreasedvolumes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;

@ExtendWith(MockitoExtension.class)
class FieldDevelopmentPlanAddendumIncreasedVolumesServiceTest {

  private static final String COMMENT = "some comment";

  @Mock
  private FieldDevelopmentPlanAddendumIncreasedVolumesRepository fdpaIncreasedVolumesRepository;

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @InjectMocks
  private FieldDevelopmentPlanAddendumIncreasedVolumesService volumesService;

  private SubmissionDetail fdpaSubmissionDetail;

  @BeforeEach
  void setUp() {
    fdpaSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDPA);
  }

  @Nested
  class SubmissionDetailToForm {

    @Test
    void whenOilCase() {
      var fdpaIncreasedVolume = getIncreasedVolumeWithChange();

      when(fdpaIncreasedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.of(fdpaIncreasedVolume));
      mockOilProbabilityDistributions();

      var form = volumesService.submissionDetailToForm(fdpaSubmissionDetail);
      assertThat(form)
          .extracting(
              FieldDevelopmentPlanAddendumIncreasedVolumesForm::getChangeInVolumetrics,
              FieldDevelopmentPlanAddendumIncreasedVolumesForm::getComment
          )
          .containsExactly(
              true,
              COMMENT
          );

      assertOilProbabilityDistributions(form);
    }

    @Test
    void whenGasCase() {
      var fdpaIncreasedVolume = getIncreasedVolumeWithChange();

      when(fdpaIncreasedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.of(fdpaIncreasedVolume));
      mockGasProbabilityDistributions();

      var form = volumesService.submissionDetailToForm(fdpaSubmissionDetail);
      assertThat(form)
          .extracting(
              FieldDevelopmentPlanAddendumIncreasedVolumesForm::getChangeInVolumetrics,
              FieldDevelopmentPlanAddendumIncreasedVolumesForm::getComment
          )
          .containsExactly(
              true,
              COMMENT
          );

      assertGasProbabilityDistributions(form);
    }

    @Test
    void whenCombinedCase() {
      var fdpaIncreasedVolume = getIncreasedVolumeWithChange();

      when(fdpaIncreasedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.of(fdpaIncreasedVolume));
      mockCombinedProbabilityDistributions();

      var form = volumesService.submissionDetailToForm(fdpaSubmissionDetail);
      assertThat(form)
          .extracting(
              FieldDevelopmentPlanAddendumIncreasedVolumesForm::getChangeInVolumetrics,
              FieldDevelopmentPlanAddendumIncreasedVolumesForm::getComment
          )
          .containsExactly(
              true,
              COMMENT
          );

      assertCombinedProbabilityDistributions(form);
    }

    @Test
    void whenNoChangeInVolumetrics() {
      var fdpaIncreasedVolume = new FieldDevelopmentPlanAddendumIncreasedVolume();
      fdpaIncreasedVolume.setChangeInVolumetrics(false);

      when(fdpaIncreasedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.of(fdpaIncreasedVolume));

      var form = volumesService.submissionDetailToForm(fdpaSubmissionDetail);
      assertThat(form)
          .extracting(
              FieldDevelopmentPlanAddendumIncreasedVolumesForm::getChangeInVolumetrics,
              FieldDevelopmentPlanAddendumIncreasedVolumesForm::getComment
          )
          .containsExactly(
              false,
              null
          );

      assertProbabilityDistributionsEmpty(form);
    }

    @Test
    void whenNoStoredEntity() {
      when(fdpaIncreasedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.empty());

      var form = volumesService.submissionDetailToForm(fdpaSubmissionDetail);
      assertThat(form)
          .extracting(
              FieldDevelopmentPlanAddendumIncreasedVolumesForm::getChangeInVolumetrics,
              FieldDevelopmentPlanAddendumIncreasedVolumesForm::getComment
          )
          .containsExactly(
              null,
              null
          );

      assertProbabilityDistributionsEmpty(form);
    }

    private FieldDevelopmentPlanAddendumIncreasedVolume getIncreasedVolumeWithChange() {
      var fieldDevelopmentPlanAddendumIncreasedVolume = new FieldDevelopmentPlanAddendumIncreasedVolume();
      fieldDevelopmentPlanAddendumIncreasedVolume.setChangeInVolumetrics(true);
      fieldDevelopmentPlanAddendumIncreasedVolume.setComment(COMMENT);
      return fieldDevelopmentPlanAddendumIncreasedVolume;
    }

    private void assertProbabilityDistributionsEmpty(FieldDevelopmentPlanAddendumIncreasedVolumesForm form) {
      assertNestedFormValuesEmpty(form.getOilInitiallyInPlace());
      assertNestedFormValuesEmpty(form.getGasInitiallyInPlace());
      assertNestedFormValuesEmpty(form.getOilRecoverableResources());
      assertNestedFormValuesEmpty(form.getOilReserves());
      assertNestedFormValuesEmpty(form.getAssociatedCondensateRecoverableResources());
      assertNestedFormValuesEmpty(form.getAssociatedCondensateReserves());
      assertNestedFormValuesEmpty(form.getNaturalGasLiquidsRecoverableResources());
      assertNestedFormValuesEmpty(form.getNaturalGasLiquidsReserves());
      assertNestedFormValuesEmpty(form.getSalesGasRecoverableResources());
      assertNestedFormValuesEmpty(form.getSalesGasReserves());
      assertNestedFormValuesEmpty(form.getOilEquivalentRecoverableResources());
      assertNestedFormValuesEmpty(form.getOilEquivalentReserves());
    }

    private void assertNestedFormValuesEmpty(ProbabilityDistributionForm probabilityDistributionForm) {
      assertThat(probabilityDistributionForm.getCustom())
          .extracting(
              form -> form.getP90().createBigDecimalOrNull(),
              form -> form.getP50().createBigDecimalOrNull(),
              form -> form.getMean().createBigDecimalOrNull(),
              form -> form.getP10().createBigDecimalOrNull(),
              form -> form.getOneC().createBigDecimalOrNull(),
              form -> form.getTwoC().createBigDecimalOrNull(),
              form -> form.getThreeC().createBigDecimalOrNull(),
              form -> form.getOneP().createBigDecimalOrNull(),
              form -> form.getTwoP().createBigDecimalOrNull(),
              form -> form.getThreeP().createBigDecimalOrNull()
          )
          .containsOnlyNulls();
    }

    private void mockSharedProbabilityDistributions() {
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail,
          ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA))
          .thenReturn(ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FORM());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail,
          ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA))
          .thenReturn(ASSOCIATED_CONDENSATE_RESERVES_FORM());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail,
          ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA))
          .thenReturn(NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_FORM());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail,
          ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA))
          .thenReturn(NATURAL_GAS_LIQUIDS_RESERVES_FORM());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail,
          ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA))
          .thenReturn(SALES_GAS_RECOVERABLE_RESOURCES_FORM());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail, ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA))
          .thenReturn(SALES_GAS_RESERVES_FORM());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA))
          .thenReturn(OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FORM());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA))
          .thenReturn(OIL_EQUIVALENT_RESERVES_FORM());
    }

    private void mockOilProbabilityDistributions() {
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(fdpaSubmissionDetail))
          .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);

      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA))
          .thenReturn(OIL_INITIALLY_IN_PLACE_FORM());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA))
          .thenReturn(OIL_RECOVERABLE_RESOURCES_FORM());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA))
          .thenReturn(OIL_RESERVES_FORM());

      mockSharedProbabilityDistributions();
    }

    private void mockGasProbabilityDistributions() {
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(fdpaSubmissionDetail))
          .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);

      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA))
          .thenReturn(GAS_INITIALLY_IN_PLACE_FORM());

      mockSharedProbabilityDistributions();
    }

    private void mockCombinedProbabilityDistributions() {
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(fdpaSubmissionDetail))
          .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);

      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA))
          .thenReturn(OIL_INITIALLY_IN_PLACE_FORM());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA))
          .thenReturn(OIL_RECOVERABLE_RESOURCES_FORM());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail, ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA))
          .thenReturn(OIL_RESERVES_FORM());
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          fdpaSubmissionDetail, ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA))
          .thenReturn(GAS_INITIALLY_IN_PLACE_FORM());

      mockSharedProbabilityDistributions();
    }

    private void assertSharedProbabilityDistributions(FieldDevelopmentPlanAddendumIncreasedVolumesForm form) {
      assertThat(form.getAssociatedCondensateRecoverableResources())
          .usingRecursiveComparison()
          .isEqualTo(ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FORM());

      assertThat(form.getNaturalGasLiquidsRecoverableResources())
          .usingRecursiveComparison()
          .isEqualTo(NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_FORM());

      assertThat(form.getNaturalGasLiquidsReserves())
          .usingRecursiveComparison()
          .isEqualTo(NATURAL_GAS_LIQUIDS_RESERVES_FORM());

      assertThat(form.getSalesGasRecoverableResources())
          .usingRecursiveComparison()
          .isEqualTo(SALES_GAS_RECOVERABLE_RESOURCES_FORM());

      assertThat(form.getSalesGasReserves())
          .usingRecursiveComparison()
          .isEqualTo(SALES_GAS_RESERVES_FORM());

      assertThat(form.getOilEquivalentRecoverableResources())
          .usingRecursiveComparison()
          .isEqualTo(OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FORM());

      assertThat(form.getOilEquivalentReserves())
          .usingRecursiveComparison()
          .isEqualTo(OIL_EQUIVALENT_RESERVES_FORM());
    }

    private void assertOilProbabilityDistributions(FieldDevelopmentPlanAddendumIncreasedVolumesForm form) {
      assertNestedFormValuesEmpty(form.getGasInitiallyInPlace());

      assertThat(form.getOilInitiallyInPlace())
          .usingRecursiveComparison()
          .isEqualTo(OIL_INITIALLY_IN_PLACE_FORM());

      assertThat(form.getOilRecoverableResources())
          .usingRecursiveComparison()
          .isEqualTo(OIL_RECOVERABLE_RESOURCES_FORM());

      assertThat(form.getOilReserves())
          .usingRecursiveComparison()
          .isEqualTo(OIL_RESERVES_FORM());

      assertSharedProbabilityDistributions(form);
    }

    private void assertGasProbabilityDistributions(FieldDevelopmentPlanAddendumIncreasedVolumesForm form) {
      assertNestedFormValuesEmpty(form.getOilInitiallyInPlace());
      assertNestedFormValuesEmpty(form.getOilRecoverableResources());
      assertNestedFormValuesEmpty(form.getOilReserves());

      assertThat(form.getGasInitiallyInPlace())
          .usingRecursiveComparison()
          .isEqualTo(GAS_INITIALLY_IN_PLACE_FORM());

      assertSharedProbabilityDistributions(form);
    }

    private void assertCombinedProbabilityDistributions(FieldDevelopmentPlanAddendumIncreasedVolumesForm form) {
      assertThat(form.getOilInitiallyInPlace())
          .usingRecursiveComparison()
          .isEqualTo(OIL_INITIALLY_IN_PLACE_FORM());

      assertThat(form.getOilRecoverableResources())
          .usingRecursiveComparison()
          .isEqualTo(OIL_RECOVERABLE_RESOURCES_FORM());

      assertThat(form.getOilReserves())
          .usingRecursiveComparison()
          .isEqualTo(OIL_RESERVES_FORM());
      assertThat(form.getGasInitiallyInPlace())
          .usingRecursiveComparison()
          .isEqualTo(GAS_INITIALLY_IN_PLACE_FORM());

      assertSharedProbabilityDistributions(form);
    }
  }

  @Nested
  class SaveFdpaIncreasedVolumes {

    private static final List<ProbabilityDistributionFieldInstance> ALL_FIELD_INSTANCES = List.of(
        // oil only
        ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA,

        // gas only
        ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA,

        //combined case
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA
    );

    private final ArgumentCaptor<FieldDevelopmentPlanAddendumIncreasedVolume> increasedVolumeArgumentCaptor
        = ArgumentCaptor.forClass(FieldDevelopmentPlanAddendumIncreasedVolume.class);

    private final ArgumentCaptor<ProbabilityDistributionForm> probabilityArgumentCaptor
        = ArgumentCaptor.forClass(ProbabilityDistributionForm.class);

    @Test
    void whenOilCase() {
      // WHEN oil only
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(fdpaSubmissionDetail))
          .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);
      when(fdpaIncreasedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.empty());

      volumesService.saveFdpaIncreasedVolumes(getFormWithChange(), fdpaSubmissionDetail);

      // THEN oil only and shared probability distributions are saved
      verifyOilProbabilityDistributionsSaved();
      verifySharedProbabilityDistributionsSaved();

      // AND gas only probability distributions are deleted
      verify(probabilityDistributionPersistenceService, times(1))
          .deleteProbabilityDistribution(
              ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA,
              fdpaSubmissionDetail
          );
    }

    @Test
    void whenGasCase() {
      // WHEN gas only
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(fdpaSubmissionDetail))
          .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);
      when(fdpaIncreasedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.empty());

      volumesService.saveFdpaIncreasedVolumes(getFormWithChange(), fdpaSubmissionDetail);

      // THEN oil only and shared probability distributions are saved
      verifyGasProbabilityDistributionsSaved();
      verifySharedProbabilityDistributionsSaved();

      // AND oil only probability distributions are deleted
      verify(probabilityDistributionPersistenceService, times(1))
          .deleteProbabilityDistributions(
              List.of(
                  ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA,
                  ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA,
                  ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA
              ),
              fdpaSubmissionDetail
          );
    }

    @Test
    void whenCombinedCase() {
      // WHEN combined case
      when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(fdpaSubmissionDetail))
          .thenReturn(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
      when(fdpaIncreasedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.empty());

      volumesService.saveFdpaIncreasedVolumes(getFormWithChange(), fdpaSubmissionDetail);

      // THEN oil only, gas only and shared probability distributions are saved
      verifyOilProbabilityDistributionsSaved();
      verifyGasProbabilityDistributionsSaved();
      verifySharedProbabilityDistributionsSaved();

      // AND nothing is deleted
      verify(probabilityDistributionPersistenceService, never())
          .deleteProbabilityDistribution(any(), any(SubmissionDetail.class));
      verify(probabilityDistributionPersistenceService, never())
          .deleteProbabilityDistributions(any(), any(SubmissionDetail.class));
    }

    @Test
    void whenNoChangeInVolumetrics() {
      var increasedVolume = getIncreasedVolumeWithChange();
      when(fdpaIncreasedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.of(increasedVolume));

      volumesService.saveFdpaIncreasedVolumes(getFormWithoutChange(), fdpaSubmissionDetail);

      verify(probabilityDistributionPersistenceService, times(1))
          .deleteProbabilityDistributions(ALL_FIELD_INSTANCES, fdpaSubmissionDetail);
      verify(fdpaIncreasedVolumesRepository, times(1))
          .save(increasedVolumeArgumentCaptor.capture());

      assertThat(increasedVolumeArgumentCaptor.getValue())
          .extracting(
              FieldDevelopmentPlanAddendumIncreasedVolume::getSubmissionDetail,
              FieldDevelopmentPlanAddendumIncreasedVolume::getChangeInVolumetrics,
              FieldDevelopmentPlanAddendumIncreasedVolume::getComment
          )
          .containsExactly(
              fdpaSubmissionDetail,
              false,
              null
          );
    }

    private FieldDevelopmentPlanAddendumIncreasedVolumesForm getFormWithoutChange() {
      var form = new FieldDevelopmentPlanAddendumIncreasedVolumesForm();
      form.setChangeInVolumetrics(false);
      return form;
    }

    private FieldDevelopmentPlanAddendumIncreasedVolumesForm getFormWithChange() {
      var form = new FieldDevelopmentPlanAddendumIncreasedVolumesForm();
      form.setChangeInVolumetrics(true);

      // oil only
      form.setOilInitiallyInPlace(OIL_INITIALLY_IN_PLACE_FORM());
      form.setOilRecoverableResources(OIL_RECOVERABLE_RESOURCES_FORM());
      form.setOilReserves(OIL_RESERVES_FORM());

      //gas only
      form.setGasInitiallyInPlace(GAS_INITIALLY_IN_PLACE_FORM());

      form.setAssociatedCondensateRecoverableResources(ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FORM());
      form.setAssociatedCondensateReserves(ASSOCIATED_CONDENSATE_RESERVES_FORM());
      form.setNaturalGasLiquidsRecoverableResources(NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_FORM());
      form.setNaturalGasLiquidsReserves(NATURAL_GAS_LIQUIDS_RESERVES_FORM());
      form.setSalesGasRecoverableResources(SALES_GAS_RECOVERABLE_RESOURCES_FORM());
      form.setSalesGasReserves(SALES_GAS_RESERVES_FORM());
      form.setOilEquivalentRecoverableResources(OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FORM());
      form.setOilEquivalentReserves(OIL_EQUIVALENT_RESERVES_FORM());

      form.setComment(COMMENT);

      return form;
    }

    private FieldDevelopmentPlanAddendumIncreasedVolume getIncreasedVolumeWithChange() {
      var increasedVolume = new FieldDevelopmentPlanAddendumIncreasedVolume();
      increasedVolume.setChangeInVolumetrics(true);
      increasedVolume.setComment(COMMENT);
      increasedVolume.setSubmissionDetail(fdpaSubmissionDetail);
      return increasedVolume;
    }

    private void verifyOilProbabilityDistributionsSaved() {
      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(OIL_INITIALLY_IN_PLACE_FORM());

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(OIL_RECOVERABLE_RESOURCES_FORM());

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(OIL_RESERVES_FORM());
    }

    private void verifyGasProbabilityDistributionsSaved() {
      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(GAS_INITIALLY_IN_PLACE_FORM());
    }

    private void verifySharedProbabilityDistributionsSaved() {
      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FORM());

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(ASSOCIATED_CONDENSATE_RESERVES_FORM());

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_FORM());

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(NATURAL_GAS_LIQUIDS_RESERVES_FORM());

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(SALES_GAS_RECOVERABLE_RESOURCES_FORM());

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(SALES_GAS_RESERVES_FORM());

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FORM());

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(OIL_EQUIVALENT_RESERVES_FORM());
    }
  }

  private static ProbabilityDistributionForm OIL_INITIALLY_IN_PLACE_FORM() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm GAS_INITIALLY_IN_PLACE_FORM() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm OIL_RECOVERABLE_RESOURCES_FORM() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm OIL_RESERVES_FORM() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FORM() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm ASSOCIATED_CONDENSATE_RESERVES_FORM() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_FORM() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm NATURAL_GAS_LIQUIDS_RESERVES_FORM() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm SALES_GAS_RECOVERABLE_RESOURCES_FORM() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm SALES_GAS_RESERVES_FORM() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FORM() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm OIL_EQUIVALENT_RESERVES_FORM() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA);
  }
}