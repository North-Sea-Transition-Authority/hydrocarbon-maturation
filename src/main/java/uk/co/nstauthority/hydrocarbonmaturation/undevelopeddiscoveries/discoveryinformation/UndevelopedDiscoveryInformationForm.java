package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

public class UndevelopedDiscoveryInformationForm {
  private String discoveryInformationDateDay;
  private String discoveryInformationDateMonth;
  private String discoveryInformationDateYear;
  private UndevelopedDiscoveryVolumetricsForm volumetricsForm = new UndevelopedDiscoveryVolumetricsForm();
  private Boolean provideDiscoveryComplexity;
  private UndevelopedDiscoveryComplexityForm complexityForm = new UndevelopedDiscoveryComplexityForm();

  public String getDiscoveryInformationDateYear() {
    return discoveryInformationDateYear;
  }

  public void setDiscoveryInformationDateYear(String discoveryInformationDateYear) {
    this.discoveryInformationDateYear = discoveryInformationDateYear;
  }

  public String getDiscoveryInformationDateMonth() {
    return discoveryInformationDateMonth;
  }

  public void setDiscoveryInformationDateMonth(String discoveryInformationDateMonth) {
    this.discoveryInformationDateMonth = discoveryInformationDateMonth;
  }

  public String getDiscoveryInformationDateDay() {
    return discoveryInformationDateDay;
  }

  public void setDiscoveryInformationDateDay(String discoveryInformationDateDay) {
    this.discoveryInformationDateDay = discoveryInformationDateDay;
  }

  public Boolean getProvideDiscoveryComplexity() {
    return provideDiscoveryComplexity;
  }

  public void setProvideDiscoveryComplexity(Boolean provideDiscoveryComplexity) {
    this.provideDiscoveryComplexity = provideDiscoveryComplexity;
  }

  public UndevelopedDiscoveryComplexityForm getComplexityForm() {
    return complexityForm;
  }

  public void setComplexityForm(UndevelopedDiscoveryComplexityForm complexityForm) {
    this.complexityForm = complexityForm;
  }

  public UndevelopedDiscoveryVolumetricsForm getVolumetricsForm() {
    return volumetricsForm;
  }

  public void setVolumetricsForm(
      UndevelopedDiscoveryVolumetricsForm volumetricsForm) {
    this.volumetricsForm = volumetricsForm;
  }
}
