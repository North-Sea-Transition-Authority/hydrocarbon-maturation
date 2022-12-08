package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class GeneralDetailSummaryServiceTest {
  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;
  @Mock
  private QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;

  @InjectMocks
  private GeneralDetailSummaryService generalDetailSummaryService;

  private SubmissionDetail submissionDetail;
  private GeneralDetail generalDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    generalDetail = new GeneralDetail();
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    submissionDetail.setResourceId(resource);
    generalDetail.setSubmissionDetail(submissionDetail);
  }

  @Test
  void getGeneralDetailSummaryFromSubmissionDetail_whenPresent_fullSummary() {
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

    var quadrantBlockRef1 = "1/1";
    var quadrantAndBlock1 = createQuadrantAndBlock(quadrantBlockRef1);
    var quadrantBlockRef2 = "1/2";
    var quadrantAndBlock2 = createQuadrantAndBlock(quadrantBlockRef2);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(List.of(quadrantAndBlock1, quadrantAndBlock2));

    generalDetail.setResourceName("prospect");
    generalDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    generalDetail.setInformationSource(InformationSource.LICENCE_APPLICATION);
    generalDetail.setLicenceApplicationReference("licence application ref");
    generalDetail.setPercentageLocatedInUkcs(BigDecimal.valueOf(2.5));
    generalDetail.setResourceLocatedInUkcs(false);
    generalDetail.setOrganisationName("name");
    generalDetail.setOrganisationRegisteredNumber("25");
    generalDetail.setOrganisationId(25);

    var view = generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            GeneralDetailSummaryView::resourceName,
            GeneralDetailSummaryView::onshoreOffshore,
            GeneralDetailSummaryView::informationSource,
            GeneralDetailSummaryView::percentageLocatedInUkcs,
            GeneralDetailSummaryView::resourceLocatedInUkcs,
            GeneralDetailSummaryView::isLicenceApplicationInfoSource,
            GeneralDetailSummaryView::licenceApplication,
            GeneralDetailSummaryView::quadAndBlockDisplayList,
            GeneralDetailSummaryView::organisationNameAndNumber,
            GeneralDetailSummaryView::resourceType
        )
        .containsExactly(
            generalDetail.getResourceName(),
            generalDetail.getOnshoreOffshore().getDisplayName(),
            generalDetail.getInformationSource().getDisplayName(),
            generalDetail.getPercentageLocatedInUkcs().toString() + "%",
            "No",
            true,
            generalDetail.getLicenceApplicationReference(),
            List.of(quadrantBlockRef1, quadrantBlockRef2),
            "name (25)",
            "prospect"
        );
  }

  private QuadrantAndBlock createQuadrantAndBlock(String quadrantBlockRef) {
    var quadrantAndBlock1 = new QuadrantAndBlock();
    quadrantAndBlock1.setQuadrantBlock(quadrantBlockRef);
    quadrantAndBlock1.setSortKey("0010");
    return quadrantAndBlock1;
  }

  @Test
  void getGeneralDetailSummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

    var view = generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            GeneralDetailSummaryView::resourceName,
            GeneralDetailSummaryView::onshoreOffshore,
            GeneralDetailSummaryView::informationSource,
            GeneralDetailSummaryView::percentageLocatedInUkcs,
            GeneralDetailSummaryView::resourceLocatedInUkcs,
            GeneralDetailSummaryView::isLicenceApplicationInfoSource,
            GeneralDetailSummaryView::licenceApplication,
            GeneralDetailSummaryView::quadAndBlockDisplayList,
            GeneralDetailSummaryView::organisationNameAndNumber,
            GeneralDetailSummaryView::resourceType
        )
        .containsExactly(
            "",
            "",
            "",
            "",
            "",
            false,
            "",
            Collections.emptyList(),
            "",
            "prospect"
        );
  }
}