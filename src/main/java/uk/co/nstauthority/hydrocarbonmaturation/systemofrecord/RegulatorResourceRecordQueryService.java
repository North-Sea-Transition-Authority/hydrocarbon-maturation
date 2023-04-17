package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_GROUP_LINKS;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.jooq.Condition;
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
        getRegulatorConditions(form),
        resourceQueryCommonService.getFormFilterAggregateConditions(form)
    );
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
