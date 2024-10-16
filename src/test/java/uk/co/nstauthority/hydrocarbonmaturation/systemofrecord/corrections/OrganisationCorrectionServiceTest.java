package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationNameHistoryDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class OrganisationCorrectionServiceTest {

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;

  @InjectMocks
  private OrganisationCorrectionService organisationCorrectionService;

  @Test
  void updateOrganisationNameForDate_noPreviousOrganisation() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setSubmissionDetail(submissionDetail);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(resourceDetail));
    organisationCorrectionService.updateOrganisationNameForDate(submissionDetail, LocalDate.now());
    verify(resourceDetailsPersistenceService, never()).saveResourceDetail(resourceDetail);
  }

  @Test
  void updateOrganisationNameForDate_withPreviousOrganisation() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    var resourceDetail = new ResourceDetail();
    var organisationUnitId = 1;
    resourceDetail.setOrganisationId(1);
    resourceDetail.setSubmissionDetail(submissionDetail);
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(resourceDetail));
    var organisationName = "name";
    var oldName = "old name";
    var organisationRegisteredNumber = "000001";
    var startDate = LocalDate.of(2012, 12, 1);
    var endDate = LocalDate.of(2012, 12, 10);
    var orgNameHistoryDto = new OrganisationNameHistoryDto(
        oldName,
        startDate,
        endDate
    );
    var organisationUnitDto = new OrganisationUnitDto(
        organisationUnitId,
        organisationName,
        organisationRegisteredNumber,
        List.of(orgNameHistoryDto)
    );
    when(organisationUnitQueryService.findOrganisationUnit(organisationUnitId)).thenReturn(Optional.of(organisationUnitDto));
    var testDate = LocalDate.of(2012, 12, 5);
    when(organisationUnitQueryService.getOrganisationUnitNameForDate(
        organisationUnitDto,
        testDate
    )).thenReturn(Optional.of(oldName));



    organisationCorrectionService.updateOrganisationNameForDate(submissionDetail, testDate);

    assertThat(resourceDetail.getOrganisationName()).isEqualTo(oldName);
    verify(resourceDetailsPersistenceService, times(1)).saveResourceDetail(resourceDetail);
  }
}
