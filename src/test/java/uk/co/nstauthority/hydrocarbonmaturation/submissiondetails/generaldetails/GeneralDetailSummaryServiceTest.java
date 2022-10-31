package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class GeneralDetailSummaryServiceTest {
  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @InjectMocks
  private GeneralDetailSummaryService generalDetailSummaryService;

  private SubmissionDetail submissionDetail;
  private GeneralDetail generalDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);
  }

  @Test
  void getGeneralDetailSummaryFromSubmissionDetail_whenPresent_fullSummary() {
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    generalDetail.setProspectName("prospect");
    generalDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    generalDetail.setInformationSource(InformationSource.LICENCE_APPLICATION);
    generalDetail.setLicenceApplicationReference("licence application ref");
    generalDetail.setPercentageLocatedInUkcs(BigDecimal.valueOf(2.5));
    generalDetail.setProspectLocatedInUkcs(false);
    var view = generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            GeneralDetailSummaryView::prospectName,
            GeneralDetailSummaryView::onshoreOffshore,
            GeneralDetailSummaryView::informationSource,
            GeneralDetailSummaryView::percentageLocatedInUkcs,
            GeneralDetailSummaryView::prospectLocatedInUkcs,
            GeneralDetailSummaryView::isLicenceApplicationInfoSource,
            GeneralDetailSummaryView::licenceApplication)
        .containsExactly(
            generalDetail.getProspectName(),
            generalDetail.getOnshoreOffshore().getDisplayName(),
            generalDetail.getInformationSource().getDisplayName(),
            generalDetail.getPercentageLocatedInUkcs().toString() + "%",
            "No",
            true,
            generalDetail.getLicenceApplicationReference()
        );
  }

  @Test
  void getGeneralDetailSummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

    var view = generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            GeneralDetailSummaryView::prospectName,
            GeneralDetailSummaryView::onshoreOffshore,
            GeneralDetailSummaryView::informationSource,
            GeneralDetailSummaryView::percentageLocatedInUkcs,
            GeneralDetailSummaryView::prospectLocatedInUkcs,
            GeneralDetailSummaryView::isLicenceApplicationInfoSource,
            GeneralDetailSummaryView::licenceApplication)
        .containsExactly(
            "",
            "",
            "",
            "",
            "",
            false,
            ""
        );
  }
}