package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.LicenceViewTestUtil;

@ExtendWith(MockitoExtension.class)
class LicencePersistenceServiceTest {

  @Mock
  private LicenceRepository licenceRepository;

  @Mock
  private LicenceQueryService licenceQueryService;

  @InjectMocks
  private LicencePersistenceService licencePersistenceService;

  @Test
  void saveLicence_VerifyDeletesExistingBeforeSavingNew() {
    var generalDetail = mock(GeneralDetail.class);

    var licenceId = "25";
    var licenceIds = List.of(licenceId);

    var form = new GeneralDetailsForm();
    form.setLicences(licenceIds);

    var licence = LicenceTestUtil.Builder()
            .withLicenceId(25)
            .withLicenceRef("ref")
            .build();
    var licenceView = new LicenceView(licence.getLicenceRef(), licence.getId(), licence.getLicenceStatus());

    when(licenceQueryService.getLicencesByIds(licenceIds))
        .thenReturn(List.of(licenceView));

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<Licence>> argumentCaptor = ArgumentCaptor.forClass(List.class);
    licencePersistenceService.saveLicences(form, generalDetail);

    var orderVerifier  = inOrder(licenceRepository);
    orderVerifier.verify(licenceRepository).deleteAllByGeneralDetail(generalDetail);
    orderVerifier.verify(licenceRepository).saveAll(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue())
        .extracting(
            Licence::getGeneralDetail,
            Licence::getLicenceId,
            Licence::getLicenceReference
        )
        .containsExactly(
            tuple(
                generalDetail,
                25,
                "ref"
            )
        );
  }

  @Test
  void hasAnExtantLicence_whenHasExtant() {
    var generalDetail = mock(GeneralDetail.class);
    var extantLicence = new Licence();
    extantLicence.setLicenceId(1);
    var nonExtantLicence = new Licence();
    nonExtantLicence.setLicenceId(2);
    var extantLicenceView = LicenceViewTestUtil.Builder().withLicenceId(1).build();
    when(licenceRepository.findAllByGeneralDetail(generalDetail))
        .thenReturn(List.of(extantLicence, nonExtantLicence));
    when(licenceQueryService.getExtantLicencesByIds(List.of("1", "2")))
        .thenReturn(List.of(extantLicenceView));

    assertThat(licencePersistenceService.hasAnExtantLicence(generalDetail)).isTrue();
  }

  @Test
  void hasAnExtantLicence_whenAllNotExtant() {
    var generalDetail = mock(GeneralDetail.class);
    var nonExtantLicence = new Licence();
    nonExtantLicence.setLicenceId(3);
    var nonExtantLicence2 = new Licence();
    nonExtantLicence2.setLicenceId(4);
    when(licenceRepository.findAllByGeneralDetail(generalDetail))
        .thenReturn(List.of(nonExtantLicence, nonExtantLicence2));
    when(licenceQueryService.getExtantLicencesByIds(List.of("3", "4")))
        .thenReturn(List.of());

    assertThat(licencePersistenceService.hasAnExtantLicence(generalDetail)).isFalse();
  }

  @Test
  void hasAnExtantLicence_whenNoLicences() {
    var generalDetail = mock(GeneralDetail.class);

    when(licenceRepository.findAllByGeneralDetail(generalDetail))
        .thenReturn(List.of());

    assertThat(licencePersistenceService.hasAnExtantLicence(generalDetail)).isFalse();
  }

  @Test
  void getLicencesByGeneralDetail_verifyMethodCall() {
    var genDetail = mock(GeneralDetail.class);
    licencePersistenceService.getLicencesByGeneralDetail(genDetail);
    verify(licenceRepository, times(1)).findAllByGeneralDetail(genDetail);
  }
}