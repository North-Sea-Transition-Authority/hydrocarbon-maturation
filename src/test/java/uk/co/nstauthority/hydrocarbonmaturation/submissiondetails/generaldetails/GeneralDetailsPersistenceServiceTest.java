package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsPersistenceServiceTest {

  private static final String LICENCE_APPLICATION = "1";
  private static final String LICENCE_APPLICATION_REF = "F1234/5678";

  private static GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  @Mock
  private static GeneralDetailsRepository generalDetailsRepository;
  @Mock
  private static LicenceApplicationQueryService licenceApplicationQueryService;
  @Mock
  private static LicencePersistenceService licencePersistenceService;
  @Mock
  private static SubmissionSupportingDocumentService supportingDocumentService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    generalDetailsPersistenceService = new GeneralDetailsPersistenceService(
        generalDetailsRepository,
        quadrantAndBlockPersistenceService,
        licenceApplicationQueryService,
        licencePersistenceService,
        supportingDocumentService);
    var submissionDetailId = UUID.randomUUID();
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void saveGeneralDetails_whenGivenAnOffshoreForm_andLocatedInUkcs_verifyCreatedEntity() {
    var form = new GeneralDetailsForm();
    form.setProspectDiscoveryLocatedInUkcs(true);
    form.setInformationSource(InformationSource.PRE_DRILL_SUBMISSION);

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.OFFSHORE);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getProspectDiscoveryLocatedInUkcs,
            GeneralDetail::getPercentageLocatedInUkcs,
            GeneralDetail::getProspectDiscoveryLocatedInEngland,
            GeneralDetail::getPercentageLocatedInEngland,
            GeneralDetail::getInformationSource,
            GeneralDetail::getOtherInformationSource
        )
        .containsExactly(
            form.getProspectDiscoveryLocatedInUkcs(),
            form.getPercentageLocatedInUkcs(),
            form.getProspectDiscoveryLocatedInEngland(),
            form.getPercentageLocatedInEngland(),
            form.getInformationSource(),
            form.getOtherSourceOfInformation()
        );
  }

  @Test
  void saveGeneralDetails_whenGivenAnOffshoreForm_andNotLocatedInUkcs_verifyCreatedEntity() {
    var form = new GeneralDetailsForm();
    form.setProspectDiscoveryLocatedInUkcs(false);
    form.setPercentageLocatedInUkcs(new DecimalInput("42"));
    form.setInformationSource(InformationSource.PRE_DRILL_SUBMISSION);

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.OFFSHORE);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getProspectDiscoveryLocatedInUkcs,
            GeneralDetail::getPercentageLocatedInUkcs,
            GeneralDetail::getProspectDiscoveryLocatedInEngland,
            GeneralDetail::getPercentageLocatedInEngland,
            GeneralDetail::getInformationSource,
            GeneralDetail::getOtherInformationSource
        )
        .containsExactly(
            form.getProspectDiscoveryLocatedInUkcs(),
            form.getPercentageLocatedInUkcs().asBigDecimal().get(),
            form.getProspectDiscoveryLocatedInEngland(),
            form.getPercentageLocatedInEngland(),
            form.getInformationSource(),
            form.getOtherSourceOfInformation()
        );
  }

  @Test
  void saveGeneralDetails_whenGivenAnOnshoreForm_andLocatedInEngland_verifyCreatedEntity() {
    var form = new GeneralDetailsForm();
    form.setProspectDiscoveryLocatedInEngland(true);
    form.setInformationSource(InformationSource.PRE_DRILL_SUBMISSION);

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.ONSHORE);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getProspectDiscoveryLocatedInUkcs,
            GeneralDetail::getPercentageLocatedInUkcs,
            GeneralDetail::getProspectDiscoveryLocatedInEngland,
            GeneralDetail::getPercentageLocatedInEngland,
            GeneralDetail::getInformationSource,
            GeneralDetail::getOtherInformationSource
        )
        .containsExactly(
            form.getProspectDiscoveryLocatedInUkcs(),
            form.getPercentageLocatedInUkcs(),
            form.getProspectDiscoveryLocatedInEngland(),
            form.getPercentageLocatedInEngland(),
            form.getInformationSource(),
            form.getOtherSourceOfInformation()
        );
  }

  @Test
  void saveGeneralDetails_whenGivenAnOnshoreForm_andNotLocatedInEngland_verifyCreatedEntity() {
    var form = new GeneralDetailsForm();
    form.setProspectDiscoveryLocatedInEngland(false);
    form.setPercentageLocatedInEngland(new DecimalInput("42"));
    form.setInformationSource(InformationSource.PRE_DRILL_SUBMISSION);

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.ONSHORE);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getProspectDiscoveryLocatedInUkcs,
            GeneralDetail::getPercentageLocatedInUkcs,
            GeneralDetail::getProspectDiscoveryLocatedInEngland,
            GeneralDetail::getPercentageLocatedInEngland,
            GeneralDetail::getInformationSource,
            GeneralDetail::getOtherInformationSource
        )
        .containsExactly(
            form.getProspectDiscoveryLocatedInUkcs(),
            form.getPercentageLocatedInUkcs(),
            form.getProspectDiscoveryLocatedInEngland(),
            form.getPercentageLocatedInEngland().asBigDecimal().get(),
            form.getInformationSource(),
            form.getOtherSourceOfInformation()
        );
  }

  @Test
  void saveGeneralDetails_whenRelinquishmentSubmission_verifyRelinquishmentInformationSource() {
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_PROSPECT);
    var form = new GeneralDetailsForm();
    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);
    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.OFFSHORE);
    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = generalDetailsArgumentCaptor.getValue();
    assertThat(savedGeneralDetail.getInformationSource()).isEqualTo(InformationSource.RELINQUISHMENT);
  }

  @Test
  void saveGeneralDetails_whenLicenceApplicationInfoSource() {
    var form = new GeneralDetailsForm();
    form.setLicenceApplication(LICENCE_APPLICATION);
    form.setInformationSource(InformationSource.LICENCE_APPLICATION);

    var licenceApplicationId = Integer.parseInt(LICENCE_APPLICATION);
    when(licenceApplicationQueryService.getLicenceApplicationRef(licenceApplicationId))
        .thenReturn(LICENCE_APPLICATION_REF);

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.OFFSHORE);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getLicenceApplicationId,
            GeneralDetail::getLicenceApplicationReference
        )
        .containsExactly(
            licenceApplicationId,
            LICENCE_APPLICATION_REF
        );
  }

  @Test
  void saveGeneralDetails_whenLicenceApplicationInfoSource_andManualEntryLicenceApp() {
    var form = new GeneralDetailsForm();
    var manualLicenceApplication = SearchSelectorService.getValueWithManualEntryPrefix(LICENCE_APPLICATION);
    form.setLicenceApplication(manualLicenceApplication);
    form.setInformationSource(InformationSource.LICENCE_APPLICATION);

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.OFFSHORE);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getLicenceApplicationId,
            GeneralDetail::getLicenceApplicationReference
        )
        .containsExactly(
            null,
            SearchSelectorService.removePrefix(manualLicenceApplication)
        );
  }

  @Test
  void saveGeneralDetails_checkLicenceAppDetails_whenNoInfoSource() {
    var form = new GeneralDetailsForm();

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.OFFSHORE);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getLicenceApplicationId,
            GeneralDetail::getLicenceApplicationReference
        )
        .containsExactly(
            null,
            null
        );
  }

  @Test
  void saveGeneralDetails_checkLicenceAppDetails_whenNotLicenceAppInfoSource() {
    var form = new GeneralDetailsForm();
    form.setInformationSource(InformationSource.PRE_DRILL_SUBMISSION);

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.OFFSHORE);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getLicenceApplicationId,
            GeneralDetail::getLicenceApplicationReference
        )
        .containsExactly(
            null,
            null
        );
  }

  @Test
  void saveGeneralDetails_checkLicenceAppDetails_whenLicenceAppNotProvided() {
    var form = new GeneralDetailsForm();
    form.setInformationSource(InformationSource.LICENCE_APPLICATION);

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.OFFSHORE);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getLicenceApplicationId,
            GeneralDetail::getLicenceApplicationReference
        )
        .containsExactly(
            null,
            null
        );
  }


  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"RELINQUISHMENT_PROSPECT", "RELINQUISHMENT_DISCOVERY"})
  void saveGeneralDetails_whenRelinquishment_whenDateProvided(SubmissionType submissionType) {
    var form = new GeneralDetailsForm();
    var year = "2012";
    var month = "12";
    var day = "1";

    form.setRelinquishmentYear(year);
    form.setRelinquishmentMonth(month);
    form.setRelinquishmentDay(day);
    submissionDetail.setSubmissionType(submissionType);
    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.OFFSHORE);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .hasAllNullFieldsOrPropertiesExcept("relinquishmentDate", "submissionDetail", "informationSource");

    assertThat(savedGeneralDetail.getRelinquishmentDate())
        .isEqualTo(LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"RELINQUISHMENT_PROSPECT", "RELINQUISHMENT_DISCOVERY"})
  void saveGeneralDetails_whenRelinquishment_whenNoDateProvided(SubmissionType submissionType) {
    var form = new GeneralDetailsForm();

    submissionDetail.setSubmissionType(submissionType);
    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.OFFSHORE);

    verify(generalDetailsRepository, times(1))
        .save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .hasAllNullFieldsOrPropertiesExcept("submissionDetail", "informationSource");
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void saveGeneralDetails_whenField_verifyCommon(SubmissionType submissionType) {
    var form = new GeneralDetailsForm();
    form.setFieldReport(List.of(new UploadedFileForm()));
    form.setFdpDateDay("1");
    form.setFdpDateMonth("1");
    form.setFdpDateYear("2024");
    submissionDetail.setSubmissionType(submissionType);
    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.OFFSHORE);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getCurrentDriveMechanism,
            GeneralDetail::getOtherDriveMechanism
        ).containsExactly(
            form.getCurrentDriveMechanism(),
            form.getOtherDriveMechanism()
        );

    verify(supportingDocumentService).saveDocuments(submissionDetail, form.getFieldReport(), FileDocumentType.FIELD_REPORT);
    verifyNoMoreInteractions(supportingDocumentService);
  }

  @Test
  void saveGeneralDetails_whenCop_assertFieldStoppedProducingDate() {
    var expectedDateFieldStoppedProducing = LocalDate.of(1999, 05, 01);
    var form = new GeneralDetailsForm();
    form.setFieldStoppedProducingDay("01");
    form.setFieldStoppedProducingMonth("05");
    form.setFieldStoppedProducingYear("1999");


    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.OFFSHORE);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail.getFieldStoppedProducingDate())
        .hasToString(expectedDateFieldStoppedProducing.toString());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDP", "CREATE_FDPA"})
  void saveGeneralDetails_whenFdpOrFdpa_assertFdpFdpaOnlyFields(SubmissionType submissionType) {
    var form = new GeneralDetailsForm();
    form.setCapitalProjectScopeDelivered("test");
    form.setFdpTitle("FDP Title");
    form.setFdpDateDay("1");
    form.setFdpDateMonth("1");
    form.setFdpDateYear("2024");

    submissionDetail.setSubmissionType(submissionType);
    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, OnshoreOffshore.OFFSHORE);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail.getCapitalProjectScopeDelivered())
        .isEqualTo(form.getCapitalProjectScopeDelivered());

    assertThat(savedGeneralDetail.getFdpTitle()).isEqualTo(form.getFdpTitle());

    assertThat(savedGeneralDetail.getFdpDate())
        .isEqualTo(
            LocalDate.of(
            Integer.parseInt(form.getFdpDateYear()),
            Integer.parseInt(form.getFdpDateMonth()),
            Integer.parseInt(form.getFdpDateDay()))
        );
  }

  @Test
  void updateGeneralDetailProspect_whenGivenAForm_verifySaved() {
    var form = new UpdateProspectForm();
    form.setInformationSource(InformationSource.OTHER);
    form.setOtherSourceOfInformation("other source");
    form.setProspectSelectedOption(submissionDetail.getId().toString());
    var generalDetail = new GeneralDetail();

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.updateGeneralDetailProspect(form, generalDetail);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getProspectDiscoveryLocatedInUkcs,
            GeneralDetail::getPercentageLocatedInUkcs,
            GeneralDetail::getInformationSource,
            GeneralDetail::getOtherInformationSource
        )
        .containsExactly(
            null,
            null,
            form.getInformationSource(),
            form.getOtherSourceOfInformation()
        );
  }

  @Test
  void setRelinquishmentInformationSource() {
    var generalDetailCaptor = ArgumentCaptor.forClass(GeneralDetail.class);
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.RELINQUISHMENT_PROSPECT);
    var generalDetail = new GeneralDetail();
    generalDetail.setInformationSource(InformationSource.OTHER);
    generalDetail.setOtherInformationSource("other information source");
    when(generalDetailsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    generalDetailsPersistenceService.setRelinquishmentInformationSource(submissionDetail);
    verify(generalDetailsRepository, times(1)).save(generalDetailCaptor.capture());
    var savedGeneralDetail = generalDetailCaptor.getValue();
    assertThat(savedGeneralDetail.getInformationSource()).isEqualTo(InformationSource.RELINQUISHMENT);
    assertThat(savedGeneralDetail.getOtherInformationSource()).isNull();
  }

  @Test
  void setRelinquishmentInformationSource_whenNoGeneralDetail() {
    var generalDetailCaptor = ArgumentCaptor.forClass(GeneralDetail.class);
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.RELINQUISHMENT_PROSPECT);
    when(generalDetailsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());
    generalDetailsPersistenceService.setRelinquishmentInformationSource(submissionDetail);
    verify(generalDetailsRepository, times(1)).save(generalDetailCaptor.capture());
    var savedGeneralDetail = generalDetailCaptor.getValue();
    assertThat(savedGeneralDetail.getInformationSource()).isEqualTo(InformationSource.RELINQUISHMENT);
    assertThat(savedGeneralDetail.getOtherInformationSource()).isNull();
  }

  @Test
  void getGeneralDetailBySubmissionId_verifyMethodCall() {
    generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail);
    verify(generalDetailsRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  @Test
  void getGeneralDetailOrThrow_verifyMethodCall() {
    var generalDetail = new GeneralDetail();

    when(generalDetailsRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(generalDetail));
    assertThat(generalDetailsPersistenceService.getGeneralDetailOrThrow(submissionDetail))
        .isEqualTo(generalDetail);
  }

  @Test
  void getGeneralDetailOrThrow_throw() {
    when(generalDetailsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() ->  generalDetailsPersistenceService.getGeneralDetailOrThrow(submissionDetail))
        .isExactlyInstanceOf(RemiEntityNotFoundException.class)
        .hasMessage("Could not find general detail for submission detail %s".formatted(submissionDetail.getId()));
  }

  @Test
  void isOnAnExtantLicence_verifyMethodCall() {
    var generalDetail = new GeneralDetail();
    generalDetailsPersistenceService.isOnAnExtantLicence(generalDetail);

    verify(licencePersistenceService).hasAnExtantLicence(generalDetail);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void isOnAnExtantLicence_assertReturn(boolean hasExtantLicence) {
    var generalDetail = new GeneralDetail();
    when(licencePersistenceService.hasAnExtantLicence(generalDetail))
        .thenReturn(hasExtantLicence);

    assertThat(generalDetailsPersistenceService.isOnAnExtantLicence(generalDetail)).isEqualTo(hasExtantLicence);
  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .map(Arguments::of);
  }
}