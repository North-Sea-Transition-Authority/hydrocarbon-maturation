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

  // TODO HM-77 use wons api instead of dummy list where its used and remove this
  public static final List<String> DUMMY_LIST_OF_WELLS = List.of("ND27a", "30/11", "29/10", "28/12", "25/10", "25/15");
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

  List<WellView> getWellsByRegistrationNumber(String registrationNumber) {
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

  List<WellView> getWellsByIds(List<Integer> wellIds) {
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

  Optional<WellView> findWellById(Integer wellId) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        wellboreApi.findWellboreById(
                wellId,
                WELLBORE_PROJECTION_ROOT,
                requestPurpose,
                logCorrelationId)
            .map(this::getWellViewFromWellbore));
  }

  List<ReferenceWellView> getWellsByRef(String searchReference) {
    return DUMMY_LIST_OF_WELLS.stream()
        .filter(well -> {
          var quadBlockRef = well.toLowerCase();
          var reference = searchReference.trim().toLowerCase();
          return quadBlockRef.contains(reference);
        })
        .map(ReferenceWellView::fromWellReference)
        .toList();
  }

  public List<ReferenceWellView> getWellByName(String dryWell) {
    return DUMMY_LIST_OF_WELLS
        .stream()
        .filter(dryWell::contains)
        .map(ReferenceWellView::fromWellReference)
        .toList();
  }

  private WellView getWellViewFromWellbore(Wellbore wellbore) {
    return new WellView(wellbore.getId().toString(), wellbore.getRegistrationNumber());
  }
}
