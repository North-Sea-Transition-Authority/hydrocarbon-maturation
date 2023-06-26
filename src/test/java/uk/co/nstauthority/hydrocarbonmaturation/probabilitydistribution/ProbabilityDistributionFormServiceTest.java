package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class ProbabilityDistributionFormServiceTest {
  @Mock
  ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @InjectMocks
  ProbabilityDistributionFormService probabilityDistributionFormService;

  @Test
  void probabilityDistributionEntityToForm_NoSavedTypeAndInstanceIsPreset_TypeIsNull() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var instance = ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT;

    when(probabilityDistributionPersistenceService.getProbabilityDistribution(submissionDetail, instance))
        .thenReturn(Optional.empty());

    var form = probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, instance);

    assertThat(form.getProbabilityDistributionType()).isNull();
  }

  @Test
  void probabilityDistributionEntityToForm_NoSavedTypeAndInstanceIsCustom_TypeIsCustom() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var instance = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT;

    when(probabilityDistributionPersistenceService.getProbabilityDistribution(submissionDetail, instance))
        .thenReturn(Optional.empty());

    var form = probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, instance);

    assertThat(form.getProbabilityDistributionType()).isEqualTo(ProbabilityDistributionType.CUSTOM);
  }

  @Test
  void probabilityDistributionEntityToForm_HasSavedTypeAndInstanceIsPreset_TypeIsFromSaved() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var instance = ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT;

    var probabilityDistribution = new ProbabilityDistribution();
    probabilityDistribution.setDistributionType(ProbabilityDistributionType.NORMAL);
    when(probabilityDistributionPersistenceService.getProbabilityDistribution(submissionDetail, instance))
        .thenReturn(Optional.of(probabilityDistribution));

    var form = probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, instance);

    assertThat(form.getProbabilityDistributionType()).isEqualTo(ProbabilityDistributionType.NORMAL);
  }

  @Test
  void probabilityDistributionEntityToForm_HasSavedTypeAndInstanceIsCustom_TypeIsCustom() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var instance = ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT;

    var probabilityDistribution = new ProbabilityDistribution();
    when(probabilityDistributionPersistenceService.getProbabilityDistribution(submissionDetail, instance))
        .thenReturn(Optional.of(probabilityDistribution));

    var form = probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, instance);

    assertThat(form.getProbabilityDistributionType()).isEqualTo(ProbabilityDistributionType.CUSTOM);
  }

  @Test
  void probabilityDistributionEntityToForm_HasOnePresetTypeAndInstanceIsPreset_IsOnePresetType() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var instance = ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL;

    when(probabilityDistributionPersistenceService.getProbabilityDistribution(submissionDetail, instance))
        .thenReturn(Optional.empty());

    var form = probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, instance);

    assertThat(form.getProbabilityDistributionType()).isEqualTo(instance.getPresetTypes().get(0));
  }
}