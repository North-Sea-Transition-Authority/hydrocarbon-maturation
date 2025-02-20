package uk.co.nstauthority.hydrocarbonmaturation.hibernate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;

@IntegrationTest
public abstract class AbstractHibernateQueryCountTest {

  @Autowired
  public HibernateQueryCounter hibernateQueryCounter;

  @BeforeEach
  void setUp() {
    hibernateQueryCounter.clearQueryCount();
  }

  @Test
  public abstract void verifyQueryCount();
}
