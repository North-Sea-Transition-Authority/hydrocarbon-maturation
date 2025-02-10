package uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.licence;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalapi.client.RequestPurpose;
import uk.co.fivium.energyportalapi.client.field.FieldApi;
import uk.co.fivium.energyportalapi.generated.client.FieldProjectionRoot;
import uk.co.fivium.energyportalapi.generated.types.Field;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;
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

  @Autowired
  public FieldLicenceQueryService(FieldApi fieldApi) {
    this.fieldApi = fieldApi;
  }

  public List<LicenceView> getFieldLicencesByFieldIdentifier(Integer fieldIdentifier) {
    return fieldApi.findFieldById(
        fieldIdentifier,
            FIELD_LICENCE_PROJECTION_ROOT,
            new RequestPurpose("Find field by ID"),
            CorrelationIdUtil.getLogCorrelationId()
        ).map(Field::getLicences).orElseGet(List::of)
        .stream()
        .map(LicenceView::fromLicence)
        .sorted(new LicenceViewByReferenceComparator())
        .toList();
  }
}
