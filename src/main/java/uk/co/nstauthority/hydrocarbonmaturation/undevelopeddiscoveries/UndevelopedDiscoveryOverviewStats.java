package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.math.BigDecimal;
import uk.co.nstauthority.hydrocarbonmaturation.util.DecimalFormatUtil;

public record UndevelopedDiscoveryOverviewStats(
    Integer undevelopedDiscoveries,
    Integer licensedUndevelopedDiscoveries,
    String mmboeOneC,
    String mmboeTwoC,
    String mmboeThreeC
) {
  public UndevelopedDiscoveryOverviewStats(Integer undevelopedDiscoveries, Integer licensedUndevelopedDiscoveries,
                                           BigDecimal mmboeOneC, BigDecimal mmboeTwoC, BigDecimal mmboeThreeC) {
    this(undevelopedDiscoveries,
        licensedUndevelopedDiscoveries,
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(mmboeOneC),
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(mmboeTwoC),
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(mmboeThreeC));
  }
}
