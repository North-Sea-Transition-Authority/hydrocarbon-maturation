package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.ArrayList;
import java.util.List;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;

public class RegulatorManagedInformationForm {

  private GeographicalArea geographicalArea;
  private List<UploadedFileForm> relinquishmentReport = new ArrayList<>();
  private ResponsibleTeamType responsibleTeamType;

  public GeographicalArea getGeographicalArea() {
    return geographicalArea;
  }

  public void setGeographicalArea(
      GeographicalArea geographicalArea) {
    this.geographicalArea = geographicalArea;
  }

  public List<UploadedFileForm> getRelinquishmentReport() {
    return relinquishmentReport;
  }

  public void setRelinquishmentReport(
      List<UploadedFileForm> relinquishmentReport) {
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
