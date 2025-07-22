package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.SessionAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;

@SessionAttributes({"searchFieldRecordsFilter"})
public class SearchFieldRecordsFilter implements SearchResourceRecordsFilter, Serializable {

  @Serial
  private static final long serialVersionUID = -1726120156817970506L;

  public static final List<String> SEARCHABLE_TYPES = List.of(ResourceType.FIELD.getEnumName());

  private String fieldNameForResourceSubmission;
  private List<String> fieldStatuses = new ArrayList<>();
  private List<String> resourceTypes = SEARCHABLE_TYPES;
  private List<String> geographicalAreas = new ArrayList<>();
  private String parentFieldName;
  private boolean showResults;
  private Boolean showArchivedResources;

  public static SearchFieldRecordsFilter createInstance() {
    var filter = new SearchFieldRecordsFilter();
    filter.initialise();
    return filter;
  }

  @Override
  public void update(SearchResourceRecordsForm form) {
    setShowResults(true);
    setFieldNameForResourceSubmission(form.getFieldNameForResourceSubmission());
    setFieldStatuses(form.getFieldStatuses());
    setGeographicalAreas(form.getGeographicalArea());
    setParentFieldName(form.getParentFieldName());
    setShowArchivedResources(form.getShowArchivedResources());
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

  public String getFieldNameForResourceSubmission() {
    return fieldNameForResourceSubmission;
  }

  public void setFieldNameForResourceSubmission(String fieldNameForResourceSubmission) {
    this.fieldNameForResourceSubmission = fieldNameForResourceSubmission;
  }

  public List<String> getFieldStatuses() {
    return fieldStatuses;
  }

  public void setFieldStatuses(List<String> fieldStatuses) {
    this.fieldStatuses = fieldStatuses;
  }

  public List<String> getGeographicalAreas() {
    return geographicalAreas;
  }

  public String getParentFieldName() {
    return parentFieldName;
  }

  public void setParentFieldName(String parentFieldName) {
    this.parentFieldName = parentFieldName;
  }

  public void setGeographicalAreas(List<String> geographicalAreas) {
    this.geographicalAreas = geographicalAreas;
  }

  public Boolean getShowArchivedResources() {
    return showArchivedResources;
  }

  public void setShowArchivedResources(Boolean showArchivedResources) {
    this.showArchivedResources = showArchivedResources;
  }

  private void initialise() {
    setShowResults(false);
    setFieldNameForResourceSubmission(null);
    setFieldStatuses(new ArrayList<>());
    setGeographicalAreas(new ArrayList<>());
    setParentFieldName(null);
    setShowArchivedResources(false);
  }
}
