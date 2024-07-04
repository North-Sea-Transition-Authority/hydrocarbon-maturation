package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

public class SummaryBehaviour extends BaseBehaviour implements ProbabilityDistributionTypeBehaviour {

  private boolean isMinAvailable;
  private boolean isP99Available;
  private boolean isP90Available;
  private boolean isModeAvailable;
  private boolean isP50Available;
  private boolean isMeanAvailable;
  private boolean isConstantAvailable;
  private boolean isP10Available;
  private boolean isP1Available;
  private boolean isMaxAvailable;
  private boolean isMostLikelyAvailable;
  private boolean is1CAvailable;
  private boolean is2CAvailable;
  private boolean is3CAvailable;
  private boolean is1PAvailable;
  private boolean is2PAvailable;
  private boolean is3PAvailable;

  public SummaryBehaviour() {
    isMinAvailable = false;
    isP99Available = false;
    isP90Available = false;
    isModeAvailable = false;
    isP50Available = false;
    isMeanAvailable = false;
    isConstantAvailable = false;
    isP10Available = false;
    isP1Available = false;
    isMaxAvailable = false;
    isMostLikelyAvailable = false;
    is1CAvailable = false;
    is2CAvailable = false;
    is3CAvailable = false;
    is1PAvailable = false;
    is2PAvailable = false;
    is3PAvailable = false;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private boolean isMinAvailable;
    private boolean isP99Available;
    private boolean isP90Available;
    private boolean isModeAvailable;
    private boolean isP50Available;
    private boolean isMeanAvailable;
    private boolean isConstantAvailable;
    private boolean isP10Available;
    private boolean isP1Available;
    private boolean isMaxAvailable;
    private boolean isMostLikelyAvailable;
    private boolean is1CAvailable;
    private boolean is2CAvailable;
    private boolean is3CAvailable;
    private boolean is1PAvailable;
    private boolean is2PAvailable;
    private boolean is3PAvailable;

    private Builder() {
    }

    public Builder withMin() {
      this.isMinAvailable = true;
      return this;
    }

    public Builder withP99() {
      this.isP99Available = true;
      return this;
    }

    public Builder withP90() {
      this.isP90Available = true;
      return this;
    }

    public Builder withMode() {
      this.isModeAvailable = true;
      return this;
    }

    public Builder withP50() {
      this.isP50Available = true;
      return this;
    }

    public Builder withMean() {
      this.isMeanAvailable = true;
      return this;
    }

    public Builder withConstant() {
      this.isConstantAvailable = true;
      return this;
    }

    public Builder withP10() {
      this.isP10Available = true;
      return this;
    }

    public Builder withP1() {
      this.isP1Available = true;
      return this;
    }

    public Builder withMax() {
      this.isMaxAvailable = true;
      return this;
    }

    public Builder withMostLikely() {
      this.isMostLikelyAvailable = true;
      return this;
    }

    public Builder with1C() {
      this.is1CAvailable = true;
      return this;
    }

    public Builder with2C() {
      this.is2CAvailable = true;
      return this;
    }

    public Builder with3C() {
      this.is3CAvailable = true;
      return this;
    }

    public Builder with1P() {
      this.is1PAvailable = true;
      return this;
    }

    public Builder with2P() {
      this.is2PAvailable = true;
      return this;
    }

    public Builder with3P() {
      this.is3PAvailable = true;
      return this;
    }

    public SummaryBehaviour build() {
      var summaryBehaviour = new SummaryBehaviour();
      summaryBehaviour.setMinAvailable(isMinAvailable);
      summaryBehaviour.setP99Available(isP99Available);
      summaryBehaviour.setP90Available(isP90Available);
      summaryBehaviour.setModeAvailable(isModeAvailable);
      summaryBehaviour.setP50Available(isP50Available);
      summaryBehaviour.setMeanAvailable(isMeanAvailable);
      summaryBehaviour.setConstantAvailable(isConstantAvailable);
      summaryBehaviour.setP10Available(isP10Available);
      summaryBehaviour.setP1Available(isP1Available);
      summaryBehaviour.setMaxAvailable(isMaxAvailable);
      summaryBehaviour.setMostLikelyAvailable(isMostLikelyAvailable);
      summaryBehaviour.setIs1CAvailable(is1CAvailable);
      summaryBehaviour.setIs2CAvailable(is2CAvailable);
      summaryBehaviour.setIs3CAvailable(is3CAvailable);
      summaryBehaviour.setIs1PAvailable(is1PAvailable);
      summaryBehaviour.setIs2PAvailable(is2PAvailable);
      summaryBehaviour.setIs3PAvailable(is3PAvailable);
      return summaryBehaviour;
    }
  }

