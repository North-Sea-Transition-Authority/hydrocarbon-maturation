package uk.co.nstauthority.hydrocarbonmaturation.field.epmq;

import java.time.Clock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalmessagequeue.sns.SnsService;
import uk.co.fivium.energyportalmessagequeue.sns.SnsTopicArn;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;
import uk.co.nstauthority.hydrocarbonmaturation.epmqmessage.FieldStatusEpmqMessage;
import uk.co.nstauthority.hydrocarbonmaturation.epmqmessage.RemiEpmqTopics;

@Service
public class FieldStatusSnsService {

  private final SnsService snsService;
  private final SnsTopicArn fieldSnsTopicArn;
  private final Clock clock;

  public FieldStatusSnsService(SnsService snsService, Clock clock) {
    this.snsService = snsService;
    this.fieldSnsTopicArn = snsService.getOrCreateTopic(RemiEpmqTopics.FIELDS.getName());
    this.clock = clock;
  }

  @Async
  public void publishFieldStatusUpdateSnsMessage(Integer fieldId, Integer fieldStatusValue) {
    var message = new FieldStatusEpmqMessage(
        CorrelationIdUtil.getCorrelationIdFromMdc(),
        clock.instant(),
        fieldId,
        fieldStatusValue
    );

    snsService.publishMessage(fieldSnsTopicArn, message);
  }

}
