package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;

interface ResourceRecordQueryServiceStrategy {
  List<ResourceRecordQueryResult> getResourceRecordSearchResults(SearchResourceRecordsForm form, User user);
}
