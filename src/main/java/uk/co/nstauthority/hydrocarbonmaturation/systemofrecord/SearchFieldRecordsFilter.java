package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import org.springframework.web.bind.annotation.SessionAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

@SessionAttributes({"searchFieldRecordsFilter"})
public class SearchFieldRecordsFilter implements SearchResourceRecordsFilter, Serializable {

  @Serial
  private static final long serialVersionUID = -1726120156817970506L;

  public static final List<String> SEARCHABLE_TYPES = List.of(ResourceType.FIELD.getEnumName());

  private List<String> resourceTypes = SEARCHABLE_TYPES;
  private boolean showResults;

  public static SearchFieldRecordsFilter createInstance() {
    var filter = new SearchFieldRecordsFilter();
    filter.initialise();
    return filter;
  }

  @Override
  public void update(SearchResourceRecordsForm form) {
    setShowResults(true);
  }

  @Override
  public void clearFilter() {
    initialise();
  }

  public List<String> getResourceTypes() {
    return resourceTypes;
  }

  public boolean isShowResults() {
    return showResults;
  }

  public void setShowResults(boolean showResults) {
    this.showResults = showResults;
  }

  private void initialise() {
    setShowResults(false);
  }
}
