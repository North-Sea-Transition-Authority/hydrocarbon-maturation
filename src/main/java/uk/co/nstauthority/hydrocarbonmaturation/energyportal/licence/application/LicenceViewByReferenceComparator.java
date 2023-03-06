package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import java.util.Comparator;
import java.util.function.Function;

class LicenceViewByReferenceComparator implements Comparator<LicenceView> {

  /**
   * Compares licence views by their reference, if has same non-numeric part compares the number part of the reference
   * Otherwise compares the strings.
   * @param licenceView1 the first object to be compared.
   * @param licenceView2 the second object to be compared.
   * @return 0 if equal 1 if licenceView1 should appear first, else -1
   */
  @Override
  public int compare(LicenceView licenceView1, LicenceView licenceView2) {
    return Comparator
        .comparing(byReferenceText())
        .thenComparing(byReferenceNumber())
        .compare(licenceView1, licenceView2);
  }

  private Function<LicenceView, String> byReferenceText() {
    //compares the text part of the licence reference
    return licenceView -> licenceView.getName().replaceAll("\\d", "");
  }

  private Function<LicenceView, Integer> byReferenceNumber() {
    //compares the numbers of the licence reference
    return licenceView -> Integer.valueOf(licenceView.getName().replaceAll("\\D", ""));
  }
}
