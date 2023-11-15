package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapType;

@ExtendWith(MockitoExtension.class)
class FieldTrapPropertiesFormServiceTest {

  @Mock
  private TrapPropertiesRepository trapPropertiesRepository;

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @InjectMocks
  private FieldTrapPropertiesFormService fieldTrapPropertiesFormService;

  @Test
  void submissionDetailToForm_existingTrapProperty() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    var probabilityDistributionForm = new ProbabilityDistributionForm();
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        eq(submissionDetail),
        any(ProbabilityDistributionFieldInstance.class))
    ).thenReturn(probabilityDistributionForm);

    var existingTrapProperty = new TrapProperty();
    existingTrapProperty.setTrapType(TrapType.COMBINATION);
    existingTrapProperty.setClosureMechanisms("test");
    existingTrapProperty.setVerticallySeparateHcPools(BigDecimal.valueOf(1));
    existingTrapProperty.setStructuralCompartments(BigDecimal.valueOf(2));
    existingTrapProperty.setOriginalHydrocarbonColumnHeight(BigDecimal.valueOf(3));
    when(trapPropertiesRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(existingTrapProperty));

    var form = fieldTrapPropertiesFormService.submissionDetailToForm(submissionDetail);

    assertThat(form)
        .extracting(
            FieldTrapPropertiesForm::getTrapType,
            FieldTrapPropertiesForm::getClosureMechanisms,
            fieldTrapPropertiesForm -> fieldTrapPropertiesForm.getVerticallySeparateHcPools().getDecimalInputValue(),
            fieldTrapPropertiesForm1 -> fieldTrapPropertiesForm1.getStructuralCompartments().getDecimalInputValue(),
            fieldTrapPropertiesForm2 -> fieldTrapPropertiesForm2.getOriginalHydrocarbonColumnHeight().getDecimalInputValue()
        ).containsExactly(
            existingTrapProperty.getTrapType(),
            existingTrapProperty.getClosureMechanisms(),
            DecimalInput.createDecimalInput(existingTrapProperty.getVerticallySeparateHcPools()).getDecimalInputValue(),
            DecimalInput.createDecimalInput(existingTrapProperty.getStructuralCompartments()).getDecimalInputValue(),
            DecimalInput.createDecimalInput(existingTrapProperty.getOriginalHydrocarbonColumnHeight()).getDecimalInputValue()
        );

    assertThat(form)
        .extracting(
            FieldTrapPropertiesForm::getDepthToCrest,
            FieldTrapPropertiesForm::getDepthToSpillPoint,
            FieldTrapPropertiesForm::getProductiveArea
        ).containsOnly(probabilityDistributionForm);
  }

  @Test
  void submissionDetailToForm_noExistingTrapProperty() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    when(trapPropertiesRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var form = fieldTrapPropertiesFormService.submissionDetailToForm(submissionDetail);

    assertThat(form)
        .extracting(
            FieldTrapPropertiesForm::getTrapType,
            FieldTrapPropertiesForm::getClosureMechanisms,

            fieldTrapPropertiesForm -> fieldTrapPropertiesForm.getVerticallySeparateHcPools().getDecimalInputValue(),
            fieldTrapPropertiesForm2 -> fieldTrapPropertiesForm2.getStructuralCompartments().getDecimalInputValue(),
            fieldTrapPropertiesForm1 -> fieldTrapPropertiesForm1.getOriginalHydrocarbonColumnHeight().getDecimalInputValue()
        ).containsOnlyNulls();
  }

  @Test
  void saveFieldTrapProperties_saveProbabilityDistributions() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
    var form = getForm();

    fieldTrapPropertiesFormService.saveFieldTrapProperties(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_FIELD,
        form.getDepthToCrest(),
        submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_FIELD,
        form.getDepthToSpillPoint(),
        submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1)).saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_FIELD,
        form.getProductiveArea(),
        submissionDetail);

    verifyNoMoreInteractions(probabilityDistributionPersistenceService);
  }

  @Test
  void saveFieldTrapProperties_noTrapPropertyExists() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    when(trapPropertiesRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var form = getForm();
    fieldTrapPropertiesFormService.saveFieldTrapProperties(form, submissionDetail);

    var captor = ArgumentCaptor.forClass(TrapProperty.class);
    verify(trapPropertiesRepository).save(captor.capture());

    var trapProperty = captor.getValue();
    assertThat(trapProperty)
        .extracting(
            TrapProperty::getTrapType,
            TrapProperty::getOtherTrapType,
            TrapProperty::getVerticallySeparateHcPools,
            TrapProperty::getStructuralCompartments,
            TrapProperty::getOriginalHydrocarbonColumnHeight
        ).containsExactly(
            form.getTrapType(),
            form.getOtherTrapType(),
            form.getVerticallySeparateHcPools().asBigDecimal().get(),
            form.getStructuralCompartments().asBigDecimal().get(),
            form.getOriginalHydrocarbonColumnHeight().asBigDecimal().get()
        );
  }

  @Test
  void saveFieldTrapProperties_nonOtherTrapType() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    when(trapPropertiesRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var form = getForm();
    form.setTrapType(TrapType.COMBINATION);
    form.setOtherTrapType("This should be cleared");

    fieldTrapPropertiesFormService.saveFieldTrapProperties(form, submissionDetail);

    var captor = ArgumentCaptor.forClass(TrapProperty.class);
    verify(trapPropertiesRepository).save(captor.capture());

    var trapProperty = captor.getValue();
    assertThat(trapProperty)
        .extracting(
            TrapProperty::getTrapType,
            TrapProperty::getOtherTrapType
        ).containsExactly(
            form.getTrapType(),
              null
        ).doesNotContain(form.getOtherTrapType());
  }

  @Test
  void saveFieldTrapProperties_otherTrapType() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);

    when(trapPropertiesRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var form = getForm();
    form.setTrapType(TrapType.OTHER);
    form.setOtherTrapType("This should not be cleared");

    fieldTrapPropertiesFormService.saveFieldTrapProperties(form, submissionDetail);

    var captor = ArgumentCaptor.forClass(TrapProperty.class);
    verify(trapPropertiesRepository).save(captor.capture());

    var trapProperty = captor.getValue();
    assertThat(trapProperty)
        .extracting(
            TrapProperty::getTrapType,
            TrapProperty::getOtherTrapType
        ).containsExactly(
            TrapType.OTHER,
            form.getOtherTrapType()
        );
  }

  private FieldTrapPropertiesForm getForm() {
    var form = new FieldTrapPropertiesForm();

    form.setTrapType(TrapType.COMBINATION);
    form.setClosureMechanisms("closure");
    form.setVerticallySeparateHcPools(DecimalInput.createDecimalInput(1));
    form.setStructuralCompartments(DecimalInput.createDecimalInput(1));
    form.setOriginalHydrocarbonColumnHeight(DecimalInput.createDecimalInput(1));

    return form;
  }
}