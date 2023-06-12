package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class OutlineForMaximumCaseMapService {
  private final CustomerConfigurationProperties customerConfigurationProperties;

  @Autowired
  public OutlineForMaximumCaseMapService(CustomerConfigurationProperties customerConfigurationProperties) {
    this.customerConfigurationProperties = customerConfigurationProperties;
  }

  String getMapAttributions() {
    return "Contains information provided by the %s"
        .formatted(customerConfigurationProperties.mnemonic());
  }

  String getMapGeoJsonDataRoute(SubmissionDetail submissionDetail) {
    return ReverseRouter.route(on(OutlineForMaximumCaseRestController.class)
        .getGeoJsonForOutlineForMaximumCase(submissionDetail.getId()));
  }
}
