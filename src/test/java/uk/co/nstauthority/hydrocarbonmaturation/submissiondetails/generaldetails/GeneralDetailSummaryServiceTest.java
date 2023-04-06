package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class GeneralDetailSummaryServiceTest {
  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;
  @Mock
  private QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  @Mock
  private LicencePersistenceService licencePersistenceService;

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
    submissionDetail.getSubmission().setResource(resource);
    generalDetail.setSubmissionDetail(submissionDetail);
  }

  @Test
  void getGeneralDetailSummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    submissionDetail.getSubmission().setResource(resource);
    generalDetail.setSubmissionDetail(submissionDetail);
    var view = generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            GeneralDetailSummaryView::informationSource,
            GeneralDetailSummaryView::percentageLocatedInUkcs,
            GeneralDetailSummaryView::resourceLocatedInUkcs,
            GeneralDetailSummaryView::isLicenceApplicationInfoSource,
            GeneralDetailSummaryView::licenceApplication,
            GeneralDetailSummaryView::quadAndBlockDisplayList,
            GeneralDetailSummaryView::isResourceOnALicence,
            GeneralDetailSummaryView::showLicences,
            GeneralDetailSummaryView::licenceDisplayList,
            GeneralDetailSummaryView::relinquishmentDate
        )
        .containsExactly(
            "",
            "",
            "",
            false,
            "",
            Collections.emptyList(),
            "",
            true,
            Collections.emptyList(),
            ""
        );
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

    var licence1Ref = "ALF/1";
    var licence1 = createLicence(licence1Ref);
    var licence2Ref = "LOL/2";
    var licence2 = createLicence(licence2Ref);
    when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
        .thenReturn(List.of(licence1, licence2));
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    submissionDetail.getSubmission().setResource(resource);
    generalDetail.setSubmissionDetail(submissionDetail);
    generalDetail.setInformationSource(InformationSource.LICENCE_APPLICATION);
    generalDetail.setLicenceApplicationReference("licence application ref");
    generalDetail.setPercentageLocatedInUkcs(BigDecimal.valueOf(2.5));
    generalDetail.setResourceLocatedInUkcs(false);
    generalDetail.setResourceOnALicence(true);

    var view = generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            GeneralDetailSummaryView::informationSource,
            GeneralDetailSummaryView::percentageLocatedInUkcs,
            GeneralDetailSummaryView::resourceLocatedInUkcs,
            GeneralDetailSummaryView::isLicenceApplicationInfoSource,
            GeneralDetailSummaryView::licenceApplication,
            GeneralDetailSummaryView::quadAndBlockDisplayList,
            GeneralDetailSummaryView::isResourceOnALicence,
            GeneralDetailSummaryView::licenceDisplayList,
            GeneralDetailSummaryView::showLicences,
            GeneralDetailSummaryView::resourceType,
            GeneralDetailSummaryView::relinquishmentDate
        )
        .containsExactly(
            generalDetail.getInformationSource().getDisplayName(),
            generalDetail.getPercentageLocatedInUkcs().toString() + "%",
            "No",
            true,
            generalDetail.getLicenceApplicationReference(),
            List.of(quadrantBlockRef1, quadrantBlockRef2),
            "Yes",
            List.of(licence1.getLicenceReference(), licence2.getLicenceReference()),
            true,
            "prospect",
            ""
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"RELINQUISHMENT_PROSPECT", "RELINQUISHMENT_DISCOVERY"})
  void getGeneralDetailSummaryFromSubmissionDetail_whenRelinquishment_whenPresent_fullSummary(
      SubmissionType submissionType) {
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

    var quadrantBlockRef1 = "1/1";
    var quadrantAndBlock1 = createQuadrantAndBlock(quadrantBlockRef1);
    var quadrantBlockRef2 = "1/2";
    var quadrantAndBlock2 = createQuadrantAndBlock(quadrantBlockRef2);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(List.of(quadrantAndBlock1, quadrantAndBlock2));

    var licence1Ref = "ALF/1";
    var licence1 = createLicence(licence1Ref);
    var licence2Ref = "LOL/2";
    var licence2 = createLicence(licence2Ref);
    when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail))
        .thenReturn(List.of(licence1, licence2));
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    submissionDetail.getSubmission().setResource(resource);
    submissionDetail.setSubmissionType(submissionType);
    generalDetail.setSubmissionDetail(submissionDetail);
    generalDetail.setInformationSource(InformationSource.LICENCE_APPLICATION);
    generalDetail.setLicenceApplicationReference("licence application ref");
    generalDetail.setPercentageLocatedInUkcs(BigDecimal.valueOf(2.5));
    generalDetail.setResourceLocatedInUkcs(false);
    generalDetail.setResourceOnALicence(true);
    generalDetail.setRelinquishmentDate(LocalDate.of(2012, 6, 12));

    var view = generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            GeneralDetailSummaryView::informationSource,
            GeneralDetailSummaryView::percentageLocatedInUkcs,
            GeneralDetailSummaryView::resourceLocatedInUkcs,
            GeneralDetailSummaryView::isLicenceApplicationInfoSource,
            GeneralDetailSummaryView::licenceApplication,
            GeneralDetailSummaryView::quadAndBlockDisplayList,
            GeneralDetailSummaryView::isResourceOnALicence,
            GeneralDetailSummaryView::licenceDisplayList,
            GeneralDetailSummaryView::showLicences,
            GeneralDetailSummaryView::resourceType,
            GeneralDetailSummaryView::relinquishmentDate
        )
        .containsExactly(
            generalDetail.getInformationSource().getDisplayName(),
            generalDetail.getPercentageLocatedInUkcs().toString() + "%",
            "No",
            true,
            generalDetail.getLicenceApplicationReference(),
            List.of(quadrantBlockRef1, quadrantBlockRef2),
            "Yes",
            List.of(licence1.getLicenceReference(), licence2.getLicenceReference()),
            true,
            "prospect",
            "12 Jun 2012"
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"RELINQUISHMENT_PROSPECT", "RELINQUISHMENT_DISCOVERY"})
  void getGeneralDetailSummaryFromSubmissionDetail_whenRelinquishment_whenEmptyDate_emptySummary(
      SubmissionType submissionType) {
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    submissionDetail.getSubmission().setResource(resource);
    submissionDetail.setSubmissionType(submissionType);
    generalDetail.setSubmissionDetail(submissionDetail);
    generalDetail.setRelinquishmentDate(null);

    var view = generalDetailSummaryService
        .getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            GeneralDetailSummaryView::relinquishmentDate)
        .isEqualTo("");
  }


  private QuadrantAndBlock createQuadrantAndBlock(String quadrantBlockRef) {
    var quadrantAndBlock1 = new QuadrantAndBlock();
    quadrantAndBlock1.setQuadrantBlock(quadrantBlockRef);
    quadrantAndBlock1.setSortKey("0010");
    return quadrantAndBlock1;
  }

  private Licence createLicence(String licenceRef) {
    var licence = new Licence();
    licence.setLicenceReference(licenceRef);
    return licence;
  }
}