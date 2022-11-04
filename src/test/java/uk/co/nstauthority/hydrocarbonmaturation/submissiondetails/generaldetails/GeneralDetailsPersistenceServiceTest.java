package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

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
  private static OrganisationUnitQueryService organisationUnitQueryService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    generalDetailsPersistenceService = new GeneralDetailsPersistenceService(
        generalDetailsRepository,
        quadrantAndBlockPersistenceService,
        licenceApplicationQueryService,
        organisationUnitQueryService);
    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void saveGeneralDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new GeneralDetailsForm();
    form.setProspectName("prospect name");
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    form.setProspectLocatedInUkcs(true);
    form.setInformationSource(InformationSource.PRE_DRILL_SUBMISSION);
    form.setResponsibleOrganisationId("1");

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    when(organisationUnitQueryService.findOrganisationUnit(1))
        .thenReturn(Optional.of(OrganisationUnitTestUtil.Builder()
                .withOrganisationId(1)
                .withOrganisationName("name")
                .withOrganisationNumber("25").build()));

    generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getProspectName,
            GeneralDetail::getOnshoreOffshore,
            GeneralDetail::getProspectLocatedInUkcs,
            GeneralDetail::getPercentageLocatedInUkcs,
            GeneralDetail::getInformationSource,
            GeneralDetail::getOtherInformationSource,
            GeneralDetail::getOrganisationId
        )
        .containsExactly(
            form.getProspectName(),
            form.getOnshoreOffshore(),
            form.getProspectLocatedInUkcs(),
            form.getPercentageLocatedInUkcs(),
            form.getInformationSource(),
            form.getOtherSourceOfInformation(),
            Integer.valueOf(form.getResponsibleOrganisationId())
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

  @Test
  void updateGeneralDetailProspect_whenGivenAForm_verifySaved() {
    var form = new UpdateProspectForm();
    form.setInformationSource(InformationSource.OTHER);
    form.setOtherSourceOfInformation("other source");
    form.setProspectSelectedOption(submissionDetail.getId().toString());
    var generalDetail = new GeneralDetail();

    var generalDetailsArgumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);

    generalDetailsPersistenceService.updateGeneralDetailProspect(form, submissionDetail, generalDetail);

    verify(generalDetailsRepository, times(1)).save(generalDetailsArgumentCaptor.capture());
    var savedGeneralDetail = (GeneralDetail) generalDetailsArgumentCaptor.getValue();

    assertThat(savedGeneralDetail)
        .extracting(
            GeneralDetail::getProspectName,
            GeneralDetail::getOnshoreOffshore,
            GeneralDetail::getProspectLocatedInUkcs,
            GeneralDetail::getPercentageLocatedInUkcs,
            GeneralDetail::getInformationSource,
            GeneralDetail::getOtherInformationSource
        )
        .containsExactly(
            null,
            null,
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
  void getGeneralDetailById_verifyMethodCall() {
    var id = UUID.randomUUID();
    generalDetailsPersistenceService.getGeneralDetailById(id);
    verify(generalDetailsRepository, times(1)).findById(id);
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
  void getGeneralDetailsByName_verifyMethodCall() {
    generalDetailsPersistenceService.getGeneralDetailsByName("prospect");
    verify(generalDetailsRepository, times(1)).findByProspectNameContainingIgnoreCase("prospect");
  }

  @Test
  void createGeneralDetailWithProspectName_VerifySave() {
    var prospectName = "prospect-name";

    var argumentCaptor = ArgumentCaptor.forClass(GeneralDetail.class);
    generalDetailsPersistenceService.createGeneralDetailWithProspectName(submissionDetail, prospectName);

    verify(generalDetailsRepository, times(1)).save(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue())
        .extracting(
            GeneralDetail::getProspectName,
            GeneralDetail::getSubmissionDetail
        )
        .containsExactly(
            prospectName,
            submissionDetail
        );
  }
}