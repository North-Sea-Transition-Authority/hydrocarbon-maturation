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

@ExtendWith(MockitoExtension.class)
class SubmissionFilterServiceTest {

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private ResourceLinkService resourceLinkService;

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
      when(submissionDetailService.getSubmissionDetailsForResource(
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
      when(submissionDetailService.getSubmissionDetailsForResource(submissionToBeFiltered.getSubmission().getResource()))
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
      when(submissionDetailService.getSubmissionDetailsForResource(
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
      when(submissionDetailService.getSubmissionDetailsForResource(
          submissionToBeFiltered.getSubmission().getResource()))
          .thenReturn(otherSubmissionsOnResource);
      var result = submissionFilterService
          .filterDiscoverySubmissionByOtherSubmissions(submissionToBeFiltered, SubmissionType.UPDATE_DISCOVERY);
      assertThat(result).isTrue();
    }
  }

  @Nested
  class FilterProspectSubmissionByOtherSubmissions {

    @ParameterizedTest
    @EnumSource(SubmissionType.class)
    void filterProspectSubmissionByOtherSubmissions_whenDeletedSubmissionTypePresent_whenUpdate_assertTrue(
        SubmissionType submissionType) {
      var submissionToBeFiltered = SubmissionDetailTestUtils.createSubmissionDetail(
          SubmissionType.PROSPECT_UPDATE);
      var otherSubmissionOnResource = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
      otherSubmissionOnResource.setStatus(SubmissionDetailStatus.DELETED);
      var otherSubmissionsOnResource = List.of(otherSubmissionOnResource);
      when(submissionDetailService.getSubmissionDetailsForResource(
          submissionToBeFiltered.getSubmission().getResource()))
          .thenReturn(otherSubmissionsOnResource);
      var result = submissionFilterService
          .filterProspectSubmissionByOtherSubmissions(submissionToBeFiltered, SubmissionType.PROSPECT_UPDATE);
      assertThat(result).isTrue();
    }

    @ParameterizedTest
    @EnumSource(SubmissionType.class)
    void filterProspectSubmissionByOtherSubmissions_whenSubmissionTypePresent_WhenUpdate_assertOnlyValidTrue(
        SubmissionType submissionType) {
      var submissionToBeFiltered = SubmissionDetailTestUtils.createSubmissionDetail(
          SubmissionType.PROSPECT_UPDATE);
      var otherSubmissionOnResource = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
      var otherSubmissionsOnResource = List.of(otherSubmissionOnResource);
      when(submissionDetailService.getSubmissionDetailsForResource(
          submissionToBeFiltered.getSubmission().getResource()))
          .thenReturn(otherSubmissionsOnResource);
      var result = submissionFilterService
          .filterProspectSubmissionByOtherSubmissions(submissionToBeFiltered, SubmissionType.PROSPECT_UPDATE);

      if (submissionType == SubmissionType.PROSPECT_UPDATE
          || submissionType == SubmissionType.POST_DRILL_DRY_HOLE
          || submissionType == SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT
          || submissionType == SubmissionType.RELINQUISHMENT_PROSPECT) {
        assertThat(result).isFalse();
      } else {
        assertThat(result).isTrue();
      }
    }

    @ParameterizedTest
    @EnumSource(
        mode = EnumSource.Mode.INCLUDE,
        value = SubmissionType.class,
        names = {
            "PROSPECT_UPDATE", "POST_DRILL_DRY_HOLE",
            "CREATE_DISCOVERY_FROM_PROSPECT", "RELINQUISHMENT_PROSPECT"})
    void filterProspectSubmissionByOtherSubmissions_whenLinkedDiscovery_WhenStartingProspectSubmission(
        SubmissionType submissionType) {
      var submissionToBeFiltered = SubmissionDetailTestUtils.createSubmissionDetail(
          SubmissionType.PROSPECT_UPDATE);
      var resourceLink = new ResourceLink();
      var discoveryResource = new Resource();
      discoveryResource.setType(ResourceType.DISCOVERY);
      resourceLink.setProspectResource(
          submissionToBeFiltered.getSubmission().getResource());
      resourceLink.setDiscoveryResource(discoveryResource);

      if (submissionType != SubmissionType.PROSPECT_UPDATE
          && submissionType != SubmissionType.RELINQUISHMENT_PROSPECT) {
        when(submissionDetailService.getSubmissionDetailsForResource(
            submissionToBeFiltered.getSubmission().getResource()))
            .thenReturn(List.of());
      }

      when(resourceLinkService.getResourceLinksByProspect(
          submissionToBeFiltered.getSubmission().getResource()))
          .thenReturn(List.of(resourceLink));
      var linkedDiscoverySubmission = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
      when(submissionDetailService.getSubmissionDetailsForResource(discoveryResource))
          .thenReturn(List.of(linkedDiscoverySubmission));

      var result = submissionFilterService
          .filterProspectSubmissionByOtherSubmissions(submissionToBeFiltered, submissionType);

      if (submissionType == SubmissionType.PROSPECT_UPDATE
          || submissionType == SubmissionType.RELINQUISHMENT_PROSPECT) {
        assertThat(result).isFalse();
      } else {
        assertThat(result).isTrue();
      }
    }

    @ParameterizedTest
    @EnumSource(
        mode = EnumSource.Mode.INCLUDE,
        value = SubmissionType.class,
        names = {
            "PROSPECT_UPDATE", "POST_DRILL_DRY_HOLE",
            "CREATE_DISCOVERY_FROM_PROSPECT", "RELINQUISHMENT_PROSPECT"})
    void filterProspectSubmissionByOtherSubmissions_whenDeletedLinkedDiscovery_WhenStartingProspectSubmission_assertTrue(
        SubmissionType submissionType) {
      var submissionToBeFiltered = SubmissionDetailTestUtils.createSubmissionDetail(
          SubmissionType.PROSPECT_UPDATE);
      var resourceLink = new ResourceLink();
      var discoveryResource = new Resource();
      discoveryResource.setType(ResourceType.DISCOVERY);
      resourceLink.setProspectResource(
          submissionToBeFiltered.getSubmission().getResource());
      resourceLink.setDiscoveryResource(discoveryResource);

      var deletedPostDrillDryHoleSubmission = SubmissionDetailTestUtils
          .createNewPostDrillDryWellSubmissionDetail();
      deletedPostDrillDryHoleSubmission.setStatus(SubmissionDetailStatus.DELETED);
      when(submissionDetailService.getSubmissionDetailsForResource(
          submissionToBeFiltered.getSubmission().getResource()))
          .thenReturn(List.of(deletedPostDrillDryHoleSubmission));


      when(resourceLinkService.getResourceLinksByProspect(
          submissionToBeFiltered.getSubmission().getResource()))
          .thenReturn(List.of(resourceLink));
      var linkedDiscoverySubmission = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
      linkedDiscoverySubmission.setStatus(SubmissionDetailStatus.DELETED);
      when(submissionDetailService.getSubmissionDetailsForResource(discoveryResource))
          .thenReturn(List.of(linkedDiscoverySubmission));

      var result = submissionFilterService
          .filterProspectSubmissionByOtherSubmissions(submissionToBeFiltered, submissionType);

      assertThat(result).isTrue();
    }
  }
}