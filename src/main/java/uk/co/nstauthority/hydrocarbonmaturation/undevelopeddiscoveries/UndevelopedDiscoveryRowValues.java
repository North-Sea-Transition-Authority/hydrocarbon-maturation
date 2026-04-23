package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.math.BigDecimal;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableValue;

public record UndevelopedDiscoveryRowValues(
    SummaryTableValue discovery,
    Integer yearAdded,
    Boolean licensed,
    Boolean groupLicensed,
    String geographicalArea,
    BigDecimal mmboeOneC,
    BigDecimal mmboeTwoC,
    BigDecimal mmboeThreeC,
    String percentageGas,
    BigDecimal complexity
) {
}
