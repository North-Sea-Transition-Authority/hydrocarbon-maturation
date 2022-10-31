package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock.ReferenceBlockSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock.ReferenceBlockView;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailId;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsFormServiceTest {

  private static final String LICENCE_APPLICATION_REF = "F33/1234";
  private static final String LICENCE_APPLICATION_ID = "1";

  private static GeneralDetailsFormValidator generalDetailsFormValidator;
  private static GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private static GeneralDetailsFormService generalDetailsFormService;
  private static SubmissionDetailService submissionDetailService;
  private static QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private static ReferenceBlockSearchService referenceBlockSearchService;

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    generalDetailsFormValidator = mock(GeneralDetailsFormValidator.class);
    generalDetailsPersistenceService = mock(GeneralDetailsPersistenceService.class);
    submissionDetailService = mock(SubmissionDetailService.class);
    quadrantAndBlockPersistenceService = mock(QuadrantAndBlockPersistenceService.class);
    referenceBlockSearchService = mock(ReferenceBlockSearchService.class);
    SearchSelectorService searchSelectorService = new SearchSelectorService();
    generalDetailsFormService = new GeneralDetailsFormService(
        generalDetailsFormValidator,
        generalDetailsPersistenceService,
        searchSelectorService,
        submissionDetailService,
        quadrantAndBlockPersistenceService,
        referenceBlockSearchService
    );
    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new GeneralDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var validationType = ValidationType.FULL;
    generalDetailsFormService.validate(form, bindingResult, validationType);
    verify(generalDetailsFormValidator, times(1)).validate(form, bindingResult, validationType);
  }

  @Test
  void generalDetailEntityToForm_newEntity_thenEmptyForm() {
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.empty());

    var form = generalDetailsFormService.getGeneralDetailsForm(submissionDetail);
    assertThat(form).hasAllNullFieldsOrPropertiesExcept("quadrantBlocks");
    assertThat(form.getQuadrantBlocks()).isEmpty();
  }

  @Test
  void generalDetailEntityToForm_existingEntity_thenPopulatedForm() {
    var generalDetail = getGeneralDetail(submissionDetail);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(generalDetail));

    var quadrantBlockRef = "27/10";
    var quadrantAndBlock = createQuadrantAndBlock(generalDetail, quadrantBlockRef);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail)).thenReturn(List.of(quadrantAndBlock));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail))
        .extracting(
            GeneralDetailsForm::getProspectName,
            GeneralDetailsForm::getOnshoreOffshore,
            GeneralDetailsForm::getProspectLocatedInUkcs,
            GeneralDetailsForm::getPercentageLocatedInUkcs,
            GeneralDetailsForm::getInformationSource,
            GeneralDetailsForm::getOtherSourceOfInformation,
            GeneralDetailsForm::getQuadrantBlocks
        )
        .containsExactly(
            generalDetail.getProspectName(),
            generalDetail.getOnshoreOffshore(),
            generalDetail.getProspectLocatedInUkcs(),
            generalDetail.getPercentageLocatedInUkcs(),
            generalDetail.getInformationSource(),
            generalDetail.getOtherInformationSource(),
            List.of(quadrantBlockRef)
        );
  }

  @Test
  void getQuadrantAndBlockViews_FormHasNoQuadBlocks_ReturnsEmptyList() {
    var form = new GeneralDetailsForm();

    var quadrantAndBlockViews = generalDetailsFormService.getQuadrantAndBlockViews(form);

    assertThat(quadrantAndBlockViews).isEmpty();
  }

  @Test
  void getQuadrantAndBlockViews_FormHasQuadBlock_ReturnsQuadrantAndBlockViewList() {
    var qbRef = "27/10";
    var qbRefList = List.of(qbRef);
    when(referenceBlockSearchService.getReferenceBlocksByRefsIn(qbRefList))
        .thenReturn(Collections.singletonList(new ReferenceBlockView(qbRef, qbRef)));

    var form = new GeneralDetailsForm();
    form.setQuadrantBlocks(qbRefList);

    var quadrantAndBlockViews = generalDetailsFormService.getQuadrantAndBlockViews(form);

    assertThat(quadrantAndBlockViews)
        .extracting(
            QuadrantAndBlockView::getId,
            QuadrantAndBlockView::getName
        )
        .containsExactly(
            tuple(qbRef, qbRef)
        );
  }

  private QuadrantAndBlock createQuadrantAndBlock(GeneralDetail generalDetail, String quadrantBlockRef) {
    var quadrantAndBlock = new QuadrantAndBlock();
    quadrantAndBlock.setGeneralDetail(generalDetail);
    quadrantAndBlock.setQuadrantBlock(quadrantBlockRef);
    return quadrantAndBlock;
  }

  @Test
  void generalDetailEntityToForm_existingEntity_withLicenceApplicationReferenceOnly_thenPopulatedForm() {
    var generalDetail = getGeneralDetail(submissionDetail);
    generalDetail.setLicenceApplicationReference(LICENCE_APPLICATION_REF);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(generalDetail));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail).getLicenceApplication())
        .isEqualTo(SearchSelectorService.getValueWithManualEntryPrefix(LICENCE_APPLICATION_REF));
  }

  @Test
  void generalDetailEntityToForm_existingEntity_withLicenceApplicationId_thenPopulatedForm() {
    var generalDetail = getGeneralDetail(submissionDetail);
    generalDetail.setLicenceApplicationReference(LICENCE_APPLICATION_REF);
    generalDetail.setLicenceApplicationId(Integer.parseInt(LICENCE_APPLICATION_ID));
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(generalDetail));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail).getLicenceApplication())
        .isEqualTo(LICENCE_APPLICATION_ID);
  }

  @Test
  void getPreselectedLicenceApplication_withNoLicenceApplication() {
    assertThat(generalDetailsFormService.getPreSelectedLicenceApplication(
        null,
        SubmissionDetailId.fromSubmissionDetailId(submissionDetail.getId()))
    ).isEmpty();
  }

  @Test
  void getPreselectedLicenceApplication_withLicenceApplicationReferenceOnly() {
    var manualLicenceRef = SearchSelectorService.getValueWithManualEntryPrefix(LICENCE_APPLICATION_REF);

    assertThat(generalDetailsFormService.getPreSelectedLicenceApplication(manualLicenceRef, SubmissionDetailId.fromSubmissionDetailId(submissionDetail.getId())))
        .containsEntry(manualLicenceRef, LICENCE_APPLICATION_REF);
  }

  @Test
  void getPreselectedLicenceApplication_withLicenceApplicationId() {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
        .thenReturn(submissionDetail);
    var generalDetail = getGeneralDetail(submissionDetail);
    generalDetail.setLicenceApplicationId(Integer.parseInt(LICENCE_APPLICATION_ID));
    generalDetail.setLicenceApplicationReference(LICENCE_APPLICATION_REF);
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(submissionDetail))
        .thenReturn(generalDetail);

    assertThat(generalDetailsFormService.getPreSelectedLicenceApplication(LICENCE_APPLICATION_ID, SubmissionDetailId.fromSubmissionDetailId(submissionDetail.getId())))
        .containsEntry(LICENCE_APPLICATION_ID, LICENCE_APPLICATION_REF);
  }

  private GeneralDetail getGeneralDetail(SubmissionDetail submissionDetail) {
    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);
    generalDetail.setProspectName("prospect name");
    generalDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    generalDetail.setProspectLocatedInUkcs(true);
    generalDetail.setInformationSource(InformationSource.WELL_INVESTMENT_DECISION);

    return generalDetail;
  }
}