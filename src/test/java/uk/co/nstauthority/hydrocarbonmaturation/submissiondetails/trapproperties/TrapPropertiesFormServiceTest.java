package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class TrapPropertiesFormServiceTest {

  private static TrapPropertiesFormValidator trapPropertiesFormValidator;
  private static TrapPropertiesFormService trapPropertiesFormService;
  private static TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private static ProbabilityDistributionFormService probabilityDistributionFormService;
  private static SupportingDocumentService supportingDocumentService;
  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    trapPropertiesFormValidator = mock(TrapPropertiesFormValidator.class);
    trapPropertiesPersistenceService = mock(TrapPropertiesPersistenceService.class);
    probabilityDistributionFormService = mock(ProbabilityDistributionFormService.class);
    supportingDocumentService = mock(SupportingDocumentService.class);
    trapPropertiesFormService = new TrapPropertiesFormService(
        trapPropertiesFormValidator,
        trapPropertiesPersistenceService,
        probabilityDistributionFormService,
        supportingDocumentService);

    UUID submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new TrapPropertiesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    trapPropertiesFormService.validate(form, bindingResult, ValidationType.FULL);
    verify(trapPropertiesFormValidator, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void trapPropertiesEntityToForm_newEntity_thenEmptyForm() {
    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail)).thenReturn(Optional.empty());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT_DISCOVERY))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT_DISCOVERY))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA))
        .thenReturn(new ProbabilityDistributionForm());
    assertThat(trapPropertiesFormService.getTrapPropertiesForm(submissionDetail))
        .extracting(
            TrapPropertiesForm::getTrapType,
            TrapPropertiesForm::getOtherTrapType,
            TrapPropertiesForm::getTrapDescription,
            form -> form.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLongitudeHemisphere(),
            form -> form.getDepthToCrest().getProbabilityDistributionType(),
            form -> form.getDepthToSpillPoint().getProbabilityDistributionType(),
            form -> form.getProductiveArea().getProbabilityDistributionType()
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void trapPropertiesEntityToForm_existingEntity_thenPopulatedForm() {
    var trapProperty = getTrapProperty(submissionDetail);
    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail)).thenReturn(Optional.of(trapProperty));
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm();
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT_DISCOVERY))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA))
        .thenReturn(probabilityDistributionForm);
    assertThat(trapPropertiesFormService.getTrapPropertiesForm(submissionDetail))
        .extracting(
            TrapPropertiesForm::getTrapType,
            TrapPropertiesForm::getOtherTrapType,
            TrapPropertiesForm::getTrapDescription,
            form -> form.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLongitudeHemisphere(),
            form -> form.getDepthToCrest().getProbabilityDistributionType(),
            form -> form.getDepthToCrest().getNormal().getMin(),
            form -> form.getDepthToCrest().getNormal().getP99(),
            form -> form.getDepthToCrest().getNormal().getP90(),
            form -> form.getDepthToCrest().getNormal().getMode(),
            form -> form.getDepthToCrest().getNormal().getP50(),
            form -> form.getDepthToCrest().getNormal().getMean(),
            form -> form.getDepthToCrest().getNormal().getP10(),
            form -> form.getDepthToCrest().getNormal().getP1(),
            form -> form.getDepthToCrest().getNormal().getMax(),
            form -> form.getDepthToCrest().getNormal().getConstant()
        )
        .containsExactly(
            trapProperty.getTrapType(),
            trapProperty.getOtherTrapType(),
            trapProperty.getTrapDescription(),
            trapProperty.getLatitudeDegrees(),
            trapProperty.getLatitudeMinutes(),
            trapProperty.getLatitudeSeconds(),
            trapProperty.getLongitudeDegrees(),
            trapProperty.getLongitudeMinutes(),
            trapProperty.getLongitudeSeconds(),
            trapProperty.getLongitudeHemisphere(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getNormal().getMin(),
            probabilityDistributionForm.getNormal().getP99(),
            probabilityDistributionForm.getNormal().getP90(),
            probabilityDistributionForm.getNormal().getMode(),
            probabilityDistributionForm.getNormal().getP50(),
            probabilityDistributionForm.getNormal().getMean(),
            probabilityDistributionForm.getNormal().getP10(),
            probabilityDistributionForm.getNormal().getP1(),
            probabilityDistributionForm.getNormal().getMax(),
            probabilityDistributionForm.getNormal().getConstant()
        );
  }

  @Test
  void getProspectDepthMapUploads_VerifyCalls() {
    FileUploadForm fileUploadForm1 = getFileUploadForm();
    FileUploadForm fileUploadForm2 = getFileUploadForm();

    var form = new TrapPropertiesForm();
    form.setProspectDepthMap(List.of(fileUploadForm1, fileUploadForm2));

    trapPropertiesFormService.getProspectDepthMapUploads(form);

    verify(supportingDocumentService, times(1)).getUploadedFileViewList(anyList());
  }


  private TrapProperty getTrapProperty(SubmissionDetail submissionDetail) {
    var trapProperty = new TrapProperty();
    trapProperty.setSubmissionDetail(submissionDetail);
    trapProperty.setTrapType(TrapType.COMBINATION);
    trapProperty.setTrapDescription("description");
    trapProperty.setLatitudeDegrees(BigDecimal.valueOf(45));
    trapProperty.setLatitudeMinutes(BigDecimal.valueOf(4));
    trapProperty.setLatitudeSeconds(BigDecimal.valueOf(14));
    trapProperty.setLongitudeDegrees(BigDecimal.valueOf(5));
    trapProperty.setLongitudeMinutes(BigDecimal.valueOf(6));
    trapProperty.setLongitudeSeconds(BigDecimal.valueOf(40));
    trapProperty.setLongitudeHemisphere(Hemisphere.EAST);

    return trapProperty;
  }

  private FileUploadForm getFileUploadForm() {
    FileUploadForm fileUploadForm = new FileUploadForm();
    fileUploadForm.setUploadedFileId(UUID.randomUUID());
    return fileUploadForm;
  }
}