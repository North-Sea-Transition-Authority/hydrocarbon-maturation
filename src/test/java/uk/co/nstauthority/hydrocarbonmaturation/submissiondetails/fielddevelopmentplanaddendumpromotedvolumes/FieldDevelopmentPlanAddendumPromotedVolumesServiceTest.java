package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fielddevelopmentplanaddendumpromotedvolumes;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;


@ExtendWith(MockitoExtension.class)
class FieldDevelopmentPlanAddendumPromotedVolumesServiceTest {

  private static final String COMMENT = "some comment";

  @Mock
  private FieldDevelopmentPlanAddendumPromotedVolumesRepository fdpaPromotedVolumesRepository;

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @InjectMocks
  private FieldDevelopmentPlanAddendumPromotedVolumesService volumesService;

  private SubmissionDetail fdpaSubmissionDetail;

  @BeforeEach
  void setUp() {
    fdpaSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDPA);
  }

  @Nested
  class SubmissionDetailToForm {
    @Test
    void oilCase_hasPromotedVolumes() {
      var promotedVolume = fdpaPromotedVolumeWithPromotedVolumes(fdpaSubmissionDetail);
      when(fdpaPromotedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.of(promotedVolume));

      mockOilOnlyPopulatedProbabilityDistributions(fdpaSubmissionDetail);

      var generatedForm = volumesService.submissionDetailToForm(fdpaSubmissionDetail);
      assertThat(generatedForm)
          .extracting(
              FieldDevelopmentPlanAddendumPromotedVolumesForm::getHasPromotedVolumes,
              FieldDevelopmentPlanAddendumPromotedVolumesForm::getComment
          )
          .containsExactly(
              Boolean.TRUE,
              COMMENT
          );

      assertProbabilityDistributionsWithOil(generatedForm);
    }

    @Test
    void noOilReserves_hasPromotedVolumes() {
      var promotedVolume = fdpaPromotedVolumeWithPromotedVolumes(fdpaSubmissionDetail);
      when(fdpaPromotedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.of(promotedVolume));

      mockPopulatedProbabilityDistributionsWithoutOilReserves(fdpaSubmissionDetail);

      var generatedForm = volumesService.submissionDetailToForm(fdpaSubmissionDetail);
      assertThat(generatedForm)
          .extracting(
              FieldDevelopmentPlanAddendumPromotedVolumesForm::getHasPromotedVolumes,
              FieldDevelopmentPlanAddendumPromotedVolumesForm::getComment
          )
          .containsExactly(
              Boolean.TRUE,
              COMMENT
          );

      assertProbabilityDistributionsWithoutOil(generatedForm);
    }

    @Test
    void noPromotedVolumes() {
      var promotedVolume = fdpaPromotedVolumeWithoutPromotedVolumes(fdpaSubmissionDetail);
      when(fdpaPromotedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.of(promotedVolume));

      var generatedForm = volumesService.submissionDetailToForm(fdpaSubmissionDetail);

      assertThat(generatedForm)
          .extracting(
              FieldDevelopmentPlanAddendumPromotedVolumesForm::getHasPromotedVolumes,
              FieldDevelopmentPlanAddendumPromotedVolumesForm::getComment
          )
          .containsExactly(
              Boolean.FALSE,
              null
          );

      assertEmptyProbabilityDistributions(generatedForm);
    }
  }

  @Nested
  class SaveFdpaPromotedVolumes {

    private ArgumentCaptor<FieldDevelopmentPlanAddendumPromotedVolume> fdpaArgumentCaptor = ArgumentCaptor.forClass(
        FieldDevelopmentPlanAddendumPromotedVolume.class);

    private ArgumentCaptor<ProbabilityDistributionForm> probabilityArgumentCaptor = ArgumentCaptor.forClass(
        ProbabilityDistributionForm.class);

    @Test
    void hasPromotedVolumes_withOilReserves() {
      // WHEN oil hydrocarbons
      when(reservoirHydrocarbonCaseService.isOilCaseOnly(fdpaSubmissionDetail))
          .thenReturn(true);
      when(fdpaPromotedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.of(new FieldDevelopmentPlanAddendumPromotedVolume()));

      volumesService.saveFdpaPromotedVolumes(getFdpaPromotedVolumesForm(Boolean.TRUE), fdpaSubmissionDetail);

      verify(fdpaPromotedVolumesRepository, times(1))
          .save(fdpaArgumentCaptor.capture());

      assertThat(fdpaArgumentCaptor.getValue())
          .extracting(
              FieldDevelopmentPlanAddendumPromotedVolume::getResourcesAreBeingPromoted,
              FieldDevelopmentPlanAddendumPromotedVolume::getSubmissionDetail,
              FieldDevelopmentPlanAddendumPromotedVolume::getComment
          )
          .containsExactly(
              Boolean.TRUE,
              fdpaSubmissionDetail,
              COMMENT
          );

      // THEN oil reserves are saved
      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(validOilReservesForm());

      // AND all other probability distributions
      verifyProbabilityDistributionSharedMethodCalls();
    }

    @Test
    void hasPromotedVolumes_noOilReserves() {
      // WHEN not only oil hydrocarbons
      when(reservoirHydrocarbonCaseService.isOilCaseOnly(fdpaSubmissionDetail))
          .thenReturn(false);
      when(fdpaPromotedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.of(new FieldDevelopmentPlanAddendumPromotedVolume()));

      volumesService.saveFdpaPromotedVolumes(getFdpaPromotedVolumesForm(Boolean.TRUE), fdpaSubmissionDetail);

      verify(fdpaPromotedVolumesRepository, times(1))
          .save(fdpaArgumentCaptor.capture());

      assertThat(fdpaArgumentCaptor.getValue())
          .extracting(
              FieldDevelopmentPlanAddendumPromotedVolume::getResourcesAreBeingPromoted,
              FieldDevelopmentPlanAddendumPromotedVolume::getSubmissionDetail,
              FieldDevelopmentPlanAddendumPromotedVolume::getComment
          )
          .containsExactly(
              Boolean.TRUE,
              fdpaSubmissionDetail,
              COMMENT
          );

      // THEN no oil reserves are saved
      verify(probabilityDistributionPersistenceService, never())
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA),
              any(),
              any(SubmissionDetail.class)
          );

      // AND old oil reserve probabilities are deleted
      verify(probabilityDistributionPersistenceService, times(1))
          .deleteProbabilityDistribution(
              ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA,
              fdpaSubmissionDetail
          );

      // AND all other probability distributions are saved
      verifyProbabilityDistributionSharedMethodCalls();
    }

    @Test
    void noPromotedVolumes() {
      // WHEN there are no promoted volumes
      when(fdpaPromotedVolumesRepository.findBySubmissionDetail(fdpaSubmissionDetail))
          .thenReturn(Optional.of(new FieldDevelopmentPlanAddendumPromotedVolume()));

      volumesService.saveFdpaPromotedVolumes(getFdpaPromotedVolumesForm(Boolean.FALSE), fdpaSubmissionDetail);

      verify(fdpaPromotedVolumesRepository, times(1))
          .save(fdpaArgumentCaptor.capture());

      assertThat(fdpaArgumentCaptor.getValue())
          .extracting(
              FieldDevelopmentPlanAddendumPromotedVolume::getResourcesAreBeingPromoted,
              FieldDevelopmentPlanAddendumPromotedVolume::getSubmissionDetail,
              FieldDevelopmentPlanAddendumPromotedVolume::getComment
          )
          .containsExactly(
              Boolean.FALSE,
              fdpaSubmissionDetail,
              null
          );

      // THEN no probability distributions are saved
      verify(probabilityDistributionPersistenceService, never())
          .saveProbabilityDistributionWhenNotNull(
              any(),
              any(),
              any(SubmissionDetail.class)
          );

      // AND old probabilities are deleted
      verify(probabilityDistributionPersistenceService, times(1))
          .deleteProbabilityDistributions(
              List.of(
                  ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA,
                  ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA,
                  ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA,
                  ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA,
                  ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA
              ),
              fdpaSubmissionDetail
          );
    }

    private void verifyProbabilityDistributionSharedMethodCalls() {
      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(validAssociatedCondensateReservesForm());

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(validNaturalGasLiquidsReservesForm());

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(validSalesGasReservesForm());

      verify(probabilityDistributionPersistenceService, times(1))
          .saveProbabilityDistributionWhenNotNull(
              eq(ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA),
              probabilityArgumentCaptor.capture(),
              eq(fdpaSubmissionDetail)
          );
      assertThat(probabilityArgumentCaptor.getValue())
          .usingRecursiveComparison()
          .isEqualTo(validOilEquivalentReservesForm());
    }
  }

  private void assertEmptyProbabilityDistributions(FieldDevelopmentPlanAddendumPromotedVolumesForm generatedForm) {
    assertThat(generatedForm.getOilReserves().getCustom())
        .extracting(
            form -> form.getOneP().createBigDecimalOrNull(),
            form -> form.getTwoP().createBigDecimalOrNull(),
            form -> form.getThreeP().createBigDecimalOrNull()
        )
        .containsExactly(
            null,
            null,
            null
        );

    assertThat(generatedForm.getSalesGasReserves().getCustom())
        .extracting(
            form -> form.getOneP().createBigDecimalOrNull(),
            form -> form.getTwoP().createBigDecimalOrNull(),
            form -> form.getThreeP().createBigDecimalOrNull()
        )
        .containsExactly(
            null,
            null,
            null
        );

    assertThat(generatedForm.getOilEquivalentReserves().getCustom())
        .extracting(
            form -> form.getOneP().createBigDecimalOrNull(),
            form -> form.getTwoP().createBigDecimalOrNull(),
            form -> form.getThreeP().createBigDecimalOrNull()
        )
        .containsExactly(
            null,
            null,
            null
        );

    assertThat(generatedForm.getAssociatedCondensateReserves().getCustom())
        .extracting(
            form -> form.getOneP().createBigDecimalOrNull(),
            form -> form.getTwoP().createBigDecimalOrNull(),
            form -> form.getThreeP().createBigDecimalOrNull()
        )
        .containsExactly(
            null,
            null,
            null
        );

    assertThat(generatedForm.getNaturalGasLiquidsReserves().getCustom())
        .extracting(
            form -> form.getOneP().createBigDecimalOrNull(),
            form -> form.getTwoP().createBigDecimalOrNull(),
            form -> form.getThreeP().createBigDecimalOrNull()
        )
        .containsExactly(
            null,
            null,
            null
        );

  }

  private void assertProbabilityDistributionsWithoutOil(FieldDevelopmentPlanAddendumPromotedVolumesForm generatedForm) {
    assertThat(generatedForm.getOilReserves().getCustom())
        .extracting(
            form -> form.getOneP().createBigDecimalOrNull(),
            form -> form.getTwoP().createBigDecimalOrNull(),
            form -> form.getThreeP().createBigDecimalOrNull()
        )
        .containsExactly(
            null,
            null,
            null
        );

    assertSharedProbabilityDistributions(generatedForm);
  }

  private void assertProbabilityDistributionsWithOil(FieldDevelopmentPlanAddendumPromotedVolumesForm generatedForm) {
    assertThat(generatedForm.getOilReserves())
        .usingRecursiveComparison()
        .isEqualTo(validOilReservesForm());
    assertSharedProbabilityDistributions(generatedForm);
  }

  private void assertSharedProbabilityDistributions(FieldDevelopmentPlanAddendumPromotedVolumesForm generatedForm) {
    assertThat(generatedForm.getAssociatedCondensateReserves())
        .usingRecursiveComparison()
        .isEqualTo(validAssociatedCondensateReservesForm());
    assertThat(generatedForm.getNaturalGasLiquidsReserves())
        .usingRecursiveComparison()
        .isEqualTo(validNaturalGasLiquidsReservesForm());
    assertThat(generatedForm.getSalesGasReserves())
        .usingRecursiveComparison()
        .isEqualTo(validSalesGasReservesForm());
    assertThat(generatedForm.getOilEquivalentReserves())
        .usingRecursiveComparison()
        .isEqualTo(validOilEquivalentReservesForm());
  }

  private static FieldDevelopmentPlanAddendumPromotedVolume fdpaPromotedVolumeWithPromotedVolumes(
      SubmissionDetail submissionDetail) {
    var promotedVolume = baseFdpaPromotedVolume(submissionDetail);
    promotedVolume.setResourcesAreBeingPromoted(Boolean.TRUE);
    return promotedVolume;
  }

  private static FieldDevelopmentPlanAddendumPromotedVolume fdpaPromotedVolumeWithoutPromotedVolumes(
      SubmissionDetail submissionDetail) {
    var promotedVolume = baseFdpaPromotedVolume(submissionDetail);
    promotedVolume.setResourcesAreBeingPromoted(Boolean.FALSE);
    return promotedVolume;
  }

  private static FieldDevelopmentPlanAddendumPromotedVolume baseFdpaPromotedVolume(SubmissionDetail submissionDetail) {
    var promotedVolume = new FieldDevelopmentPlanAddendumPromotedVolume();
    promotedVolume.setId(UUID.randomUUID());
    promotedVolume.setComment(COMMENT);
    promotedVolume.setSubmissionDetail(submissionDetail);
    return promotedVolume;
  }

  private void mockOilOnlyPopulatedProbabilityDistributions(SubmissionDetail submissionDetail) {
    when(reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail))
        .thenReturn(true);
    mockPopulatedProbabilityDistributions(Boolean.TRUE, submissionDetail);
  }

  private void mockPopulatedProbabilityDistributionsWithoutOilReserves(SubmissionDetail submissionDetail) {
    when(reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail))
        .thenReturn(false);
    mockPopulatedProbabilityDistributions(Boolean.FALSE, submissionDetail);
  }

  private void mockPopulatedProbabilityDistributions(Boolean oilOnly, SubmissionDetail submissionDetail) {
    if (BooleanUtils.isTrue(oilOnly)) {
      when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail, ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA))
          .thenReturn(validOilReservesForm());
    }

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA))
        .thenReturn(validAssociatedCondensateReservesForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA))
        .thenReturn(validNaturalGasLiquidsReservesForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA))
        .thenReturn(validSalesGasReservesForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA))
        .thenReturn(validOilEquivalentReservesForm());
  }

  private static FieldDevelopmentPlanAddendumPromotedVolumesForm getFdpaPromotedVolumesForm(
      Boolean hasPromotedVolumes) {
    var form = new FieldDevelopmentPlanAddendumPromotedVolumesForm();
    form.setHasPromotedVolumes(hasPromotedVolumes);
    form.setComment(COMMENT);

    form.setOilReserves(validOilReservesForm());
    form.setSalesGasReserves(validSalesGasReservesForm());
    form.setNaturalGasLiquidsReserves(validNaturalGasLiquidsReservesForm());
    form.setAssociatedCondensateReserves(validAssociatedCondensateReservesForm());
    form.setOilEquivalentReserves(validOilEquivalentReservesForm());

    return form;
  }

  private static ProbabilityDistributionForm validOilReservesForm() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm validAssociatedCondensateReservesForm() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm validNaturalGasLiquidsReservesForm() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm validSalesGasReservesForm() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA);
  }

  private static ProbabilityDistributionForm validOilEquivalentReservesForm() {
    return ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm(
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA);
  }
}