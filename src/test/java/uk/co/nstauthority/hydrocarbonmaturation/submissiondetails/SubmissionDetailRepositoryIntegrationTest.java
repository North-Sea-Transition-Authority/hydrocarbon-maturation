package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

@Transactional
@IntegrationTest
class SubmissionDetailRepositoryIntegrationTest {

  @Autowired
  private DSLContext dslContext;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private SubmissionDetailService submissionDetailService;

  @Test
  void findLatestSubmittedForSubmissionsBySubmissionTypesAndStatusesOrderedBySubmittedTimestamp_orderedBySubmittedTimestamp() {
    var resource = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, resource, SubmissionDetailStatus.SUBMITTED, entityManager);

    var updateSubmissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.PROSPECT_UPDATE, resource, SubmissionDetailStatus.SUBMITTED, entityManager);

    assertThat(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .isEqualTo(Optional.of(updateSubmissionDetail));
  }

  @Test
  void findLatestForSubmissionsBySubmissionTypesAndStatusesOrderedBySubmittedTimestamp_orderedByCorrectionTimestamp() {
    var resource = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, resource, SubmissionDetailStatus.SUBMITTED, entityManager);

    var correctionSubmissionDetail = SubmissionDetailIntegrationTestUtils
        .createCorrectionSubmissionDetail(submissionDetail, resource, entityManager);

    assertThat(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .isEqualTo(Optional.of(correctionSubmissionDetail));
  }

  @Test
  void findLatestForSubmissionsBySubmissionTypesAndStatusesOrderedBySubmittedTimestamp_orderedByLegacyResDataId() {
    var resource = ResourceIntegrationTestUtils.buildResource(ResourceType.PROSPECT, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_PROSPECT, resource, SubmissionDetailStatus.SUBMITTED, entityManager);

    var legacySubmissionDetail = SubmissionDetailIntegrationTestUtils
        .createLegacySubmissionDetail(submissionDetail, resource, entityManager);

    assertThat(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource))
        .isEqualTo(Optional.of(legacySubmissionDetail));
  }

  @Test
  void findLatestAcceptedForSubmissionsBySubmissionTypesAndStatusOrderedByAcceptedTimestamp_orderedByTimestamp() {
    var resource = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, resource, SubmissionDetailStatus.ACCEPTED, entityManager);

    var updateSubmissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.UPDATE_DISCOVERY, resource, SubmissionDetailStatus.ACCEPTED, entityManager);

    assertThat(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resource))
        .isEqualTo(Optional.of(updateSubmissionDetail));
  }

  @Test
  void findLatestForSubmissionsBySubmissionTypesAndStatusOrderedByAcceptedTimestamp_orderedByCorrectionTimestamp() {
    var resource = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, resource, SubmissionDetailStatus.ACCEPTED, entityManager);

    var correctionSubmissionDetail = SubmissionDetailIntegrationTestUtils
        .createCorrectionSubmissionDetail(submissionDetail, resource, entityManager);

    assertThat(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resource))
        .isEqualTo(Optional.of(correctionSubmissionDetail));
  }

  @Test
  void findLatestForSubmissionsBySubmissionTypesAndStatusOrderedByAcceptedTimestamp_orderedByLegacyResDataId() {
    var resource = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, resource, SubmissionDetailStatus.ACCEPTED, entityManager);

    var legacySubmissionDetail = SubmissionDetailIntegrationTestUtils
        .createLegacySubmissionDetail(submissionDetail, resource, entityManager);

    assertThat(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resource))
        .isEqualTo(Optional.of(legacySubmissionDetail));
  }

  @Test
  void findLatestAcceptedForSubmissionsByStatusOrderedByAcceptedTimestamp_orderedByTimestamp() {
    var resource = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, resource, SubmissionDetailStatus.ACCEPTED, entityManager);

    var updateSubmissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.UPDATE_DISCOVERY, resource, SubmissionDetailStatus.ACCEPTED, entityManager);

    assertThat(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .isEqualTo(Optional.of(updateSubmissionDetail));
  }

  @Test
  void findLatestForSubmissionsByStatusOrderedByAcceptedTimestamp_orderedByCorrectionTimestamp() {
    var resource = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, resource, SubmissionDetailStatus.ACCEPTED, entityManager);

    var correctionSubmissionDetail = SubmissionDetailIntegrationTestUtils
        .createCorrectionSubmissionDetail(submissionDetail, resource, entityManager);

    assertThat(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .isEqualTo(Optional.of(correctionSubmissionDetail));
  }

  @Test
  void findLatestForSubmissionsByStatusOrderedByAcceptedTimestamp_orderedByLegacyResDataId() {
    var resource = ResourceIntegrationTestUtils.buildResource(ResourceType.DISCOVERY, entityManager);

    var submissionDetail = SubmissionDetailIntegrationTestUtils
        .createSubmissionDetail(SubmissionType.CREATE_DISCOVERY, resource, SubmissionDetailStatus.ACCEPTED, entityManager);

    var legacySubmissionDetail = SubmissionDetailIntegrationTestUtils
        .createLegacySubmissionDetail(submissionDetail, resource, entityManager);

    assertThat(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .isEqualTo(Optional.of(legacySubmissionDetail));
  }

}
