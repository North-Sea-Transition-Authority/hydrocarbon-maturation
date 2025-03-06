package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.List;

public interface ResourceRecordQueryServiceStrategy {
  List<ResourceRecordQueryResult> getResourceRecordSearchResults(SearchResourceRecordsForm form);
}
