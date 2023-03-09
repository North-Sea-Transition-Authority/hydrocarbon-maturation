package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
class RegulatorResourceRecordQueryService implements ResourceRecordQueryServiceStrategy {

  private final ResourceQueryCommonService resourceQueryCommonService;

  RegulatorResourceRecordQueryService(ResourceQueryCommonService resourceQueryCommonService) {
    this.resourceQueryCommonService = resourceQueryCommonService;
  }

  @Override
  public List<ResourceRecordQueryResult> getResourceRecordSearchResults(SearchResourceRecordsForm form) {
    return resourceQueryCommonService.getResourceRecordQueryResults(
        resourceQueryCommonService.getFormFilterConditions(form),
        resourceQueryCommonService.getFormFilterAggregateConditions(form)
    );
  }
}
