package uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application;

import java.util.Comparator;

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
    int i1 = getNumbersFromReference(licenceView1.getName());
    int i2 = getNumbersFromReference(licenceView2.getName());
    String s1 = getStringWithoutNumbers(licenceView1.getName());
    String s2 = getStringWithoutNumbers(licenceView2.getName());

    if (s1.equals(s2)) {
      return Integer.compare(i1, i2);
    } else {
      return licenceView1.getName().compareTo(licenceView2.getName());
    }
  }

  private String getStringWithoutNumbers(String reference) {
    return reference.replaceAll("\\d", "");
  }

  private Integer getNumbersFromReference(String s) {
    // for sorting purposes, keep only numbers
    // this means A10 will order after A2 (for example)
    var numbersOnly = s.replaceAll("\\D", "");
    if (!numbersOnly.isEmpty()) {
      return Integer.parseInt(numbersOnly);
    } else {
      // if a reference has no numbers, put it last in the sort
      return Integer.MAX_VALUE;
    }
  }
}
