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
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockView;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
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
  private static OrganisationUnitQueryService organisationUnitQueryService;
  private static ReferenceBlockQueryService referenceBlockQueryService;
  private static LicencePersistenceService licencePersistenceService;
  private static LicenceQueryService licenceQueryService;

  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    generalDetailsFormValidator = mock(GeneralDetailsFormValidator.class);
    generalDetailsPersistenceService = mock(GeneralDetailsPersistenceService.class);
    quadrantAndBlockPersistenceService = mock(QuadrantAndBlockPersistenceService.class);
    referenceBlockQueryService = mock(ReferenceBlockQueryService.class);
    licenceApplicationQueryService = mock(LicenceApplicationQueryService.class);
    organisationUnitQueryService = mock(OrganisationUnitQueryService.class);
    licencePersistenceService = mock(LicencePersistenceService.class);
    licenceQueryService = mock(LicenceQueryService.class);

    SearchSelectorService searchSelectorService = new SearchSelectorService();
    generalDetailsFormService = new GeneralDetailsFormService(
        generalDetailsFormValidator,
        generalDetailsPersistenceService,
        searchSelectorService,
        licenceApplicationQueryService,
        organisationUnitQueryService,
        quadrantAndBlockPersistenceService,
        referenceBlockQueryService,
        licenceQueryService,
        licencePersistenceService);
    var submissionDetailId = UUID.randomUUID();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new GeneralDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var validationType = ValidationType.FULL;
    generalDetailsFormService.validate(form, bindingResult, validationType, SubmissionType.CREATE_PROSPECT);
    verify(generalDetailsFormValidator, times(1)).validate(form, bindingResult,
        validationType, SubmissionType.CREATE_PROSPECT);
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
    var quadrantAndBlock = createQuadrantAndBlock(generalDetail, quadrantBlockRef);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail)).thenReturn(List.of(quadrantAndBlock));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail))
        .extracting(
            GeneralDetailsForm::getResourceName,
            GeneralDetailsForm::getOnshoreOffshore,
            GeneralDetailsForm::getResourceLocatedInUkcs,
            GeneralDetailsForm::getPercentageLocatedInUkcs,
            GeneralDetailsForm::getInformationSource,
            GeneralDetailsForm::getOtherSourceOfInformation,
            GeneralDetailsForm::getQuadrantBlocks,
            GeneralDetailsForm::getResponsibleOrganisationId
        )
        .containsExactly(
            generalDetail.getResourceName(),
            generalDetail.getOnshoreOffshore(),
            generalDetail.getResourceLocatedInUkcs(),
            generalDetail.getPercentageLocatedInUkcs(),
            generalDetail.getInformationSource(),
            generalDetail.getOtherInformationSource(),
            List.of(quadrantBlockRef),
            generalDetail.getOrganisationId().toString()
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
    when(referenceBlockQueryService.getReferenceBlocksByRefsIn(qbRefList))
        .thenReturn(Collections.singletonList(new ReferenceBlockView(qbRef, "005005")));

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
  void getPreselectedLicenceApplication_verifyMethodCall() {
    generalDetailsFormService.getPreSelectedLicenceApplication("app");

    verify(licenceApplicationQueryService, times(1)).getPreSelectedLicenceApplication("app");
  }

  @Test
  void getPreselectedOrganisation_withNameAndIdOnly() {
    var organisationUnit = new OrganisationUnit();
    organisationUnit.setName("name");
    when(organisationUnitQueryService.findOrganisationUnit(1))
        .thenReturn(Optional.of(organisationUnit));

    assertThat(generalDetailsFormService.getPreSelectedOrganisation("1"))
        .containsEntry("1", "name");
  }

  @Test
  void getPreselectedOrganisation_withOrganisationNumber() {
    var organisationUnit = new OrganisationUnit();
    organisationUnit.setName("name");
    organisationUnit.setRegisteredNumber("25");
    when(organisationUnitQueryService.findOrganisationUnit(1))
        .thenReturn(Optional.of(organisationUnit));

    assertThat(generalDetailsFormService.getPreSelectedOrganisation("1"))
        .containsEntry("1", "name (25)");
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

  private GeneralDetail getGeneralDetail(SubmissionDetail submissionDetail) {
    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);
    generalDetail.setResourceName("prospect name");
    generalDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    generalDetail.setResourceLocatedInUkcs(true);
    generalDetail.setInformationSource(InformationSource.WELL_INVESTMENT_DECISION);
    generalDetail.setOrganisationId(1);
    generalDetail.setOrganisationRegisteredNumber("25");
    generalDetail.setOrganisationName("name");
    return generalDetail;
  }
}