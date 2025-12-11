package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.REGULATOR_MANAGED_INFORMATION;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCES;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

@Service
public class RegulatorResourceRecordQueryService implements ResourceRecordQueryServiceStrategy {

  private final ResourceQueryCommonService resourceQueryCommonService;

  @Autowired
  RegulatorResourceRecordQueryService(ResourceQueryCommonService resourceQueryCommonService) {
    this.resourceQueryCommonService = resourceQueryCommonService;
  }

  @Override
  public List<ResourceRecordQueryResult> getResourceRecordSearchResults(SearchResourceRecordsForm form) {
    var resourceTypes = form.getResourceTypes();
    if (resourceTypes.contains(ResourceType.FIELD.getEnumName())) {
      return resourceQueryCommonService.getFieldRecordQueryResults(
          getRegulatorConditions(form)
      );
    } else {
      return resourceQueryCommonService.getProspectAndDiscoveryRecordQueryResults(
          getRegulatorConditions(form),
          resourceQueryCommonService.getFormFilterAggregateConditions(form)
      );
    }
  }

  private List<Condition> getRegulatorConditions(SearchResourceRecordsForm form) {
    var conditionList = new ArrayList<>(resourceQueryCommonService.getFormFilterConditions(form));
    if (BooleanUtils.isNotTrue(form.getShowArchivedResources())) {
      conditionList.add((RESOURCES.ACTIVE.eq(true)));
    }
    if (form.getRegulatorResourceName() != null) {
      conditionList.add(REGULATOR_MANAGED_INFORMATION.RESOURCE_NAME.eq(form.getRegulatorResourceName()));
    }
    return conditionList;
  }
}
