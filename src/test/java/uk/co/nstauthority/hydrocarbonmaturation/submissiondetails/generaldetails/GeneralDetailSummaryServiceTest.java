package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryTestUtil.assertSummarySection;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells.PlannedSanctionedWellsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;

@ExtendWith(MockitoExtension.class)
class GeneralDetailSummaryServiceTest {
  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;
  @Mock
  private QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  @Mock
  private LicencePersistenceService licencePersistenceService;
  @Mock
  private OnshoreOffshoreService onshoreOffshoreService;
  @Mock
  private PlannedSanctionedWellsSummaryService plannedSanctionedWellsSummaryService;
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
            GeneralDetailSummaryView::percentageLocatedInEngland,
            GeneralDetailSummaryView::resourceLocatedInUkcs,
            GeneralDetailSummaryView::resourceLocatedInEngland,
            GeneralDetailSummaryView::isLicenceApplicationInfoSource,
            GeneralDetailSummaryView::licenceApplication,
            GeneralDetailSummaryView::quadAndBlockDisplayList,
            GeneralDetailSummaryView::licenceDisplayList,
            GeneralDetailSummaryView::relinquishmentDate
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
            Collections.emptyList(),
            ""
        );
  }

  @Test
  void getGeneralDetailSummaryFromSubmissionDetail_whenPresent_fullSummary() {
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));

    var quadrantBlockRef1 = "1/1";
    var quadrantAndBlock1 = createQuadrantAndBlock(
        quadrantBlockRef1,
        "1",
        1,
        OnshoreOffshore.OFFSHORE
    );
    var quadrantBlockRef2 = "1/2";
    var quadrantAndBlock2 = createQuadrantAndBlock(
        quadrantBlockRef2,
        "1",
        2,
        OnshoreOffshore.OFFSHORE
    );
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
    generalDetail.setPercentageLocatedInEngland(BigDecimal.valueOf(4.2));
    generalDetail.setResourceLocatedInEngland(false);

    var view = generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            GeneralDetailSummaryView::informationSource,
            GeneralDetailSummaryView::percentageLocatedInUkcs,
            GeneralDetailSummaryView::resourceLocatedInUkcs,
            GeneralDetailSummaryView::percentageLocatedInEngland,
            GeneralDetailSummaryView::resourceLocatedInEngland,
            GeneralDetailSummaryView::isLicenceApplicationInfoSource,
            GeneralDetailSummaryView::licenceApplication,
            GeneralDetailSummaryView::quadAndBlockDisplayList,
            GeneralDetailSummaryView::licenceDisplayList,
            GeneralDetailSummaryView::resourceType,
            GeneralDetailSummaryView::relinquishmentDate
        )
        .containsExactly(
            generalDetail.getInformationSource().getDisplayName(),
            generalDetail.getPercentageLocatedInUkcs().toString() + "%",
            "No",
            generalDetail.getPercentageLocatedInEngland().toString() + "%",
            "No",
            true,
            generalDetail.getLicenceApplicationReference(),
            List.of(quadrantBlockRef1, quadrantBlockRef2),
            List.of(licence1.getLicenceReference(), licence2.getLicenceReference()),
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
    var quadrantAndBlock1 = createQuadrantAndBlock(
        quadrantBlockRef1,
        "1",
        1,
        OnshoreOffshore.OFFSHORE
    );
    var quadrantBlockRef2 = "1/2";
    var quadrantAndBlock2 = createQuadrantAndBlock(
        quadrantBlockRef2,
        "1",
        2,
        OnshoreOffshore.OFFSHORE
    );
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
    generalDetail.setPercentageLocatedInEngland(BigDecimal.valueOf(4.2));
    generalDetail.setResourceLocatedInEngland(false);
    generalDetail.setRelinquishmentDate(LocalDate.of(2012, 6, 12));

    var view = generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            GeneralDetailSummaryView::informationSource,
            GeneralDetailSummaryView::percentageLocatedInUkcs,
            GeneralDetailSummaryView::resourceLocatedInUkcs,
            GeneralDetailSummaryView::percentageLocatedInEngland,
            GeneralDetailSummaryView::resourceLocatedInEngland,
            GeneralDetailSummaryView::isLicenceApplicationInfoSource,
            GeneralDetailSummaryView::licenceApplication,
            GeneralDetailSummaryView::quadAndBlockDisplayList,
            GeneralDetailSummaryView::licenceDisplayList,
            GeneralDetailSummaryView::resourceType,
            GeneralDetailSummaryView::relinquishmentDate
        )
        .containsExactly(
            generalDetail.getInformationSource().getDisplayName(),
            generalDetail.getPercentageLocatedInUkcs().toString() + "%",
            "No",
            generalDetail.getPercentageLocatedInEngland().toString() + "%",
            "No",
            true,
            generalDetail.getLicenceApplicationReference(),
            List.of(quadrantBlockRef1, quadrantBlockRef2),
            List.of(licence1.getLicenceReference(), licence2.getLicenceReference()),
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

  @Test
  void getSummarySection_generalDetailsSummary_forField_offshore_notFullyInUkcs() {
    submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    generalDetail.setResourceLocatedInUkcs(false);
    generalDetail.setPercentageLocatedInUkcs(BigDecimal.valueOf(99));
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    var licenceRef1 = "Licence 1";
    var licence1 = createLicence(licenceRef1);
    var licenceRef2 = "Licence 2";
    var licence2 = createLicence(licenceRef2);
    var licenceList = List.of(licence1, licence2);
    when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail)).thenReturn(licenceList);
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.OFFSHORE);
    var quadBlockRef1 = "1/2";
    var quadBlockRef2 = "3/4";
    var quadrantBlock1 = createQuadrantAndBlock(quadBlockRef1, "1", 2, OnshoreOffshore.OFFSHORE);
    var quadrantBlock2 = createQuadrantAndBlock(quadBlockRef2, "3", 4, OnshoreOffshore.OFFSHORE);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(List.of(quadrantBlock1, quadrantBlock2));
    generalDetail.setCurrentDriveMechanism(DriveMechanism.DEPLETION);

    var summarySectionOptional = generalDetailSummaryService.getSummarySection(submissionDetail);
    assertThat(summarySectionOptional).isPresent();
    var summarySection = summarySectionOptional.get();
    assertSummarySection(summarySection, GeneralDetailSummaryService.GENERAL_DETAIL_SECTION_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        GeneralDetailSummaryService.LICENCES,
        String.join(", ", List.of(licenceRef1, licenceRef2))
    );
    summaryDataView.addKeyValue(GeneralDetailSummaryService.WHOLLY_WITHIN_UKCS, generalDetail.getResourceLocatedInUkcs());
    summaryDataView.addKeyValue(GeneralDetailSummaryService.PERCENT_WITHIN_UKCS, generalDetail.getPercentageLocatedInUkcs());
    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.QUADRANT_AND_BLOCKS,
        String.join(", ", List.of(quadBlockRef1, quadBlockRef2))
    );
    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.CURRENT_DRIVE_MECHANISM, generalDetail.getCurrentDriveMechanism().getDisplayName()
    );

    var summaryCard = SummaryCard.simpleSummaryCardWithHeading(GeneralDetailSummaryService.GENERAL_DETAILS, summaryDataView);
    assertThat(summarySection.summaryItems().get(0).summaryCards()).contains(summaryCard);
  }

  @Test
  void getSummarySection_generalDetailsSummary_forField_offshore_fullyInUkcs() {
    submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    generalDetail.setResourceLocatedInUkcs(true);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    var licenceRef1 = "Licence 1";
    var licence1 = createLicence(licenceRef1);
    var licenceRef2 = "Licence 2";
    var licence2 = createLicence(licenceRef2);
    var licenceList = List.of(licence1, licence2);
    when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail)).thenReturn(licenceList);
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.OFFSHORE);
    var quadBlockRef1 = "1/2";
    var quadBlockRef2 = "3/4";
    var quadrantBlock1 = createQuadrantAndBlock(quadBlockRef1, "1", 2, OnshoreOffshore.OFFSHORE);
    var quadrantBlock2 = createQuadrantAndBlock(quadBlockRef2, "3", 4, OnshoreOffshore.OFFSHORE);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(List.of(quadrantBlock1, quadrantBlock2));
    generalDetail.setCurrentDriveMechanism(DriveMechanism.DEPLETION);

    var summarySectionOptional = generalDetailSummaryService.getSummarySection(submissionDetail);
    assertThat(summarySectionOptional).isPresent();
    var summarySection = summarySectionOptional.get();
    assertSummarySection(summarySection, GeneralDetailSummaryService.GENERAL_DETAIL_SECTION_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        GeneralDetailSummaryService.LICENCES,
        String.join(", ", List.of(licenceRef1, licenceRef2))
    );
    summaryDataView.addKeyValue(GeneralDetailSummaryService.WHOLLY_WITHIN_UKCS, generalDetail.getResourceLocatedInUkcs());
    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.QUADRANT_AND_BLOCKS,
        String.join(", ", List.of(quadBlockRef1, quadBlockRef2))
    );
    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.CURRENT_DRIVE_MECHANISM, generalDetail.getCurrentDriveMechanism().getDisplayName()
    );

    var summaryCard = SummaryCard.simpleSummaryCardWithHeading(GeneralDetailSummaryService.GENERAL_DETAILS, summaryDataView);
    assertThat(summarySection.summaryItems().get(0).summaryCards()).contains(summaryCard);
  }

  @Test
  void getSummarySection_generalDetailsSummary_forField_onshore_notFullyInEngland() {
    submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    generalDetail.setResourceLocatedInEngland(false);
    generalDetail.setPercentageLocatedInEngland(BigDecimal.valueOf(99));
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    var licenceRef1 = "Licence 1";
    var licence1 = createLicence(licenceRef1);
    var licenceRef2 = "Licence 2";
    var licence2 = createLicence(licenceRef2);
    var licenceList = List.of(licence1, licence2);
    when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail)).thenReturn(licenceList);
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.ONSHORE);
    var quadBlockRef1 = "1/2";
    var quadBlockRef2 = "3/4";
    var quadrantBlock1 = createQuadrantAndBlock(quadBlockRef1, "1", 2, OnshoreOffshore.ONSHORE);
    var quadrantBlock2 = createQuadrantAndBlock(quadBlockRef2, "3", 4, OnshoreOffshore.ONSHORE);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(List.of(quadrantBlock1, quadrantBlock2));
    generalDetail.setCurrentDriveMechanism(DriveMechanism.DEPLETION);

    var summarySectionOptional = generalDetailSummaryService.getSummarySection(submissionDetail);
    assertThat(summarySectionOptional).isPresent();
    var summarySection = summarySectionOptional.get();
    assertSummarySection(summarySection, GeneralDetailSummaryService.GENERAL_DETAIL_SECTION_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        GeneralDetailSummaryService.LICENCES,
        String.join(", ", List.of(licenceRef1, licenceRef2))
    );
    summaryDataView.addKeyValue(GeneralDetailSummaryService.WHOLLY_WITHIN_ENGLAND, generalDetail.getResourceLocatedInEngland());
    summaryDataView.addKeyValue(GeneralDetailSummaryService.PERCENT_WITHIN_ENGLAND, generalDetail.getPercentageLocatedInEngland());
    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.QUADRANT_AND_BLOCKS,
        String.join(", ", List.of(quadBlockRef1, quadBlockRef2))
    );
    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.CURRENT_DRIVE_MECHANISM, generalDetail.getCurrentDriveMechanism().getDisplayName()
    );

    var summaryCard = SummaryCard.simpleSummaryCardWithHeading(GeneralDetailSummaryService.GENERAL_DETAILS, summaryDataView);
    assertThat(summarySection.summaryItems().get(0).summaryCards()).contains(summaryCard);
  }

  @Test
  void getSummarySection_generalDetailsSummary_forField_onshore_fullyInEngland() {
    submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    generalDetail.setResourceLocatedInEngland(true);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    var licenceRef1 = "Licence 1";
    var licence1 = createLicence(licenceRef1);
    var licenceRef2 = "Licence 2";
    var licence2 = createLicence(licenceRef2);
    var licenceList = List.of(licence1, licence2);
    when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail)).thenReturn(licenceList);
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.ONSHORE);
    var quadBlockRef1 = "1/2";
    var quadBlockRef2 = "3/4";
    var quadrantBlock1 = createQuadrantAndBlock(quadBlockRef1, "1", 2, OnshoreOffshore.ONSHORE);
    var quadrantBlock2 = createQuadrantAndBlock(quadBlockRef2, "3", 4, OnshoreOffshore.ONSHORE);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(List.of(quadrantBlock1, quadrantBlock2));
    generalDetail.setCurrentDriveMechanism(DriveMechanism.COMBINED);

    var summarySectionOptional = generalDetailSummaryService.getSummarySection(submissionDetail);
    assertThat(summarySectionOptional).isPresent();
    var summarySection = summarySectionOptional.get();
    assertSummarySection(summarySection, GeneralDetailSummaryService.GENERAL_DETAIL_SECTION_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        GeneralDetailSummaryService.LICENCES,
        String.join(", ", List.of(licenceRef1, licenceRef2))
    );
    summaryDataView.addKeyValue(GeneralDetailSummaryService.WHOLLY_WITHIN_ENGLAND, generalDetail.getResourceLocatedInEngland());
    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.QUADRANT_AND_BLOCKS,
        String.join(", ", List.of(quadBlockRef1, quadBlockRef2))
    );

    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.CURRENT_DRIVE_MECHANISM, generalDetail.getCurrentDriveMechanism().getDisplayName()
    );

    var summaryCard = SummaryCard.simpleSummaryCardWithHeading(GeneralDetailSummaryService.GENERAL_DETAILS, summaryDataView);
    assertThat(summarySection.summaryItems().get(0).summaryCards()).contains(summaryCard);
  }

  @Test
  void getSummarySection_generalDetailsSummary_Cop() {
    submissionDetail = SubmissionDetailTestUtils.createNewCopFieldSubmissionDetail();
    generalDetail.setResourceLocatedInEngland(true);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    var licenceRef1 = "Licence 1";
    var licence1 = createLicence(licenceRef1);
    var licenceRef2 = "Licence 2";
    var licence2 = createLicence(licenceRef2);
    var licenceList = List.of(licence1, licence2);
    when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail)).thenReturn(licenceList);
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.ONSHORE);
    var quadBlockRef1 = "1/2";
    var quadBlockRef2 = "3/4";
    var quadrantBlock1 = createQuadrantAndBlock(quadBlockRef1, "1", 2, OnshoreOffshore.ONSHORE);
    var quadrantBlock2 = createQuadrantAndBlock(quadBlockRef2, "3", 4, OnshoreOffshore.ONSHORE);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(List.of(quadrantBlock1, quadrantBlock2));

    generalDetail.setFieldStoppedProducingDate(LocalDate.of(1999, 5, 1));
    generalDetail.setCurrentDriveMechanism(DriveMechanism.OTHER);
    generalDetail.setOtherDriveMechanism("test");

    var summarySectionOptional = generalDetailSummaryService.getSummarySection(submissionDetail);
    assertThat(summarySectionOptional).isPresent();
    var summarySection = summarySectionOptional.get();
    assertSummarySection(summarySection, GeneralDetailSummaryService.GENERAL_DETAIL_SECTION_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        GeneralDetailSummaryService.LICENCES,
        String.join(", ", List.of(licenceRef1, licenceRef2))
    );
    summaryDataView.addKeyValue(GeneralDetailSummaryService.WHOLLY_WITHIN_ENGLAND, generalDetail.getResourceLocatedInEngland());
    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.QUADRANT_AND_BLOCKS,
        String.join(", ", List.of(quadBlockRef1, quadBlockRef2))
    );

    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.FIELD_STOPPED_PRODUCING_DATE, "1 May 1999"
    );

    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.CURRENT_DRIVE_MECHANISM, generalDetail.getOtherDriveMechanism()
    );

    var summaryCard = SummaryCard.simpleSummaryCardWithHeading(GeneralDetailSummaryService.GENERAL_DETAILS, summaryDataView);
    assertThat(summarySection.summaryItems().get(0).summaryCards()).contains(summaryCard);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FDP", "CREATE_FDPA"}
  )
  void getSummarySection_generalDetailsSummary_FdpOrFdpa(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    generalDetail.setResourceLocatedInEngland(true);
    when(plannedSanctionedWellsSummaryService.getPlannedSanctionedWellsCard(submissionDetail))
        .thenReturn(SummaryCard.emptySummaryCard());
    when(plannedSanctionedWellsSummaryService.getWellTypesSummaryCard(submissionDetail))
        .thenReturn(SummaryCard.emptySummaryCard());
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(generalDetail));
    var licenceRef1 = "Licence 1";
    var licence1 = createLicence(licenceRef1);
    var licenceRef2 = "Licence 2";
    var licence2 = createLicence(licenceRef2);
    var licenceList = List.of(licence1, licence2);
    when(licencePersistenceService.getLicencesByGeneralDetail(generalDetail)).thenReturn(licenceList);
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.ONSHORE);
    var quadBlockRef1 = "1/2";
    var quadBlockRef2 = "3/4";
    var quadrantBlock1 = createQuadrantAndBlock(quadBlockRef1, "1", 2, OnshoreOffshore.ONSHORE);
    var quadrantBlock2 = createQuadrantAndBlock(quadBlockRef2, "3", 4, OnshoreOffshore.ONSHORE);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(List.of(quadrantBlock1, quadrantBlock2));

    generalDetail.setCapitalProjectScopeDelivered("test");
    generalDetail.setCurrentDriveMechanism(DriveMechanism.OTHER);
    generalDetail.setOtherDriveMechanism("test");

    var summarySectionOptional = generalDetailSummaryService.getSummarySection(submissionDetail);
    assertThat(summarySectionOptional).isPresent();
    var summarySection = summarySectionOptional.get();
    assertSummarySection(summarySection, GeneralDetailSummaryService.GENERAL_DETAIL_SECTION_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        GeneralDetailSummaryService.LICENCES,
        String.join(", ", List.of(licenceRef1, licenceRef2))
    );
    summaryDataView.addKeyValue(GeneralDetailSummaryService.WHOLLY_WITHIN_ENGLAND, generalDetail.getResourceLocatedInEngland());
    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.QUADRANT_AND_BLOCKS,
        String.join(", ", List.of(quadBlockRef1, quadBlockRef2))
    );

    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.CAPITAL_PROJECT_SCOPE_DELIVERED, generalDetail.getCapitalProjectScopeDelivered()
    );
    summaryDataView.addKeyValue(
        GeneralDetailSummaryService.CURRENT_DRIVE_MECHANISM, generalDetail.getOtherDriveMechanism()
    );

    var summaryCard = SummaryCard.simpleSummaryCardWithHeading(GeneralDetailSummaryService.GENERAL_DETAILS, summaryDataView);
    assertThat(summarySection.summaryItems().get(0).summaryCards()).contains(summaryCard);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {
          "CREATE_FDP",
          "CREATE_FDPA"})
  void getSummarySection_assertCards_fdpAndFdpa(SubmissionType submissionType) {
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var summaryCard1 = SummaryCard.emptySummaryCard();
    var summaryCard2 = SummaryCard.emptySummaryCard();
    var summaryCard3 = SummaryCard.emptySummaryCard();
    var summaryCard4 = SummaryCard.emptySummaryCard();
    var summaryCard5 = SummaryCard.emptySummaryCard();

    when(plannedSanctionedWellsSummaryService.getPlannedSanctionedWellsCard(submissionDetail))
        .thenReturn(summaryCard1);
    when(plannedSanctionedWellsSummaryService.getWellTypesSummaryCard(submissionDetail))
        .thenReturn(summaryCard2);
    when(plannedSanctionedWellsSummaryService.getHoleCompletionTypesSummaryCard(submissionDetail))
        .thenReturn(summaryCard3);
    when(plannedSanctionedWellsSummaryService.getPlannedCompletionTypesSummaryCard(submissionDetail))
        .thenReturn(summaryCard4);
    when(plannedSanctionedWellsSummaryService.getArtificialLiftMechanismsSummaryCard(submissionDetail))
        .thenReturn(summaryCard5);

    var summaryCards = generalDetailSummaryService.getSummarySection(submissionDetail).get().summaryItems()
        .get(0).summaryCards();
    // 5 from planned sanctioned wells + 1 from general details
    assertThat(summaryCards)
        .hasSize(6)
        .contains(
            summaryCard1,
            summaryCard2,
            summaryCard2,
            summaryCard3,
            summaryCard4,
            summaryCard5
        );

    verify(plannedSanctionedWellsSummaryService, times(1)).getPlannedSanctionedWellsCard(submissionDetail);
    verify(plannedSanctionedWellsSummaryService, times(1)).getWellTypesSummaryCard(submissionDetail);
    verify(plannedSanctionedWellsSummaryService, times(1)).getPlannedCompletionTypesSummaryCard(submissionDetail);
    verify(plannedSanctionedWellsSummaryService, times(1)).getHoleCompletionTypesSummaryCard(submissionDetail);
    verify(plannedSanctionedWellsSummaryService, times(1)).getArtificialLiftMechanismsSummaryCard(submissionDetail);
    verifyNoMoreInteractions(plannedSanctionedWellsSummaryService);
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class,
      names = {"CREATE_CSR", "CREATE_COP"})
  void getSummarySection_assertCards_fieldNotFdpAndFdpa(SubmissionType submissionType) {
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var summaryCards = generalDetailSummaryService.getSummarySection(submissionDetail).get().summaryItems()
        .get(0).summaryCards();
    assertThat(summaryCards).hasSize(1);

    verify(plannedSanctionedWellsSummaryService, never()).getPlannedSanctionedWellsCard(submissionDetail);
    verify(plannedSanctionedWellsSummaryService, never()).getWellTypesSummaryCard(submissionDetail);
    verifyNoInteractions(plannedSanctionedWellsSummaryService);
  }

  private QuadrantAndBlock createQuadrantAndBlock(
      String quadrantBlockRef,
      String quad,
      Integer block,
      OnshoreOffshore onshoreOffshore
  ) {
    var quadrantAndBlock = new QuadrantAndBlock();
    quadrantAndBlock.setQuadrantBlockReference(quadrantBlockRef);
    quadrantAndBlock.setQuadrant(quad);
    quadrantAndBlock.setBlock(block);
    quadrantAndBlock.setOnshoreOffshore(onshoreOffshore);
    return quadrantAndBlock;
  }

  private Licence createLicence(String licenceRef) {
    var licence = new Licence();
    licence.setLicenceReference(licenceRef);
    return licence;
  }
}