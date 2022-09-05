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

}
