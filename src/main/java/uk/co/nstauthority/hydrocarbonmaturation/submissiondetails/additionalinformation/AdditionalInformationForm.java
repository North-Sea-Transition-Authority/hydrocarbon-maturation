package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import java.util.ArrayList;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class AdditionalInformationForm implements SubmissionForm {

  private Boolean providingAdditionalComments;
  private String additionalComments;
  private Boolean providingAdditionalDocuments;
  private List<FileUploadDetail> additionalDocuments = new ArrayList<>();

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

  public List<FileUploadDetail> getAdditionalDocuments() {
    return additionalDocuments;
  }

  public void setAdditionalDocuments(
      List<FileUploadDetail> additionalDocuments) {
    this.additionalDocuments = additionalDocuments;
  }
}
