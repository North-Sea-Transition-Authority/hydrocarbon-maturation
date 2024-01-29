package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import java.util.ArrayList;
import java.util.List;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class OutlineForMaximumCaseUploadForm implements SubmissionForm {
  private List<UploadedFileForm> outlineForMaximumCase = new ArrayList<>();

  public List<UploadedFileForm> getOutlineForMaximumCase() {
    return outlineForMaximumCase;
  }

  public void setOutlineForMaximumCase(
      List<UploadedFileForm> outlineForMaximumCase) {
    this.outlineForMaximumCase = outlineForMaximumCase;
  }
}
