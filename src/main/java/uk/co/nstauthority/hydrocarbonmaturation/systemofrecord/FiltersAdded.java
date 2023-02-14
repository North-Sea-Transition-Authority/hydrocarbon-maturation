package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

enum FiltersAdded {
  YES(true),
  NO(false)
  ;

  private final boolean hasFilters;

  FiltersAdded(boolean hasFilters) {
    this.hasFilters = hasFilters;
  }

  boolean isHasFilters() {
    return hasFilters;
  }
}
