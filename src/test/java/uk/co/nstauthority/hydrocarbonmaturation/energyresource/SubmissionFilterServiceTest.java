package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;

@ExtendWith(MockitoExtension.class)
class SubmissionFilterServiceTest {

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @Mock
  private SubmissionPersistenceService submissionPersistenceService;

  @InjectMocks
  private SubmissionFilterService submissionFilterService;

  @Nested
  class WhenFilterDiscoverySubmissionByOtherSubmissions {

    @ParameterizedTest
    @EnumSource(
        mode = EnumSource.Mode.INCLUDE,
        value = SubmissionType.class,
        names = {"UPDATE_DISCOVERY", "RELINQUISHMENT_DISCOVERY"})
    void filterDiscoverySubmissionByOtherSubmissions_whenRelinquishmentPresent_assertFalse(SubmissionType submissionType) {
      var submissionToBeFiltered = SubmissionDetailTestUtils.createSubmissionDetail(
          SubmissionType.CREATE_DISCOVERY);
      var otherSubmissionsOnResource = List.of(
          SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.RELINQUISHMENT_DISCOVERY));
      when(submissionDetailService.getExtantSubmissionDetailsForResource(
          submissionToBeFiltered.getSubmission().getResource()))
          .thenReturn(otherSubmissionsOnResource);

      var result = submissionFilterService
          .filterDiscoverySubmissionByOtherSubmissions(submissionToBeFiltered, submissionType);

      assertThat(result).isFalse();
    }

    @ParameterizedTest
    @EnumSource(
        mode = EnumSource.Mode.INCLUDE,
        value = SubmissionType.class,
        names = {"UPDATE_DISCOVERY", "RELINQUISHMENT_DISCOVERY"})
    void filterDiscoverySubmissionByOtherSubmissions_whenUpdatePresent_assertFalse(SubmissionType submissionType) {
      var submissionToBeFiltered = SubmissionDetailTestUtils.createSubmissionDetail(
          SubmissionType.CREATE_DISCOVERY);
      var otherSubmissionsOnResource = List.of(
          SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.UPDATE_DISCOVERY));
      when(submissionDetailService.getExtantSubmissionDetailsForResource(submissionToBeFiltered.getSubmission().getResource()))
          .thenReturn(otherSubmissionsOnResource);
      var result = submissionFilterService
          .filterDiscoverySubmissionByOtherSubmissions(submissionToBeFiltered, submissionType);

      assertThat(result).isFalse();
    }

    @ParameterizedTest
    @EnumSource(SubmissionType.class)
    void filterDiscoverySubmissionByOtherSubmissions_whenSubmissionTypePresent_WhenUpdate_assertOnlyValidTrue(
        SubmissionType submissionType) {
      var submissionToBeFiltered = SubmissionDetailTestUtils.createSubmissionDetail(
          SubmissionType.UPDATE_DISCOVERY);
      var otherSubmissionsOnResource = List.of(
          SubmissionDetailTestUtils.createSubmissionDetail(submissionType));
      when(submissionDetailService.getExtantSubmissionDetailsForResource(
          submissionToBeFiltered.getSubmission().getResource()))
          .thenReturn(otherSubmissionsOnResource);
      var result = submissionFilterService
          .filterDiscoverySubmissionByOtherSubmissions(submissionToBeFiltered, SubmissionType.UPDATE_DISCOVERY);

      if (submissionType == SubmissionType.UPDATE_DISCOVERY
          || submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY) {
        //assert only the presence of a draft update or relinquishment discovery prevents a new update discovery.
        assertThat(result).isFalse();
      } else {
        assertThat(result).isTrue();
      }
    }

    @ParameterizedTest
    @EnumSource(SubmissionType.class)
    void filterDiscoverySubmissionByOtherSubmissions_whenDeletedSubmissionTypePresent_WhenUpdate_assertTrue(
        SubmissionType submissionType) {
      var submissionToBeFiltered = SubmissionDetailTestUtils.createSubmissionDetail(
          SubmissionType.UPDATE_DISCOVERY);
      var otherSubmissionOnResource = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
      otherSubmissionOnResource.setStatus(SubmissionDetailStatus.DELETED);
      var otherSubmissionsOnResource = List.of(otherSubmissionOnResource);
      when(submissionDetailService.getExtantSubmissionDetailsForResource(
          submissionToBeFiltered.getSubmission().getResource()))
          .thenReturn(otherSubmissionsOnResource);
      var result = submissionFilterService
          .filterDiscoverySubmissionByOtherSubmissions(submissionToBeFiltered, SubmissionType.UPDATE_DISCOVERY);
      assertThat(result).isTrue();
    }
  }
}