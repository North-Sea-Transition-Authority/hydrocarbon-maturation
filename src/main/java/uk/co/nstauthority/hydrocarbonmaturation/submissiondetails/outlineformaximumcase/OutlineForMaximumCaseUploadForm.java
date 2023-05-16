package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import java.util.ArrayList;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class OutlineForMaximumCaseUploadForm implements SubmissionForm {
  private List<FileUploadForm> outlineForMaximumCase = new ArrayList<>();

  public List<FileUploadForm> getOutlineForMaximumCase() {
    return outlineForMaximumCase;
  }

  public void setOutlineForMaximumCase(
      List<FileUploadForm> outlineForMaximumCase) {
    this.outlineForMaximumCase = outlineForMaximumCase;
  }
}
