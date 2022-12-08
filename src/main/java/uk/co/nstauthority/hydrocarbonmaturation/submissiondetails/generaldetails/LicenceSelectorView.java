package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import uk.co.nstauthority.hydrocarbonmaturation.fds.addtolist.AddToListItem;

record LicenceSelectorView(String id, String name, boolean isValid) implements AddToListItem {

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }
}
