package uk.co.nstauthority.hydrocarbonmaturation.externalapi.fielddevelopmentplan;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.hibernate.AbstractHibernateQueryCountTest;

public class FieldDevelopmentPlanExternalServiceIntegrationTest extends AbstractHibernateQueryCountTest {

  @Autowired
  private FieldDevelopmentPlanExternalService fieldDevelopmentPlanExternalService;

  @Override
  @Test
  public void verifyQueryCount() {
    hibernateQueryCounter.clearQueryCount();
    fieldDevelopmentPlanExternalService.createFdpDtosByFieldIds(List.of(1));

    assertThat(hibernateQueryCounter.getOverallQueryCount()).isEqualTo(4);
  }
}
