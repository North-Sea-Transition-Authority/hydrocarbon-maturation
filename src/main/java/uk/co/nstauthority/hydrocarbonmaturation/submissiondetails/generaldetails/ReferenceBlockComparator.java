package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Comparator;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;

public class ReferenceBlockComparator implements Comparator<QuadrantAndBlock> {

  @Override
  public int compare(QuadrantAndBlock qb1, QuadrantAndBlock qb2) {
    return Comparator
        .comparing(byOnshoreOffShore())
        .thenComparing(byReferenceBlock())
        .compare(qb1, qb2);
  }

  private Function<QuadrantAndBlock, Integer> byOnshoreOffShore() {
    return referenceBlock -> switch (referenceBlock.getOnshoreOffshore()) {
      case OFFSHORE -> 0;
      case ONSHORE -> 1;
    };
  }


  private Function<QuadrantAndBlock, String> byReferenceBlock() {
    return referenceBlock ->
        StringUtils.leftPad(referenceBlock.getQuadrant(), 10, "0") +
            StringUtils.leftPad(referenceBlock.getBlock().toString(), 10, "0");
  }
}
