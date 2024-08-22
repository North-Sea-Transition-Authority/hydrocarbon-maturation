package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_GROUP_LINKS;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.jooq.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

@Service
public class RegulatorResourceRecordQueryService implements ResourceRecordQueryServiceStrategy {

  private final ResourceQueryCommonService resourceQueryCommonService;

  @Autowired
  RegulatorResourceRecordQueryService(ResourceQueryCommonService resourceQueryCommonService) {
    this.resourceQueryCommonService = resourceQueryCommonService;
  }

  @Override
  public List<ResourceRecordQueryResult> getResourceRecordSearchResults(SearchResourceRecordsForm form, User user) {
    var resourceTypes = form.getResourceTypes();
    if (resourceTypes.contains(ResourceType.FIELD.getEnumName())) {
      return resourceQueryCommonService.getFieldRecordQueryResults(
          getRegulatorConditions(form),
          resourceQueryCommonService.getFormFilterAggregateConditions(form)
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
    if (BooleanUtils.isTrue(form.isShowOnlyPreferredViews())) {
      //ensures only preferred views are displayed
      //resources with no resource group link are considered preferred views
      conditionList.add(RESOURCE_GROUP_LINKS.PREFERRED_VIEW.eq(true).or(RESOURCE_GROUP_LINKS.ID.isNull()));
    }

    return conditionList;
  }
}
