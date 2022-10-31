package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.client.licence.application.LicenceApplicationApi;
import uk.co.fivium.energyportalapi.generated.client.LicenceApplicationProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.LicenceApplicationsProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.LicenceApplication;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;

@Service
public class LicenceApplicationQueryService {

  public static final LicenceApplicationsProjectionRoot LICENCE_APPLICATIONS_PROJECTION_ROOT =
      new LicenceApplicationsProjectionRoot().licenceApplicationId().licenceApplicationRef();
  public static final LicenceApplicationProjectionRoot LICENCE_APPLICATION_PROJECTION_ROOT =
      new LicenceApplicationProjectionRoot().licenceApplicationId().licenceApplicationRef();

  private final LicenceApplicationApi licenceApplicationApi;
  private final EnergyPortalApiWrapper energyPortalApiWrapper;

  @Autowired
  public LicenceApplicationQueryService(LicenceApplicationApi licenceApplicationApi,
                                        EnergyPortalApiWrapper energyPortalApiWrapper) {
    this.licenceApplicationApi = licenceApplicationApi;
    this.energyPortalApiWrapper = energyPortalApiWrapper;
  }

  public List<LicenceApplicationDto> getLicenceApplicationSearchableResults(String licenceApplicationRef) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        searchLicenceApplications(
            licenceApplicationRef,
            requestPurpose.purpose(),
            logCorrelationId.id())
        .stream()
        .map(this::fromLicenceApplication)
        .toList());
  }

  private LicenceApplicationDto fromLicenceApplication(LicenceApplication licenceApplication) {
    var licenceApplicationDto = new LicenceApplicationDto();
    licenceApplicationDto.setLicenceApplicationId(licenceApplication.getLicenceApplicationId());
    licenceApplicationDto.setLicenceApplicationRef(licenceApplication.getLicenceApplicationRef());
    return licenceApplicationDto;
  }

  public String getLicenceApplicationRef(Integer licenceApplicationId) {
    var licenceApplication = findLicenceApplication(licenceApplicationId);
    return licenceApplication.map(LicenceApplication::getLicenceApplicationRef).orElse(null);
  }

  public Optional<LicenceApplication> findLicenceApplication(Integer licenceApplicationId) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        licenceApplicationApi.findLicenceApplication(
            licenceApplicationId,
            LICENCE_APPLICATION_PROJECTION_ROOT,
            requestPurpose.purpose(),
            logCorrelationId.id())
    );
  }

  List<LicenceApplication> searchLicenceApplications(
      String licenceApplicationRef,
      String requestPurpose,
      String logCorrelationId) {
    return licenceApplicationApi.searchLicenceApplications(
       licenceApplicationRef,
       LICENCE_APPLICATIONS_PROJECTION_ROOT,
       requestPurpose,
       logCorrelationId);
  }
}
