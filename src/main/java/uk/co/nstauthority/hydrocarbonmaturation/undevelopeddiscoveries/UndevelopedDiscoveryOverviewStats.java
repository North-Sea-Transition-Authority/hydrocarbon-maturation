package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.math.BigDecimal;
import uk.co.nstauthority.hydrocarbonmaturation.util.DecimalFormatUtil;

public record UndevelopedDiscoveryOverviewStats(
    Integer undevelopedDiscoveries,
    Integer licensedUndevelopedDiscoveries,
    Integer groupLicensedUndevelopedDiscoveries,
    String totalMmboeOneC,
    String totalMmboeTwoC,
    String totalMmboeThreeC,
    String groupLicensedMmboeOneC,
    String groupLicensedMmboeTwoC,
    String groupLicensedMmboeThreeC,
    String groupUnlicensedMmboeOneC,
    String groupUnlicensedMmboeTwoC,
    String groupUnlicensedMmboeThreeC
) {
  public UndevelopedDiscoveryOverviewStats(Integer undevelopedDiscoveries, Integer licensedUndevelopedDiscoveries,
                                           Integer groupLicensedUndevelopedDiscoveries,
                                           BigDecimal totalMmboeOneC, BigDecimal totalMmboeTwoC, BigDecimal totalMmboeThreeC,
                                           BigDecimal licensedMmboeOneC, BigDecimal licensedMmboeTwoC,
                                           BigDecimal licensedMmboeThreeC,
                                           BigDecimal unlicensedMmboeOneC, BigDecimal unlicensedMmboeTwoC,
                                           BigDecimal unlicensedMmboeThreeC) {
    this(undevelopedDiscoveries,
        licensedUndevelopedDiscoveries,
        groupLicensedUndevelopedDiscoveries,
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(totalMmboeOneC),
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(totalMmboeTwoC),
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(totalMmboeThreeC),
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(licensedMmboeOneC),
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(licensedMmboeTwoC),
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(licensedMmboeThreeC),
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(unlicensedMmboeOneC),
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(unlicensedMmboeTwoC),
        DecimalFormatUtil.bigDecimalToTwoDecimalPlace(unlicensedMmboeThreeC));
  }
}
