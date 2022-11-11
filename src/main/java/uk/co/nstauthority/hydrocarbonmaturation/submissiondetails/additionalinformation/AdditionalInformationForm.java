package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import java.util.ArrayList;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class AdditionalInformationForm implements SubmissionForm {

  private Boolean providingAdditionalComments;
  private String additionalComments;
  private Boolean providingAdditionalDocuments;
  private List<FileUploadForm> additionalDocuments = new ArrayList<>();

  public Boolean getProvidingAdditionalComments() {
    return providingAdditionalComments;
  }

  public void setProvidingAdditionalComments(Boolean providingAdditionalComments) {
    this.providingAdditionalComments = providingAdditionalComments;
  }

  public String getAdditionalComments() {
    return additionalComments;
  }

  public void setAdditionalComments(String additionalComments) {
    this.additionalComments = additionalComments;
  }

  public Boolean getProvidingAdditionalDocuments() {
    return providingAdditionalDocuments;
  }

  public void setProvidingAdditionalDocuments(Boolean providingAdditionalDocuments) {
    this.providingAdditionalDocuments = providingAdditionalDocuments;
  }

  public List<FileUploadForm> getAdditionalDocuments() {
    return additionalDocuments;
  }

  public void setAdditionalDocuments(
      List<FileUploadForm> additionalDocuments) {
    this.additionalDocuments = additionalDocuments;
  }
}
