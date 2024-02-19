package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public interface ProbabilityDistributionTypeBehaviour {
  boolean isMinAvailable();

  boolean isMinRequired();

  boolean isP99Available();

  boolean isP99Required();

  boolean isP90Available();

  boolean isP90Required();

  boolean isModeAvailable();

  boolean isModeRequired();

  boolean isP50Available();

  boolean isP50Required();

  boolean isMeanAvailable();

  boolean isMeanRequired();

  boolean isConstantAvailable();

  boolean isConstantRequired();

  boolean isP10Available();

  boolean isP10Required();

  boolean isP1Available();

  boolean isP1Required();

  boolean isMaxAvailable();

  boolean isMaxRequired();

  boolean isMostLikelyAvailable();

  boolean isMostLikelyRequired();

  boolean is1CAvailable();

  boolean is1CRequired();

  boolean is2CAvailable();

  boolean is2CRequired();

  boolean is3CAvailable();

  boolean is3CRequired();

  boolean is1PAvailable();

  boolean is1PRequired();

  boolean is2PAvailable();

  boolean is2PRequired();

  boolean is3PAvailable();

  boolean is3PRequired();

  boolean allowEquivalentValues();

  ProbabilityDistributionValidationError getP99ToP1Error();

  ProbabilityDistributionValidationError getP90ToP10Error();

  ProbabilityDistributionValidationError getMinMaxError();

  ProbabilityDistributionValidationError get1CTo3CError();

  ProbabilityDistributionValidationError get1PTo3PError();
}
