package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.client.LogCorrelationId;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.licence.application.LicenceApplicationApi;
import uk.co.fivium.energyportalapi.generated.client.LicenceApplicationProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.LicenceApplicationsProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.LicenceApplication;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;

@Service
public class LicenceApplicationQueryService {

  public static final LicenceApplicationsProjectionRoot LICENCE_APPLICATIONS_PROJECTION_ROOT =
      new LicenceApplicationsProjectionRoot().licenceApplicationId().licenceApplicationRef();
  public static final LicenceApplicationProjectionRoot LICENCE_APPLICATION_PROJECTION_ROOT =
      new LicenceApplicationProjectionRoot().licenceApplicationId().licenceApplicationRef();

  private final LicenceApplicationApi licenceApplicationApi;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  public LicenceApplicationQueryService(LicenceApplicationApi licenceApplicationApi,
                                        SearchSelectorService searchSelectorService) {
    this.licenceApplicationApi = licenceApplicationApi;
    this.searchSelectorService = searchSelectorService;
  }

  public List<LicenceApplicationDto> getLicenceApplicationSearchableResults(String licenceApplicationRef) {
    return searchLicenceApplications(
        licenceApplicationRef,
        new RequestPurpose("Search licence applications by reference"),
        CorrelationIdUtil.getLogCorrelationId()
    )
    .stream()
    .map(this::fromLicenceApplication)
    .toList();
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
    return licenceApplicationApi.findLicenceApplication(
            licenceApplicationId,
            LICENCE_APPLICATION_PROJECTION_ROOT,
            new RequestPurpose("Find licence application by ID"),
            CorrelationIdUtil.getLogCorrelationId()
    );
  }

  public Map<String, String> getPreSelectedLicenceApplication(String licenceApplication) {
    if (licenceApplication != null) {
      if (SearchSelectorService.isManualEntry(licenceApplication)) {
        return searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(licenceApplication),
            Map.of(licenceApplication, licenceApplication)
        );
      } else {
        return searchSelectorService.buildPrePopulatedSelections(
            Collections.singletonList(licenceApplication),
            Map.of(
                licenceApplication,
                getLicenceApplicationRef(Integer.parseInt(licenceApplication))
            ));
      }
    }
    return Map.of();
  }

  private List<LicenceApplication> searchLicenceApplications(
      String licenceApplicationRef,
      RequestPurpose requestPurpose,
      LogCorrelationId logCorrelationId) {
    return licenceApplicationApi.searchLicenceApplications(
       licenceApplicationRef,
       LICENCE_APPLICATIONS_PROJECTION_ROOT,
       requestPurpose,
       logCorrelationId);
  }
}
