package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.math.BigDecimal;
import uk.co.nstauthority.hydrocarbonmaturation.util.DecimalFormatUtil;

public record UndevelopedDiscoveryOverviewStats(
    Integer undevelopedDiscoveries,
    Integer licensedUndevelopedDiscoveries,
    Integer groupLicensedUndevelopedDiscoveries,
    String mmboeOneC,
    String mmboeTwoC,
    String mmboeThreeC
) {
  public UndevelopedDiscoveryOverviewStats(Integer undevelopedDiscoveries, Integer licensedUndevelopedDiscoveries,
                                           Integer groupLicensedUndevelopedDiscoveries,
                                           BigDecimal mmboeOneC, BigDecimal mmboeTwoC, BigDecimal mmboeThreeC) {
    this(undevelopedDiscoveries,
        licensedUndevelopedDiscoveries,
        groupLicensedUndevelopedDiscoveries,
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(mmboeOneC),
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(mmboeTwoC),
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(mmboeThreeC));
  }
}
