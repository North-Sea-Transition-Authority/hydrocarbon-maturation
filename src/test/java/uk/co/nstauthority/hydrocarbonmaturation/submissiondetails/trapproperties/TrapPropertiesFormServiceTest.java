package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static java.util.Map.entry;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlockPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class TrapPropertiesFormServiceTest {

  private static TrapPropertiesFormValidator trapPropertiesFormValidator;
  private static TrapPropertiesFormService trapPropertiesFormService;
  private static TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private static ProbabilityDistributionFormService probabilityDistributionFormService;
  private static SupportingDocumentService supportingDocumentService;
  private static QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private static GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    trapPropertiesFormValidator = mock(TrapPropertiesFormValidator.class);
    trapPropertiesPersistenceService = mock(TrapPropertiesPersistenceService.class);
    probabilityDistributionFormService = mock(ProbabilityDistributionFormService.class);
    supportingDocumentService = mock(SupportingDocumentService.class);
    generalDetailsPersistenceService = mock(GeneralDetailsPersistenceService.class);
    quadrantAndBlockPersistenceService = mock(QuadrantAndBlockPersistenceService.class);
    trapPropertiesFormService = new TrapPropertiesFormService(
        trapPropertiesFormValidator,
        trapPropertiesPersistenceService,
        probabilityDistributionFormService,
        quadrantAndBlockPersistenceService,
        generalDetailsPersistenceService,
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
    var fileUploadFormList = List.of(mock(FileUploadForm.class));
    when(supportingDocumentService.getFileUploadFormListForSubmissionAndType(submissionDetail, SupportingDocumentType.PROSPECT_DEPTH_MAP))
        .thenReturn(fileUploadFormList);
    var trapPropertiesForm = trapPropertiesFormService.getTrapPropertiesForm(submissionDetail);
    assertThat(trapPropertiesForm)
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
            TrapPropertiesForm::getDatum,
            TrapPropertiesForm::getCentreOfInterestQuadrantBlock,
            form -> form.getDepthToCrest().getProbabilityDistributionType(),
            form -> form.getDepthToSpillPoint().getProbabilityDistributionType(),
            form -> form.getProductiveArea().getProbabilityDistributionType())
        .containsOnlyNulls();

    assertThat(trapPropertiesForm.getProspectDepthMap()).isEqualTo(fileUploadFormList);
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
    var fileUploadFormList = List.of(mock(FileUploadForm.class));
    when(supportingDocumentService.getFileUploadFormListForSubmissionAndType(submissionDetail, SupportingDocumentType.PROSPECT_DEPTH_MAP))
        .thenReturn(fileUploadFormList);
    assertThat(trapPropertiesFormService.getTrapPropertiesForm(submissionDetail))
        .extracting(
            TrapPropertiesForm::getTrapType,
            TrapPropertiesForm::getOtherTrapType,
            TrapPropertiesForm::getTrapDescription,
            TrapPropertiesForm::getProspectDepthMap,
            form -> form.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form -> form.getCoordinateInputForm().getLongitudeHemisphere(),
            TrapPropertiesForm::getDatum,
            TrapPropertiesForm::getCentreOfInterestQuadrantBlock,
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
            form -> form.getDepthToCrest().getNormal().getConstant())
        .containsExactly(
            trapProperty.getTrapType(),
            trapProperty.getOtherTrapType(),
            trapProperty.getTrapDescription(),
            fileUploadFormList,
            trapProperty.getLatitudeDegrees(),
            trapProperty.getLatitudeMinutes(),
            trapProperty.getLatitudeSeconds(),
            trapProperty.getLongitudeDegrees(),
            trapProperty.getLongitudeMinutes(),
            trapProperty.getLongitudeSeconds(),
            trapProperty.getLongitudeHemisphere(),
            trapProperty.getDatum(),
            trapProperty.getCentreOfInterestQuadrantBlock(),
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
    var fileUploadForm1 = getFileUploadForm();
    var fileUploadForm2 = getFileUploadForm();

    var form = new TrapPropertiesForm();
    form.setProspectDepthMap(List.of(fileUploadForm1, fileUploadForm2));

    trapPropertiesFormService.getProspectDepthMapUploads(form);

    verify(supportingDocumentService, times(1)).getUploadedFileViewList(anyList());
  }

  @Test
  void getQuadrantAndBlocks() {
    var generalDetail = new GeneralDetail();
    var quadrantBlock = new QuadrantAndBlock();
    quadrantBlock.setQuadrantBlock("quad1");
    var blockId = UUID.randomUUID();
    quadrantBlock.setId(blockId);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(List.of(quadrantBlock));

    var quadrantBlocks = trapPropertiesFormService.getQuadrantAndBlocks(submissionDetail);
    assertThat(quadrantBlocks)
        .contains(
            entry("quad1", "quad1")
        );
  }

  @Test
  void getQuadrantAndBlocks_assertSorted() {
    var generalDetail = new GeneralDetail();
    var quadrantBlock = new QuadrantAndBlock();
    var quadrantBlock1 = new QuadrantAndBlock();
    var quadrantBlock2 = new QuadrantAndBlock();
    quadrantBlock1.setSortKey("0002");
    quadrantBlock1.setQuadrantBlock("quad1");
    quadrantBlock2.setQuadrantBlock("quad2");
    quadrantBlock2.setSortKey("0003");
    quadrantBlock.setSortKey("0001");
    quadrantBlock.setQuadrantBlock("quad");
    var blockId = UUID.randomUUID();
    quadrantBlock.setId(blockId);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(List.of(quadrantBlock2, quadrantBlock1, quadrantBlock));

    var quadrantBlocks = trapPropertiesFormService.getQuadrantAndBlocks(submissionDetail);
    assertThat(quadrantBlocks)
        .containsExactly(
            entry("quad", "quad"),
            entry("quad1", "quad1"),
            entry("quad2", "quad2")
        );
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
    trapProperty.setDatum(ReservoirDatum.ED50);
    trapProperty.setCentreOfInterestQuadrantBlock("10/15");

    return trapProperty;
  }

  private FileUploadForm getFileUploadForm() {
    var fileUploadForm = new FileUploadForm();
    fileUploadForm.setUploadedFileId(UUID.randomUUID());
    return fileUploadForm;
  }
}