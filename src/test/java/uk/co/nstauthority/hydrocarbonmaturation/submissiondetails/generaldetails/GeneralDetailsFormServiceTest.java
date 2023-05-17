package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockView;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsFormServiceTest {

  private static final String LICENCE_APPLICATION_REF = "F33/1234";
  private static final String LICENCE_APPLICATION_ID = "1";

  private static GeneralDetailsFormValidator generalDetailsFormValidator;
  private static GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private static GeneralDetailsFormService generalDetailsFormService;
  private static LicenceApplicationQueryService licenceApplicationQueryService;
  private static QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private static ReferenceBlockQueryService referenceBlockQueryService;
  private static LicencePersistenceService licencePersistenceService;
  private static LicenceQueryService licenceQueryService;
  private static SubmissionDetailService submissionDetailService;

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    generalDetailsFormValidator = mock(GeneralDetailsFormValidator.class);
    generalDetailsPersistenceService = mock(GeneralDetailsPersistenceService.class);
    quadrantAndBlockPersistenceService = mock(QuadrantAndBlockPersistenceService.class);
    referenceBlockQueryService = mock(ReferenceBlockQueryService.class);
    licenceApplicationQueryService = mock(LicenceApplicationQueryService.class);
    licencePersistenceService = mock(LicencePersistenceService.class);
    licenceQueryService = mock(LicenceQueryService.class);
    submissionDetailService = mock(SubmissionDetailService.class);

    generalDetailsFormService = new GeneralDetailsFormService(
        generalDetailsFormValidator,
        generalDetailsPersistenceService,
        licenceApplicationQueryService,
        quadrantAndBlockPersistenceService,
        referenceBlockQueryService,
        licenceQueryService,
        licencePersistenceService, submissionDetailService);
    var submissionDetailId = UUID.randomUUID();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_whenUpdate_whenNoLicence_verifyMethodCall() {
    var form = new GeneralDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var validationType = ValidationType.FULL;
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    var captor = ArgumentCaptor.forClass(
        GeneralDetailsFormValidatorHints.class);
    var latestGeneralDetail = new GeneralDetail();
    var mockLatestSubmission = mock(SubmissionDetail.class);
    when(submissionDetailService.getLatestApprovedSubmissionForSubmission(any()))
        .thenReturn(Optional.of(mockLatestSubmission));
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(mockLatestSubmission))
        .thenReturn(Optional.of(latestGeneralDetail));
    when(licencePersistenceService.getLicencesByGeneralDetail(latestGeneralDetail))
        .thenReturn(List.of());
    generalDetailsFormService.validate(form, bindingResult, validationType, submissionDetail);
    verify(generalDetailsFormValidator, times(1)).validate(eq(form), eq(bindingResult),
        captor.capture());

    assertThat(captor.getValue())
        .extracting(
            GeneralDetailsFormValidatorHints::getValidationType,
            GeneralDetailsFormValidatorHints::getSubmissionDetail,
            GeneralDetailsFormValidatorHints::getLatestApprovedGeneralDetail,
            GeneralDetailsFormValidatorHints::getLicencePresent)
        .containsExactly(validationType, submissionDetail, latestGeneralDetail, false);
  }

  @Test
  void validate_whenCreate_verifyMethodCall() {
    var form = new GeneralDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var validationType = ValidationType.FULL;
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    var captor = ArgumentCaptor.forClass(
        GeneralDetailsFormValidatorHints.class);
    generalDetailsFormService.validate(form, bindingResult, validationType, submissionDetail);
    verify(generalDetailsFormValidator, times(1)).validate(eq(form), eq(bindingResult),
        captor.capture());

    assertThat(captor.getValue())
        .extracting(
            GeneralDetailsFormValidatorHints::getValidationType,
            GeneralDetailsFormValidatorHints::getSubmissionDetail,
            GeneralDetailsFormValidatorHints::getLatestApprovedGeneralDetail,
            GeneralDetailsFormValidatorHints::getLicencePresent)
        .containsExactly(validationType, submissionDetail, null, false);
  }

  @Test
  void validate_whenUpdate_whenLicences_assertHints() {
    var form = new GeneralDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var validationType = ValidationType.FULL;
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var captor = ArgumentCaptor.forClass(
        GeneralDetailsFormValidatorHints.class);
    var latestGeneralDetail = new GeneralDetail();
    var mockLatestSubmission = mock(SubmissionDetail.class);
    when(submissionDetailService.getLatestApprovedSubmissionForSubmission(any()))
        .thenReturn(Optional.of(mockLatestSubmission));
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(mockLatestSubmission))
        .thenReturn(Optional.of(latestGeneralDetail));
    when(licencePersistenceService.getLicencesByGeneralDetail(latestGeneralDetail))
        .thenReturn(List.of(mock(Licence.class)));
    generalDetailsFormService.validate(form, bindingResult, validationType, submissionDetail);
    verify(generalDetailsFormValidator, times(1)).validate(eq(form), eq(bindingResult),
        captor.capture());

    assertThat(captor.getValue())
        .extracting(
            GeneralDetailsFormValidatorHints::getValidationType,
            GeneralDetailsFormValidatorHints::getSubmissionDetail,
            GeneralDetailsFormValidatorHints::getLatestApprovedGeneralDetail,
            GeneralDetailsFormValidatorHints::getLicencePresent)
        .containsExactly(validationType, submissionDetail, latestGeneralDetail, true);
  }

  @Test
  void generalDetailEntityToForm_newEntity_thenEmptyForm() {
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.empty());

    var form = generalDetailsFormService.getGeneralDetailsForm(submissionDetail);
    assertThat(form).hasAllNullFieldsOrPropertiesExcept("quadrantBlocks", "licences");
    assertThat(form.getQuadrantBlocks()).isEmpty();
  }

  @Test
  void generalDetailEntityToForm_existingEntity_thenPopulatedForm() {
    var generalDetail = getGeneralDetail(submissionDetail);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(generalDetail));

    var quadrantBlockRef = "27/10";
    var referenceBlockView = new ReferenceBlockView(quadrantBlockRef, "27", 10, OnshoreOffshore.OFFSHORE);
    var quadrantAndBlock = createQuadrantAndBlock(generalDetail, referenceBlockView);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail)).thenReturn(List.of(quadrantAndBlock));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail))
        .extracting(
            GeneralDetailsForm::getResourceLocatedInUkcs,
            GeneralDetailsForm::getPercentageLocatedInUkcs,
            GeneralDetailsForm::getInformationSource,
            GeneralDetailsForm::getOtherSourceOfInformation,
            GeneralDetailsForm::getQuadrantBlocks
        )
        .containsExactly(
            generalDetail.getResourceLocatedInUkcs(),
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
    var referenceBlockView = new ReferenceBlockView(qbRef, "27", 10, OnshoreOffshore.OFFSHORE);
    var qbRefList = List.of(qbRef);
    when(referenceBlockQueryService.getReferenceBlocksByRefsIn(qbRefList))
        .thenReturn(Collections.singletonList(referenceBlockView));

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

  private QuadrantAndBlock createQuadrantAndBlock(GeneralDetail generalDetail, ReferenceBlockView referenceBlockView) {
    var quadrantAndBlock = new QuadrantAndBlock();
    quadrantAndBlock.setGeneralDetail(generalDetail);
    quadrantAndBlock.setQuadrantBlockReference(referenceBlockView.quadAndBlockRef());
    quadrantAndBlock.setQuadrant(referenceBlockView.quadrant());
    quadrantAndBlock.setBlock(referenceBlockView.blockNo());
    quadrantAndBlock.setOnshoreOffshore(referenceBlockView.onshoreOffshore());
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

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"RELINQUISHMENT_PROSPECT", "RELINQUISHMENT_DISCOVERY"})
  void generalDetailEntityToForm_existingEntity_withRelinquishmentDate_thenPopulatedForm(
      SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    var generalDetail = getGeneralDetail(submissionDetail);
    generalDetail.setRelinquishmentDate(LocalDate.of(2012, 5, 22));
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(generalDetail));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail))
        .extracting(
            GeneralDetailsForm::getRelinquishmentDay,
            GeneralDetailsForm::getRelinquishmentMonth,
            GeneralDetailsForm::getRelinquishmentYear)
        .containsExactly(
            "22",
            "5",
            "2012"
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"RELINQUISHMENT_PROSPECT", "RELINQUISHMENT_DISCOVERY"})
  void generalDetailEntityToForm_existingEntity_withNullRelinquishmentDate_thenEmptyRelinquishmentField(
      SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    var generalDetail = getGeneralDetail(submissionDetail);
    generalDetail.setRelinquishmentDate(null);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(generalDetail));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail))
        .extracting(
            GeneralDetailsForm::getRelinquishmentDay,
            GeneralDetailsForm::getRelinquishmentMonth,
            GeneralDetailsForm::getRelinquishmentYear)
        .containsOnlyNulls();
  }

  @Test
  void getPreselectedLicenceApplication_verifyMethodCall() {
    generalDetailsFormService.getPreSelectedLicenceApplication("app");

    verify(licenceApplicationQueryService, times(1)).getPreSelectedLicenceApplication("app");
  }

  @Test
  void getLicenceApplicationViews_whenLicencesPresent() {
    var form = new GeneralDetailsForm();
    form.setLicences(List.of("ref", "ref2"));
    var genDetail = getGeneralDetail(submissionDetail);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(genDetail));
    var licence1 = new LicenceView("ref", 1, LicenceStatus.EXTANT);
    var licence2 = new LicenceView("ref2", 2, LicenceStatus.EXTANT);

    when(licenceQueryService.getLicencesByIds(form.getLicences()))
        .thenReturn(List.of(licence1, licence2));

    var selectorViews = generalDetailsFormService.getLicenceViews(form);

    assertThat(selectorViews)
        .extracting(
            LicenceView::getId,
            LicenceView::getName,
            LicenceView::isValid)
        .containsExactly(
            tuple("1", "ref", true),
            tuple("2", "ref2", true)
        );
  }

  @Test
  void getLicenceApplicationViews_whenLicencesNotPresent() {
    var form = new GeneralDetailsForm();
    var genDetail = getGeneralDetail(submissionDetail);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(genDetail));

    when(licenceQueryService.getLicencesByIds(form.getLicences()))
        .thenReturn(List.of());

    var selectorViews = generalDetailsFormService.getLicenceViews(form);

    assertThat(selectorViews).isEmpty();
  }

  @Test
  void hasLicencesOnEarlierSubmission_whenFirstSubmission_assertFalse() {
    var newProspectSubmissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var newDiscoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    assertThat(generalDetailsFormService.hasLicencesOnEarlierSubmission(newProspectSubmissionDetail))
        .isFalse();
    assertThat(generalDetailsFormService.hasLicencesOnEarlierSubmission(newDiscoverySubmissionDetail))
        .isFalse();
  }

  @Test
  void hasLicencesOnEarlierSubmission_whenNotFirstSubmissionAndNoLicence_assertFalse() {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    var approvedProspect = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
        .thenReturn(Optional.of(approvedProspect));
    var generalDetail = new GeneralDetail();
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(approvedProspect))
        .thenReturn(Optional.of(generalDetail));
    when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
        .thenReturn(List.of());

    assertThat(generalDetailsFormService.hasLicencesOnEarlierSubmission(submissionDetail))
        .isFalse();
  }

  @Test
  void hasLicencesOnEarlierSubmission_whenLicenceFound_assertTrue() {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    var approvedProspect = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
        .thenReturn(Optional.of(approvedProspect));
    var generalDetail = new GeneralDetail();
    generalDetail.setResourceOnALicence(true);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(approvedProspect))
        .thenReturn(Optional.of(generalDetail));
    when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
        .thenReturn(List.of(mock(Licence.class)));

    assertThat(generalDetailsFormService.hasLicencesOnEarlierSubmission(submissionDetail))
        .isTrue();
  }

  @Test
  void hasLicencesOnEarlierSubmission_whenEarlierSubmissionWithoutGeneralDetail_assertFalse() {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    var approvedProspect = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
        .thenReturn(Optional.of(approvedProspect));
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(approvedProspect))
        .thenReturn(Optional.empty());
    assertThat(generalDetailsFormService.hasLicencesOnEarlierSubmission(submissionDetail))
        .isFalse();
  }


  @Test
  void hasLicencesOnEarlierSubmission_whenEarlierSubmissionNotPresent_assertFalse() {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    when(submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail))
        .thenReturn(Optional.empty());
    assertThat(generalDetailsFormService.hasLicencesOnEarlierSubmission(submissionDetail))
        .isFalse();
  }

  private GeneralDetail getGeneralDetail(SubmissionDetail submissionDetail) {
    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);
    generalDetail.setResourceLocatedInUkcs(true);
    generalDetail.setInformationSource(InformationSource.WELL_INVESTMENT_DECISION);
    return generalDetail;
  }
}