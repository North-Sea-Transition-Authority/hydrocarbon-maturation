import org.junit.jupiter.api.Test;

public class RemiEpmqMessagesJarIntegrationTest {

  @Test
  void classesExist() throws ClassNotFoundException {
    Class.forName("uk.co.nstauthority.hydrocarbonmaturation.epmqmessage.FieldEpmqMessage");
    Class.forName("uk.co.nstauthority.hydrocarbonmaturation.epmqmessage.FieldStatusEpmqMessage");
    Class.forName("uk.co.nstauthority.hydrocarbonmaturation.epmqmessage.RemiEpmqMessage");
    Class.forName("uk.co.nstauthority.hydrocarbonmaturation.epmqmessage.RemiEpmqTopics");
  }
}
