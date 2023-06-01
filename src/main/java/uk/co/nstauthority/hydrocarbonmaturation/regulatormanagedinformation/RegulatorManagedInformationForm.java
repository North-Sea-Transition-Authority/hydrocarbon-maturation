package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.ArrayList;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;

public class RegulatorManagedInformationForm {

  private GeographicalArea geographicalArea;
  private List<FileUploadForm> relinquishmentReport = new ArrayList<>();
  private ResponsibleTeamType responsibleTeamType;

  public GeographicalArea getGeographicalArea() {
    return geographicalArea;
  }

  public void setGeographicalArea(
      GeographicalArea geographicalArea) {
    this.geographicalArea = geographicalArea;
  }

  public List<FileUploadForm> getRelinquishmentReport() {
    return relinquishmentReport;
  }

  public void setRelinquishmentReport(
      List<FileUploadForm> relinquishmentReport) {
    this.relinquishmentReport = relinquishmentReport;
  }

  public ResponsibleTeamType getResponsibleTeamType() {
    return responsibleTeamType;
  }

  public void setResponsibleTeamType(
      ResponsibleTeamType responsibleTeamType) {
    this.responsibleTeamType = responsibleTeamType;
  }
}
