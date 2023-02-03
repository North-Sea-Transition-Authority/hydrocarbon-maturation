package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResourceRecordSearchResultItemModelProperties {

  private final HashMap<String, Object> properties = new LinkedHashMap<>();

  ResourceRecordSearchResultItemModelProperties addProperty(String key, Object value) {
    properties.put(key, value);
    return this;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }
}
