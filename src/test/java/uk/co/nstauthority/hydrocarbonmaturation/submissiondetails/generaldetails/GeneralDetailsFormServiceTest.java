package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockView;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsFormServiceTest {

  private static final String LICENCE_APPLICATION_REF = "F33/1234";
  private static final String LICENCE_APPLICATION_ID = "1";

  @Mock
  private GeneralDetailsFormValidator generalDetailsFormValidator;

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private LicenceApplicationQueryService licenceApplicationQueryService;

  @Mock
  private QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;

  @Mock
  private ReferenceBlockQueryService referenceBlockQueryService;

  @Mock
  private LicencePersistenceService licencePersistenceService;

  @Mock
  private LicenceQueryService licenceQueryService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private SubmissionSupportingDocumentService supportingDocumentService;

  @InjectMocks
  private GeneralDetailsFormService generalDetailsFormService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    var submissionDetailId = UUID.randomUUID();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void generalDetailEntityToForm_newEntity_thenEmptyForm() {
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.empty());

    var form = generalDetailsFormService.getGeneralDetailsForm(submissionDetail);
    assertThat(form).hasAllNullFieldsOrPropertiesExcept("quadrantBlocks", "licences", "fieldReport");
    assertThat(form.getQuadrantBlocks()).isEmpty();
  }

  @Test
  void generalDetailEntityToForm_existingEntityOffshore_thenPopulatedForm() {
    var generalDetail = getGeneralDetail(submissionDetail);
    generalDetail.setProspectDiscoveryLocatedInUkcs(false);
    generalDetail.setPercentageLocatedInUkcs(new BigDecimal(50));
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(generalDetail));

    var quadrantBlockRef = "27/10";
    var referenceBlockView = new ReferenceBlockView(quadrantBlockRef, "27", 10, OnshoreOffshore.OFFSHORE);
    var quadrantAndBlock = createQuadrantAndBlock(generalDetail, referenceBlockView);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail)).thenReturn(List.of(quadrantAndBlock));

    var generalDetailForm = generalDetailsFormService.getGeneralDetailsForm(submissionDetail);
    assertThat(generalDetailForm)
        .extracting(
            GeneralDetailsForm::getProspectDiscoveryLocatedInUkcs,
            GeneralDetailsForm::getProspectDiscoveryLocatedInEngland,
            GeneralDetailsForm::getInformationSource,
            GeneralDetailsForm::getOtherSourceOfInformation,
            GeneralDetailsForm::getQuadrantBlocks
        )
        .containsExactly(
            generalDetail.getProspectDiscoveryLocatedInUkcs(),
            generalDetail.getProspectDiscoveryLocatedInEngland(),
            generalDetail.getInformationSource(),
            generalDetail.getOtherInformationSource(),
            List.of(quadrantBlockRef)
        );

    assertThat(generalDetailForm.getPercentageLocatedInUkcs().asBigDecimal())
        .contains(generalDetail.getPercentageLocatedInUkcs());
    assertThat(generalDetailForm.getPercentageLocatedInEngland()).isNull();
  }

  @Test
  void generalDetailEntityToForm_existingEntityOnshore_thenPopulatedForm() {
    var generalDetail = getGeneralDetail(submissionDetail);
    generalDetail.setProspectDiscoveryLocatedInUkcs(null);
    generalDetail.setProspectDiscoveryLocatedInEngland(false);
    generalDetail.setPercentageLocatedInEngland(new BigDecimal(50));
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(generalDetail));

    var quadrantBlockRef = "27/10";
    var referenceBlockView = new ReferenceBlockView(quadrantBlockRef, "27", 10, OnshoreOffshore.OFFSHORE);
    var quadrantAndBlock = createQuadrantAndBlock(generalDetail, referenceBlockView);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail)).thenReturn(List.of(quadrantAndBlock));

    var generalDetailForm = generalDetailsFormService.getGeneralDetailsForm(submissionDetail);
    assertThat(generalDetailForm)
        .extracting(
            GeneralDetailsForm::getProspectDiscoveryLocatedInUkcs,
            GeneralDetailsForm::getProspectDiscoveryLocatedInEngland,
            GeneralDetailsForm::getInformationSource,
            GeneralDetailsForm::getOtherSourceOfInformation,
            GeneralDetailsForm::getQuadrantBlocks
        )
        .containsExactly(
            generalDetail.getProspectDiscoveryLocatedInUkcs(),
            generalDetail.getProspectDiscoveryLocatedInEngland(),
            generalDetail.getInformationSource(),
            generalDetail.getOtherInformationSource(),
            List.of(quadrantBlockRef)
        );

    assertThat(generalDetailForm.getPercentageLocatedInEngland().asBigDecimal())
        .contains(generalDetail.getPercentageLocatedInEngland());
    assertThat(generalDetailForm.getPercentageLocatedInUkcs()).isNull();
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
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail).getLicenceApplication())
        .isEqualTo(SearchSelectorService.getValueWithManualEntryPrefix(LICENCE_APPLICATION_REF));
  }

  @Test
  void generalDetailEntityToForm_existingEntity_withLicenceApplicationId_thenPopulatedForm() {
    var generalDetail = getGeneralDetail(submissionDetail);
    generalDetail.setLicenceApplicationReference(LICENCE_APPLICATION_REF);
    generalDetail.setLicenceApplicationId(Integer.parseInt(LICENCE_APPLICATION_ID));
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

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
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

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
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail))
        .extracting(
            GeneralDetailsForm::getRelinquishmentDay,
            GeneralDetailsForm::getRelinquishmentMonth,
            GeneralDetailsForm::getRelinquishmentYear)
        .containsOnlyNulls();
  }

  @Test
  void generalDetailEntityToForm_existingEntity_cop_thenStoppedProducingDate() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
    var generalDetail = getGeneralDetail(submissionDetail);

    generalDetail.setFieldStoppedProducingDate(LocalDate.of(1999, 5, 1));

    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail))
        .extracting(
            GeneralDetailsForm::getFieldStoppedProducingDay,
            GeneralDetailsForm::getFieldStoppedProducingMonth,
            GeneralDetailsForm::getFieldStoppedProducingYear
        ).containsExactly(
            String.valueOf(generalDetail.getFieldStoppedProducingDate().getDayOfMonth()),
            String.valueOf(generalDetail.getFieldStoppedProducingDate().getMonthValue()),
            String.valueOf(generalDetail.getFieldStoppedProducingDate().getYear())
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDP", "CREATE_FDPA"})
  void generalDetailEntityToForm_existingEntity_FdpAndFdpa_thenCapitalProjectScopeAndFdpDateAndTitle(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    var generalDetail = getGeneralDetail(submissionDetail);

    generalDetail.setCapitalProjectScopeDelivered("test");
    generalDetail.setFdpTitle("FDP Title");
    generalDetail.setFdpDate(LocalDate.of(2024, 1, 1));

    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

    var result = generalDetailsFormService.getGeneralDetailsForm(submissionDetail);

    assertThat(result.getCapitalProjectScopeDelivered()).isEqualTo(generalDetail.getCapitalProjectScopeDelivered());
    assertThat(result.getFdpTitle()).isEqualTo(generalDetail.getFdpTitle());
    assertThat(result.getFdpDateDay()).isEqualTo(String.valueOf(generalDetail.getFdpDate().getDayOfMonth()));
    assertThat(result.getFdpDateMonth()).isEqualTo(String.valueOf(generalDetail.getFdpDate().getMonthValue()));
    assertThat(result.getFdpDateYear()).isEqualTo(String.valueOf(generalDetail.getFdpDate().getYear()));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDP", "CREATE_FDPA"})
  void generalDetailEntityToForm_existingEntity_allFieldSubmission_thenCommonFieldAttributes(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    var generalDetail = getGeneralDetail(submissionDetail);

    generalDetail.setCurrentDriveMechanism(DriveMechanism.OTHER);
    generalDetail.setOtherDriveMechanism("test");
    generalDetail.setFdpDate(LocalDate.of(2024, 1, 1));

    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail))
        .extracting(
            GeneralDetailsForm::getCurrentDriveMechanism,
            GeneralDetailsForm::getOtherDriveMechanism
        ).containsExactly(
            generalDetail.getCurrentDriveMechanism(),
            generalDetail.getOtherDriveMechanism()
        );
  }

  @Test
  void generalDetailEntityToForm_fieldSubmission_verifyFieldReportAdded() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
    var generalDetail = getGeneralDetail(submissionDetail);

    var existingDocuments = mock(List.class);
    when(supportingDocumentService.getUploadedFileForms(submissionDetail, FileDocumentType.FIELD_REPORT))
        .thenReturn(existingDocuments);

    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

    assertThat(generalDetailsFormService.getGeneralDetailsForm(submissionDetail))
        .extracting(
            GeneralDetailsForm::getFieldReport
        ).isEqualTo(
            existingDocuments
        );
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
    var licence1 = new LicenceDto("ref", 1, LicenceStatus.EXTANT);
    var licence2 = new LicenceDto("ref2", 2, LicenceStatus.EXTANT);

    when(licenceQueryService.getLicencesByIds(form.getLicences()))
        .thenReturn(List.of(licence1, licence2));

    var selectorViews = generalDetailsFormService.getLicenceViews(form);

    assertThat(selectorViews)
        .extracting(
            LicenceDto::getId,
            LicenceDto::getName,
            LicenceDto::isValid)
        .containsExactly(
            tuple("1", "ref", true),
            tuple("2", "ref2", true)
        );
  }

  @Test
  void getLicenceApplicationViews_whenLicencesNotPresent() {
    var form = new GeneralDetailsForm();

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
    when(submissionDetailService.getLatestApprovedSubmissionDetailForSubmission(submissionDetail))
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
    when(submissionDetailService.getLatestApprovedSubmissionDetailForSubmission(submissionDetail))
        .thenReturn(Optional.of(approvedProspect));
    var generalDetail = new GeneralDetail();
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
    when(submissionDetailService.getLatestApprovedSubmissionDetailForSubmission(submissionDetail))
        .thenReturn(Optional.of(approvedProspect));
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(approvedProspect))
        .thenReturn(Optional.empty());
    assertThat(generalDetailsFormService.hasLicencesOnEarlierSubmission(submissionDetail))
        .isFalse();
  }


  @Test
  void hasLicencesOnEarlierSubmission_whenEarlierSubmissionNotPresent_assertFalse() {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    when(submissionDetailService.getLatestApprovedSubmissionDetailForSubmission(submissionDetail))
        .thenReturn(Optional.empty());
    assertThat(generalDetailsFormService.hasLicencesOnEarlierSubmission(submissionDetail))
        .isFalse();
  }

  private GeneralDetail getGeneralDetail(SubmissionDetail submissionDetail) {
    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);
    generalDetail.setProspectDiscoveryLocatedInUkcs(true);
    generalDetail.setInformationSource(InformationSource.WELL_INVESTMENT_DECISION);
    generalDetail.setFieldStoppedProducingDate(LocalDate.of(1999, 5, 1));
    generalDetail.setCapitalProjectScopeDelivered("test");
    generalDetail.setCurrentDriveMechanism(DriveMechanism.OTHER);
    generalDetail.setOtherDriveMechanism("test");
    return generalDetail;
  }
}