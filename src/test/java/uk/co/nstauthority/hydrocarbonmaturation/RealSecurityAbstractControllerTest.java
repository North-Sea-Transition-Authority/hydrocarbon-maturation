package uk.co.nstauthority.hydrocarbonmaturation;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlResponseParser;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceLogoutSuccessHandler;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.DevtoolsConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.ExternalApiConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.ExternalApiWebSecurityConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.SamlProperties;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.WebSecurityConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.access.EnergyPortalAccessApiConfiguration;

@Import(value = {
    WebSecurityConfiguration.class,
    DevtoolsConfiguration.class,
    ExternalApiWebSecurityConfiguration.class,
    SamlProperties.class,
    SamlResponseParser.class,
    ServiceLogoutSuccessHandler.class})
@EnableConfigurationProperties(value = {
    EnergyPortalAccessApiConfiguration.class,
    ExternalApiConfiguration.class
})
public abstract class RealSecurityAbstractControllerTest extends AbstractControllerTest {
}