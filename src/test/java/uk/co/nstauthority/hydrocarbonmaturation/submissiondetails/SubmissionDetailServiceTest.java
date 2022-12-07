package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class SubmissionDetailServiceTest {

  private static final Instant INSTANT = Instant.now();
  @Mock
  private SubmissionDetailRepository submissionDetailRepository;

  private SubmissionDetailService submissionDetailService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    Clock clock = Clock.fixed(Instant.from(INSTANT), ZoneId.systemDefault());
    submissionDetailService = new SubmissionDetailService(submissionDetailRepository, clock);
    submissionDetail = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
  }

  @Test
  void createInitialSubmissionDetail() {
    var submissionType = SubmissionType.CREATE_PROSPECT;
    var resource = new Resource();
    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);
    var submissionDetail = submissionDetailService.createInitialSubmissionDetail(resource, submissionType);
    verify(submissionDetailRepository, times(1)).save(submissionDetailArgumentCaptor.capture());

    var createdSubmissionDetail = submissionDetailArgumentCaptor.getValue();
    assertThat(createdSubmissionDetail.getResourceId()).isEqualTo(resource);
    assertThat(createdSubmissionDetail.getCreatedTimestamp()).isEqualTo(INSTANT);
    assertThat(createdSubmissionDetail.getCreatedByWuaId()).isEqualTo(1);
    assertThat(createdSubmissionDetail.getStatus()).isEqualTo(SubmissionDetailStatus.DRAFT);
    assertThat(createdSubmissionDetail.getSubmissionType()).isEqualTo(submissionType);
    assertThat(createdSubmissionDetail.getVersionNumber()).isEqualTo(SubmissionDetailService.FIRST_VERSION);

  }

  @Test
  void getLatestSubmissionDetailByResource_verifyMethodCall() {
    var resource = new Resource();
    submissionDetailService.getLatestSubmissionDetailByResource(resource);

    verify(submissionDetailRepository, times(1))
        .findFirstByResourceIdOrderByCreatedTimestampDesc(resource);
  }

  @Test
  void getLatestSubmissionDetailForResourceOrThrow_verifyMethodCall() {
    var resource = new Resource();
    when(submissionDetailRepository.findFirstByResourceIdOrderByCreatedTimestampDesc(resource))
        .thenReturn(Optional.of(submissionDetail));

    assertThat(submissionDetailService.getLatestSubmissionDetailForResourceOrThrow(resource))
        .isEqualTo(submissionDetail);
  }

  @Test
  void getLatestSubmissionDetailForResourceOrThrow_throw() {
    var resource = new Resource();
    when(submissionDetailRepository.findFirstByResourceIdOrderByCreatedTimestampDesc(resource))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> submissionDetailService.getLatestSubmissionDetailForResourceOrThrow(resource))
        .isExactlyInstanceOf(RemiEntityNotFoundException.class)
        .hasMessage("Submission detail for resource with ID %s not found"
            .formatted(resource.getId()));
  }

  @Test
  void completeSubmission() {
    var submissionDetail = new SubmissionDetail();
    submissionDetailService.completeSubmission(submissionDetail);

    assertThat(submissionDetail.getStatus()).isEqualTo(SubmissionDetailStatus.APPROVED);
    verify(submissionDetailRepository, times(1)).save(submissionDetail);
  }

  @Test
  void getSubmissionDetailsForResource_verifyMethodCall() {
    var resource = new Resource();

    submissionDetailService.getSubmissionDetailsForResource(resource);

    verify(submissionDetailRepository, times(1)).findSubmissionDetailsByResourceId(resource);
  }
}
