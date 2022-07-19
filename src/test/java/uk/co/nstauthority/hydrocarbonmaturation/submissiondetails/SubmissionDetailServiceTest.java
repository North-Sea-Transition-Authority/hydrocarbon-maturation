package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@ExtendWith(MockitoExtension.class)
public class SubmissionDetailServiceTest {

  private static final Instant INSTANT = Instant.now();
  @Mock
  private SubmissionDetailRepository submissionDetailRepository;

  private SubmissionDetailService submissionDetailService;

  @BeforeEach
  void setUp() {
    Clock clock = Clock.fixed(Instant.from(INSTANT), ZoneId.systemDefault());
    submissionDetailService = new SubmissionDetailService(submissionDetailRepository, clock);
  }

  @Test
  public void createInitialSubmissionDetail() {
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
}
