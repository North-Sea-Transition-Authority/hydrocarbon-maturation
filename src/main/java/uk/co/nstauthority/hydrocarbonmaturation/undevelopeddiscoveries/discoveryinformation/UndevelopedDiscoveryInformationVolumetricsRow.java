package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.summary.CrossTabRow;

public record UndevelopedDiscoveryInformationVolumetricsRow(String rowKey, String p50, String oneC, String twoC,
                                                            String threeC, String units) implements CrossTabRow {
  @Override
  public String getKey() {
    return rowKey;
  }

  @Override
  public List<String> getValues() {
    return List.of(p50, oneC, twoC, threeC, units);
  }
}
