package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapType;

public class FieldTrapPropertiesForm implements SubmissionForm {
  private TrapType trapType;

  private String otherTrapType;

  private ProbabilityDistributionForm depthToCrest = new ProbabilityDistributionForm();

  private ProbabilityDistributionForm depthToSpillPoint = new ProbabilityDistributionForm();

  private ProbabilityDistributionForm productiveArea = new ProbabilityDistributionForm();

  private String closureMechanisms;

  private DecimalInput verticallySeparateHcPools = new DecimalInput();

  private DecimalInput structuralCompartments = new DecimalInput();

  private DecimalInput originalHydrocarbonColumnHeight = new DecimalInput();

  public TrapType getTrapType() {
    return trapType;
  }

  public void setTrapType(TrapType trapType) {
    this.trapType = trapType;
  }

  public ProbabilityDistributionForm getDepthToCrest() {
    return depthToCrest;
  }

  public String getOtherTrapType() {
    return otherTrapType;
  }

  public void setOtherTrapType(String otherTrapType) {
    this.otherTrapType = otherTrapType;
  }

  public void setDepthToCrest(ProbabilityDistributionForm depthToCrest) {
    this.depthToCrest = depthToCrest;
  }

  public ProbabilityDistributionForm getDepthToSpillPoint() {
    return depthToSpillPoint;
  }

  public void setDepthToSpillPoint(ProbabilityDistributionForm depthToSpillPoint) {
    this.depthToSpillPoint = depthToSpillPoint;
  }

  public ProbabilityDistributionForm getProductiveArea() {
    return productiveArea;
  }

  public void setProductiveArea(ProbabilityDistributionForm productiveArea) {
    this.productiveArea = productiveArea;
  }

  public String getClosureMechanisms() {
    return closureMechanisms;
  }

  public void setClosureMechanisms(String closureMechanisms) {
    this.closureMechanisms = closureMechanisms;
  }

  public DecimalInput getVerticallySeparateHcPools() {
    return verticallySeparateHcPools;
  }

  public void setVerticallySeparateHcPools(DecimalInput verticallySeparateHcPools) {
    this.verticallySeparateHcPools = verticallySeparateHcPools;
  }

  public DecimalInput getStructuralCompartments() {
    return structuralCompartments;
  }

  public void setStructuralCompartments(DecimalInput structuralCompartments) {
    this.structuralCompartments = structuralCompartments;
  }

  public DecimalInput getOriginalHydrocarbonColumnHeight() {
    return originalHydrocarbonColumnHeight;
  }

  public void setOriginalHydrocarbonColumnHeight(DecimalInput originalHydrocarbonColumnHeight) {
    this.originalHydrocarbonColumnHeight = originalHydrocarbonColumnHeight;
  }
}
