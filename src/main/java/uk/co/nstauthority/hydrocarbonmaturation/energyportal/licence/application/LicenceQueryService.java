package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.client.licence.licence.LicenceApi;
import uk.co.fivium.energyportalapi.generated.client.LicenceProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.LicencesProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.Licence;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;

@Service
public class LicenceQueryService {

  public static final LicenceProjectionRoot LICENCE_PROJECTION_ROOT =
      new LicenceProjectionRoot().licenceRef().id().licenceStatus().root();

  public static final LicencesProjectionRoot LICENCES_PROJECTION_ROOT =
      new LicencesProjectionRoot().licenceRef().id().licenceStatus().root();

  private final LicenceApi licenceApi;
  private final EnergyPortalApiWrapper energyPortalApiWrapper;

  @Autowired
  public LicenceQueryService(LicenceApi licenceApi, EnergyPortalApiWrapper energyPortalApiWrapper) {
    this.licenceApi = licenceApi;
    this.energyPortalApiWrapper = energyPortalApiWrapper;
  }

  public List<LicenceView> getExtantLicencesBy(String searchReference) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        licenceApi.searchLicences(
            searchReference,
            LICENCES_PROJECTION_ROOT,
            requestPurpose,
            logCorrelationId
            ).stream()
            .filter(licence -> licence.getLicenceStatus() == LicenceStatus.EXTANT)
            .sorted(Comparator.comparing(Licence::getLicenceRef))
            .map(this::getLicenceView)
            .toList()
    );
  }

  public List<LicenceView> getLicencesByIds(List<String> searchIdStrings) {
    return searchIdStrings.stream()
        .map(ref -> findLicence(Integer.parseInt(ref)))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .distinct()
        .sorted(Comparator.comparing(LicenceView::licenceRef))
        .toList();
  }

  private Optional<LicenceView> findLicence(Integer licenceId) {
    return energyPortalApiWrapper.makeRequest(((logCorrelationId, requestPurpose) ->
        licenceApi.findLicence(
            licenceId,
            LICENCE_PROJECTION_ROOT,
            requestPurpose,
            logCorrelationId)))
        .map(item -> new LicenceView(item.getLicenceRef(), item.getId()));
  }

  private LicenceView getLicenceView(Licence licence) {
    return new LicenceView(licence.getLicenceRef(), licence.getId());
  }
}
