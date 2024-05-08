package uk.co.nstauthority.hydrocarbonmaturation.complexity;

import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ArealDensityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.StructuralComplexityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.VerticalHeterogeneityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ViscosityRange;

public class BaseComplexityForm {
  private ViscosityRange viscosityRange;
  private VerticalHeterogeneityRange verticalHeterogeneityRange;
  private ArealDensityRange arealDensityRange;
  private StructuralComplexityRange structuralComplexityRange;

  public ViscosityRange getViscosityRange() {
    return viscosityRange;
  }

  public void setViscosityRange(ViscosityRange viscosityRange) {
    this.viscosityRange = viscosityRange;
  }

  public VerticalHeterogeneityRange getVerticalHeterogeneityRange() {
    return verticalHeterogeneityRange;
  }

  public void setVerticalHeterogeneityRange(VerticalHeterogeneityRange verticalHeterogeneityRange) {
    this.verticalHeterogeneityRange = verticalHeterogeneityRange;
  }

  public ArealDensityRange getArealDensityRange() {
    return arealDensityRange;
  }

  public void setArealDensityRange(ArealDensityRange arealDensityRange) {
    this.arealDensityRange = arealDensityRange;
  }

  public StructuralComplexityRange getStructuralComplexityRange() {
    return structuralComplexityRange;
  }

  public void setStructuralComplexityRange(StructuralComplexityRange structuralComplexityRange) {
    this.structuralComplexityRange = structuralComplexityRange;
  }
}
