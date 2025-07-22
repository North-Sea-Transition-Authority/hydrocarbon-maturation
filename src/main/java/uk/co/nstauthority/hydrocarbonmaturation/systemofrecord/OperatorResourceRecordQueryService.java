package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCES;

import java.util.ArrayList;
import java.util.List;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

@Service
public class OperatorResourceRecordQueryService implements ResourceRecordQueryServiceStrategy {

  private final ResourceQueryCommonService resourceQueryCommonService;

  @Autowired
  OperatorResourceRecordQueryService(ResourceQueryCommonService resourceQueryCommonService) {
    this.resourceQueryCommonService = resourceQueryCommonService;
  }

  @Override
  public List<ResourceRecordQueryResult> getResourceRecordSearchResults(SearchResourceRecordsForm form) {
    var resourceTypes = form.getResourceTypes();
    if (resourceTypes.contains(ResourceType.FIELD.getEnumName())) {
      return resourceQueryCommonService.getFieldRecordQueryResults(
          getFormFilterConditions(form)
      );
    } else {
      return resourceQueryCommonService.getProspectAndDiscoveryRecordQueryResults(
          getFormFilterConditions(form),
          getFormAggregateConditions(form)
      );
    }
  }

  private List<Condition> getFormFilterConditions(SearchResourceRecordsForm form) {
    var conditionList = new ArrayList<>(resourceQueryCommonService.getFormFilterConditions(form));
    conditionList.add(RESOURCES.ACTIVE.eq(true));

    return conditionList;
  }

  private List<Condition> getFormAggregateConditions(SearchResourceRecordsForm form) {
    return new ArrayList<>(resourceQueryCommonService.getFormFilterAggregateConditions(form));
  }
}
