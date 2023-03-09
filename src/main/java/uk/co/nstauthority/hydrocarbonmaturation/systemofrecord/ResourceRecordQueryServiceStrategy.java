package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.List;

interface ResourceRecordQueryServiceStrategy {
  List<ResourceRecordQueryResult> getResourceRecordSearchResults(SearchResourceRecordsForm form);
}
