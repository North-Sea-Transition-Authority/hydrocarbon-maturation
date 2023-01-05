package uk.co.nstauthority.hydrocarbonmaturation;

import org.springframework.context.annotation.Import;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlResponseParser;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceLogoutSuccessHandler;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.SamlProperties;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.WebSecurityConfiguration;

@Import(value = {
    WebSecurityConfiguration.class,
    SamlProperties.class,
    SamlResponseParser.class,
    ServiceLogoutSuccessHandler.class})
public abstract class RealSecurityAbstractControllerTest extends AbstractControllerTest {
}