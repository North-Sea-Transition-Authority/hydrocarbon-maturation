package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record SummaryDataViewWithActions(
    Map<String, String> actionsMap,
    List<SummaryKeyValue> keyValues
) {

  public static SummaryDataViewWithActions newWithActionsMap(Map<String, String> actionsMap) {
    return new SummaryDataViewWithActions(actionsMap, new ArrayList<>());
  }

  public SummaryDataViewWithActions addKeyValue(String key, Object value) {
    keyValues.add(new SummaryKeyValue(key, SummaryUtil.format(value)));
    return this;
  }
}
