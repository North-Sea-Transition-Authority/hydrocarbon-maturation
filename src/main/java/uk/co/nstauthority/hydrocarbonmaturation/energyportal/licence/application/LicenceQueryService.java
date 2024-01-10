package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
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

  public List<LicenceView> getLicencesByReferenceAndStatus(String searchReference, LicenceStatus licenceStatus) {
    return getLicenceListBy(searchReference)
        .stream()
        .filter(licence -> licence.getLicenceStatus() == licenceStatus)
        .map(this::getLicenceView)
        .sorted(new LicenceViewByReferenceComparator())
        .toList();
  }

  public List<LicenceView> getLiveLicencesBy(String searchReference) {
    return getLicenceListBy(searchReference)
        .stream()
        .map(this::getLicenceView)
        .sorted(new LicenceViewByReferenceComparator())
        .toList();
  }

  public List<LicenceView> getLicencesByIds(List<String> searchIdStrings) {
    var searchIds = searchIdStrings.stream()
        .map(Integer::parseInt)
        .distinct()
        .toList();

    return findLicencesByIds(searchIds).stream()
        .sorted(new LicenceViewByReferenceComparator())
        .toList();
  }

  public Map<Integer, LicenceView> getLicenceMapByIds(List<Integer> licences) {
    return findLicencesByIds(licences).stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(LicenceView::licenceId, Function.identity()));
  }

  public List<LicenceView> getExtantLicencesByIds(List<String> searchIdStrings) {
    return searchIdStrings.stream()
        .map(id -> findLicence(Integer.parseInt(id)))
        .flatMap(Optional::stream)
        .distinct()
        .filter(licence -> licence.licenceStatus() == LicenceStatus.EXTANT)
        .sorted(new LicenceViewByReferenceComparator())
        .toList();
  }

  public Optional<LicenceView> getLicenceById(Integer licenceId) {
    return findLicence(licenceId);
  }

  public static boolean isLicenceStatusValid(LicenceStatus licenceStatus) {
    return licenceStatus == LicenceStatus.EXTANT;
  }

  private Optional<LicenceView> findLicence(Integer licenceId) {
    return energyPortalApiWrapper.makeRequest(((logCorrelationId, requestPurpose) ->
        licenceApi.findLicence(
            licenceId,
            LICENCE_PROJECTION_ROOT,
            requestPurpose,
            logCorrelationId)))
        .map(item -> new LicenceView(item.getLicenceRef(), item.getId(), item.getLicenceStatus()));
  }

  private List<LicenceView> findLicencesByIds(List<Integer> licenceIds) {
    return energyPortalApiWrapper.makeRequest(((logCorrelationId, requestPurpose) ->
            licenceApi.searchLicencesById(
                licenceIds,
                LICENCES_PROJECTION_ROOT,
                requestPurpose,
                logCorrelationId)))
        .stream()
        .map(item -> new LicenceView(item.getLicenceRef(), item.getId(), item.getLicenceStatus()))
        .toList();
  }

  private LicenceView getLicenceView(Licence licence) {
    return new LicenceView(licence.getLicenceRef(), licence.getId(), licence.getLicenceStatus());
  }

  private List<Licence> getLicenceListBy(String searchReference) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        licenceApi.searchLicences(
            searchReference,
            LICENCES_PROJECTION_ROOT,
            requestPurpose,
            logCorrelationId
        ));
  }
}
