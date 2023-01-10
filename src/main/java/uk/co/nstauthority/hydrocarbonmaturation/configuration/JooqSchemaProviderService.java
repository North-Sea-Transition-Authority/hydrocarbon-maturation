package uk.co.nstauthority.hydrocarbonmaturation.configuration;

import static org.jooq.impl.DSL.table;

import org.jooq.Record;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JooqSchemaProviderService {

  private final JooqConfigurationProperties jooqConfigurationProperties;

  @Autowired
  JooqSchemaProviderService(JooqConfigurationProperties jooqConfigurationProperties) {
    this.jooqConfigurationProperties = jooqConfigurationProperties;
  }

  //This is used to provide the schema when specifying a table in JOOQ.
  public Table<Record> remiTable(String tableName) {
    return table(jooqConfigurationProperties.schema() + "." + tableName);
  }
}
