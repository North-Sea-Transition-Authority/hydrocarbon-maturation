package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import java.util.Comparator;
import java.util.function.Function;

public class LicenceViewByReferenceComparator implements Comparator<LicenceDto> {

  /**
   * Compares licence views by their reference, if has same non-numeric part compares the number part of the reference
   * Otherwise compares the strings.
   * @param licenceDto1 the first object to be compared.
   * @param licenceDto2 the second object to be compared.
   * @return 0 if equal 1 if licenceView1 should appear first, else -1
   */
  @Override
  public int compare(LicenceDto licenceDto1, LicenceDto licenceDto2) {
    return Comparator
        .comparing(byReferenceText())
        .thenComparing(byReferenceNumber())
        .compare(licenceDto1, licenceDto2);
  }

  private Function<LicenceDto, String> byReferenceText() {
    //compares the text part of the licence reference
    return licenceView -> licenceView.getName().replaceAll("\\d", "");
  }

  private Function<LicenceDto, Integer> byReferenceNumber() {
    //compares the numbers of the licence reference
    return licenceView -> Integer.valueOf(licenceView.getName().replaceAll("\\D", ""));
  }
}
