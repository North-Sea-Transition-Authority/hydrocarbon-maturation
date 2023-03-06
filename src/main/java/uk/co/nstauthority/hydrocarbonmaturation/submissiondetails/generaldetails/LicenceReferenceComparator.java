package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Comparator;
import java.util.function.Function;

public class LicenceReferenceComparator implements Comparator<Licence> {

  @Override
  public int compare(Licence o1, Licence o2) {
    return Comparator
        .comparing(byReferenceText())
        .thenComparing(byReferenceNumber())
        .compare(o1, o2);
  }

  private Function<Licence, String> byReferenceText() {
    //compares the text part of the licence reference
    return licence -> licence.getLicenceReference().replaceAll("\\d", "");
  }

  private Function<Licence, Integer> byReferenceNumber() {
    //compares the numbers of the licence reference
    return licence -> Integer.valueOf(licence.getLicenceReference().replaceAll("\\D", ""));
  }
}
