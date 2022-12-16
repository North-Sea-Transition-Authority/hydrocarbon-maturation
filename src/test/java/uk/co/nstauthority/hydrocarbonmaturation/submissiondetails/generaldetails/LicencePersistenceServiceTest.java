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
  void getLicencesByGeneralDetail_verifyMethodCall() {
    var genDetail = mock(GeneralDetail.class);
    licencePersistenceService.getLicencesByGeneralDetail(genDetail);
    verify(licenceRepository, times(1)).findAllByGeneralDetail(genDetail);
  }
}