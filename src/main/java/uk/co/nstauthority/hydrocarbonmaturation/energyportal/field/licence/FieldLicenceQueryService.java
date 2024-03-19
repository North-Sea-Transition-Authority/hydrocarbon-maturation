package uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.licence;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.client.field.FieldApi;
import uk.co.fivium.energyportalapi.generated.client.FieldProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.Field;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.api.EnergyPortalApiWrapper;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceViewByReferenceComparator;

@Service
public class FieldLicenceQueryService {
  public static final FieldProjectionRoot FIELD_LICENCE_PROJECTION_ROOT =
      new FieldProjectionRoot()
          .licences()
          .licenceRef()
          .licenceStatus()
          .root();

  private final FieldApi fieldApi;
  private final EnergyPortalApiWrapper energyPortalApiWrapper;

  @Autowired
  public FieldLicenceQueryService(FieldApi fieldApi, EnergyPortalApiWrapper energyPortalApiWrapper) {
    this.fieldApi = fieldApi;
    this.energyPortalApiWrapper = energyPortalApiWrapper;
  }

  public List<LicenceView> getFieldLicencesByFieldIdentifier(Integer fieldIdentifier) {
    return energyPortalApiWrapper.makeRequest((logCorrelationId, requestPurpose) ->
        fieldApi.findFieldById(
                fieldIdentifier,
                FIELD_LICENCE_PROJECTION_ROOT,
                requestPurpose,
                logCorrelationId
            )
            .map(Field::getLicences).orElseGet(List::of)
            .stream()
            .map(LicenceView::fromLicence)
            .sorted(new LicenceViewByReferenceComparator())
            .toList());
  }
}
