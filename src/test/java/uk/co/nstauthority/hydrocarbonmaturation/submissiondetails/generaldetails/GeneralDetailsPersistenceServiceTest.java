package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

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

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    generalDetailsPersistenceService = new GeneralDetailsPersistenceService(
        generalDetailsRepository,
        quadrantAndBlockPersistenceService,
        licenceApplicationQueryService,
        licencePersistenceService);
    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void saveGeneralDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new GeneralDetailsForm();
    form.setResourceLocatedInUkcs(true);
    form.setInformationSource(InformationSource.PRE_DRILL_SUBMISSION);

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getResourceLocatedInUkcs,
            GeneralDetail::getPercentageLocatedInUkcs,
            GeneralDetail::getInformationSource,
            GeneralDetail::getOtherInformationSource
        )
        .containsExactly(
            form.getResourceLocatedInUkcs(),
            form.getPercentageLocatedInUkcs(),
            form.getInformationSource(),
            form.getOtherSourceOfInformation()
        );
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

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail);

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

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail);

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

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail);

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

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail);

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

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail);

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

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .hasAllNullFieldsOrPropertiesExcept("relinquishmentDate", "submissionDetail");

    assertThat(savedGeneralDetail.getRelinquishmentDate())
        .isEqualTo(LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"RELINQUISHMENT_PROSPECT", "RELINQUISHMENT_DISCOVERY"})
  void saveGeneralDetails_whenRelinquishment_whenNoDateProvided(SubmissionType submissionType) {
    var form = new GeneralDetailsForm();

    submissionDetail.setSubmissionType(submissionType);
    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail);

    verify(generalDetailsRepository, times(1))
        .save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .hasAllNullFieldsOrPropertiesExcept("submissionDetail");
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
            GeneralDetail::getResourceLocatedInUkcs,
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
}