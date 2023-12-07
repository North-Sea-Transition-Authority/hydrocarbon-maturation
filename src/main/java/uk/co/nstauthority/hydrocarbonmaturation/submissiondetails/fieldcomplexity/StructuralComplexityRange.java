package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum StructuralComplexityRange implements Displayable {
  EXCELLENT_RESERVOIR_QUALITY(
      "Excellent reservoir quality",
      "Good connectivity, no significant baffles or barriers",
      10,
      1),
  MINOR_BARRIER_BAFFLE_EFFECT(
      "Reservoir performance affected by minor baffles or barriers, such as faulting or lithology or facies change",
      "",
      20,
      2),
  COMPLEX_GEOMETRIES_COMPARTMENTALISATION(
      "Multi-scale faulting or complex sedimentary geometries lead to compartmentalisation",
      "May have the potential for early water breakthrough",
      30,
      3),
  LABYRINTHINE_RESERVOIR_ARCHITECTURE(
      "Complex reservoir with labyrinthine architecture. Connectivity hard to predict, may have multiple contacts",
      "Potential for water or gas breakthrough via permeable conduits, such as injectites, faults or fractures",
      40,
      4),
  HIGHLY_FAULTED_AND_FRACTURED(
      "Highly faulted and fractured reservoir with multiple compartments. Likely to have multiple fluid contacts",
      "Despite compartmentalisation, early water breakthrough may occur via faults",
      50,
      5);

  private final String displayName;
  private final String description;
  private final int displayOrder;
  private final int value;

  StructuralComplexityRange(String displayName, String description, int displayOrder, int value) {
    this.displayName = displayName;
    this.description = description;
    this.displayOrder = displayOrder;
    this.value = value;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }

  public int getValue() {
    return value;
  }
}
