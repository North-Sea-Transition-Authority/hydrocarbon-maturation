package uk.co.nstauthority.hydrocarbonmaturation.configuration;


import javax.sql.DataSource;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

@Configuration
class JooqConfiguration {

  private final DataSource dataSource;

  @Autowired
  JooqConfiguration(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Bean
  public DataSourceConnectionProvider connectionProvider() {
    return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
  }

  @Bean
  public DefaultDSLContext dsl() {
    return new DefaultDSLContext(configuration());
  }

  public DefaultConfiguration configuration() {
    DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
    jooqConfiguration.setSQLDialect(SQLDialect.POSTGRES);
    jooqConfiguration.set(connectionProvider());

    var settings = jooqConfiguration.settings().withRenderQuotedNames(RenderQuotedNames.NEVER)
        .withInterpreterDialect(SQLDialect.POSTGRES)
        .withParseDialect(SQLDialect.POSTGRES)
        .withRenderSchema(true);
    jooqConfiguration
        .setSettings(settings);

    return jooqConfiguration;
  }
}