  @Override
  public boolean isMinAvailable() {
    return isMinAvailable;
  }

  private void setMinAvailable(boolean minAvailable) {
    isMinAvailable = minAvailable;
  }

  @Override
  public boolean isP99Available() {
    return isP99Available;
  }

  private void setP99Available(boolean p99Available) {
    isP99Available = p99Available;
  }

  @Override
  public boolean isP90Available() {
    return isP90Available;
  }

  private void setP90Available(boolean p90Available) {
    isP90Available = p90Available;
  }

  @Override
  public boolean isModeAvailable() {
    return isModeAvailable;
  }

  private void setModeAvailable(boolean modeAvailable) {
    isModeAvailable = modeAvailable;
  }

  @Override
  public boolean isP50Available() {
    return isP50Available;
  }

  private void setP50Available(boolean p50Available) {
    isP50Available = p50Available;
  }

  @Override
  public boolean isMeanAvailable() {
    return isMeanAvailable;
  }

  private void setMeanAvailable(boolean meanAvailable) {
    isMeanAvailable = meanAvailable;
  }

  @Override
  public boolean isConstantAvailable() {
    return isConstantAvailable;
  }

  private void setConstantAvailable(boolean constantAvailable) {
    isConstantAvailable = constantAvailable;
  }

  @Override
  public boolean isP10Available() {
    return isP10Available;
  }

  private void setP10Available(boolean p10Available) {
    isP10Available = p10Available;
  }

  @Override
  public boolean isP1Available() {
    return isP1Available;
  }

  private void setP1Available(boolean p1Available) {
    isP1Available = p1Available;
  }

  @Override
  public boolean isMaxAvailable() {
    return isMaxAvailable;
  }

  private void setMaxAvailable(boolean maxAvailable) {
    isMaxAvailable = maxAvailable;
  }

  @Override
  public boolean isMostLikelyAvailable() {
    return isMostLikelyAvailable;
  }

  private void setMostLikelyAvailable(boolean mostLikelyAvailable) {
    isMostLikelyAvailable = mostLikelyAvailable;
  }

  @Override
  public boolean is1CAvailable() {
    return is1CAvailable;
  }

  private void setIs1CAvailable(boolean is1CAvailable) {
    this.is1CAvailable = is1CAvailable;
  }

  @Override
  public boolean is2CAvailable() {
    return is2CAvailable;
  }

  private void setIs2CAvailable(boolean is2CAvailable) {
    this.is2CAvailable = is2CAvailable;
  }

  @Override
  public boolean is3CAvailable() {
    return is3CAvailable;
  }

  private void setIs3CAvailable(boolean is3CAvailable) {
    this.is3CAvailable = is3CAvailable;
  }

  @Override
  public boolean is1PAvailable() {
    return is1PAvailable;
  }

  private void setIs1PAvailable(boolean is1PAvailable) {
    this.is1PAvailable = is1PAvailable;
  }

  @Override
  public boolean is2PAvailable() {
    return is2PAvailable;
  }

  private void setIs2PAvailable(boolean is2PAvailable) {
    this.is2PAvailable = is2PAvailable;
  }

  @Override
  public boolean is3PAvailable() {
    return is3PAvailable;
  }

  private void setIs3PAvailable(boolean is3PAvailable) {
    this.is3PAvailable = is3PAvailable;
  }
}