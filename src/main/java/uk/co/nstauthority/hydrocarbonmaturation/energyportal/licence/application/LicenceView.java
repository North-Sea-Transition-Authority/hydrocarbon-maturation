package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import uk.co.fivium.energyportalapi.generated.types.Licence;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.fds.addtolist.AddToListItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectable;

public record LicenceView(String licenceRef,
                          Integer licenceId,
                          LicenceStatus licenceStatus) implements AddToListItem, SearchSelectable {
  public static LicenceView fromLicence(Licence licence) {
    return new LicenceView(licence.getLicenceRef(), licence.getId(), licence.getLicenceStatus());
  }

  @Override
  public String getId() {
    return licenceId.toString();
  }

  @Override
  public String getName() {
    return licenceRef;
  }

  @Override
  public boolean isValid() {
    return LicenceQueryService.isLicenceStatusValid(licenceStatus);
  }

  @Override
  public String getSelectionId() {
    return licenceId.toString();
  }

  @Override
  public String getSelectionText() {
    return licenceRef;
  }
}
