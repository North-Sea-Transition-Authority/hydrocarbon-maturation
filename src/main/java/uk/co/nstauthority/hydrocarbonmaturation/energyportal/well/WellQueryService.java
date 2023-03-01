package uk.co.nstauthority.hydrocarbonmaturation.energyportal.well;


import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.client.wellbore.WellboreApi;
import uk.co.fivium.energyportalapi.generated.client.WellboreProjectionRoot;
import uk.co.fivium.energyportalapi.generated.client.WellboresProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.Wellbore;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;

@Service
public class WellQueryService {

  public static final WellboresProjectionRoot WELLBORES_PROJECTION_ROOT =
      new WellboresProjectionRoot()
          .id()
          .registrationNumber();
  public static final WellboreProjectionRoot WELLBORE_PROJECTION_ROOT =
      new WellboreProjectionRoot()
          .id()
          .registrationNumber();

  private final WellboreApi wellboreApi;
  private final EnergyPortalApiWrapper energyPortalApiWrapper;

  @Autowired
  public WellQueryService(WellboreApi wellboreApi,
                          EnergyPortalApiWrapper energyPortalApiWrapper) {
    this.wellboreApi = wellboreApi;
    this.energyPortalApiWrapper = energyPortalApiWrapper;
  }

  public List<WellView> getWellsByRegistrationNumber(String registrationNumber) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        wellboreApi.searchWellboresByRegistrationNumber(
            registrationNumber,
            WELLBORES_PROJECTION_ROOT,
            requestPurpose,
            logCorrelationId)
            .stream()
            .map(this::getWellViewFromWellbore)
            .toList());
  }

  public List<WellView> getWellsByIds(List<Integer> wellIds) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        wellboreApi.searchWellboresByIds(
                wellIds,
                WELLBORES_PROJECTION_ROOT,
                requestPurpose,
                logCorrelationId)
            .stream()
            .map(this::getWellViewFromWellbore)
            .toList());
  }

  public Optional<WellView> findWellById(Integer wellId) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        wellboreApi.findWellboreById(
                wellId,
                WELLBORE_PROJECTION_ROOT,
                requestPurpose,
                logCorrelationId)
            .map(this::getWellViewFromWellbore));
  }

  private WellView getWellViewFromWellbore(Wellbore wellbore) {
    return new WellView(wellbore.getId().toString(), wellbore.getRegistrationNumber());
  }
}
