package uk.co.nstauthority.hydrocarbonmaturation.epmqmessage;

import java.time.Instant;
import uk.co.fivium.energyportalmessagequeue.message.EpmqMessage;

public class RemiEpmqMessage extends EpmqMessage {

  protected RemiEpmqMessage(String type, String correlationId, Instant createdInstant) {
    super("REMI", type, correlationId, createdInstant);
  }
}
