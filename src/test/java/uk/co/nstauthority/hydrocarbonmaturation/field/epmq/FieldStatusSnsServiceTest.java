package uk.co.nstauthority.hydrocarbonmaturation.field.epmq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalmessagequeue.sns.SnsService;
import uk.co.fivium.energyportalmessagequeue.sns.SnsTopicArn;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;
import uk.co.nstauthority.hydrocarbonmaturation.epmqmessage.FieldStatusEpmqMessage;
import uk.co.nstauthority.hydrocarbonmaturation.epmqmessage.RemiEpmqTopics;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;

@ExtendWith(MockitoExtension.class)
class FieldStatusSnsServiceTest {

  private final SnsTopicArn snsTopicArn = new SnsTopicArn(RemiEpmqTopics.FIELDS.getName());

  @Mock
  private SnsService snsService;

  private Clock clock;

  private FieldStatusSnsService fieldStatusSnsService;

  @Captor
  private ArgumentCaptor<FieldStatusEpmqMessage> captor;

  @BeforeEach
  void setUp() {
    when(snsService.getOrCreateTopic(RemiEpmqTopics.FIELDS.getName())).thenReturn(snsTopicArn);

    fieldStatusSnsService = spy(new FieldStatusSnsService(
        snsService,
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
    ));
  }

  @Test
  void publishFieldStatusUpdateSnsMessage() {
    var fieldId = 1;
    var fieldStatus = FieldStatus.STATUS_000;

    var message = new FieldStatusEpmqMessage(
        CorrelationIdUtil.getCorrelationIdFromMdc(),
        clock.instant(),
        fieldId,
        fieldStatus.getStatusValue()
    );

    fieldStatusSnsService.publishFieldStatusUpdateSnsMessage(fieldId, fieldStatus.getStatusValue());

    verify(snsService).publishMessage(eq(snsTopicArn), captor.capture());

    assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(message);
  }
}
