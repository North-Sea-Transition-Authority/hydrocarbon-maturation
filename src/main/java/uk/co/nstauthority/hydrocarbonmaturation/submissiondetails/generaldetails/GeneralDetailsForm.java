package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.ArrayList;
import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;

class GeneralDetailsForm implements SubmissionForm {

  private String prospectName;
  //TODO HM-129
  private OnshoreOffshore onshoreOffshore;
  private Boolean prospectLocatedInUkcs;
  private DecimalInput percentageLocatedInUkcs;
  //TODO HM-129

  private List<String> quadrantBlocks = new ArrayList<>();
  private String quadrantBlockSelect;
  private InformationSource informationSource;
  private String otherSourceOfInformation;

  public String getProspectName() {
    return prospectName;
  }

  public void setProspectName(String prospectName) {
    this.prospectName = prospectName;
  }

  public OnshoreOffshore getOnshoreOffshore() {
    return onshoreOffshore;
  }

  public void setOnshoreOffshore(OnshoreOffshore onshoreOffshore) {
    this.onshoreOffshore = onshoreOffshore;
  }

  public Boolean getProspectLocatedInUkcs() {
    return prospectLocatedInUkcs;
  }

  public void setProspectLocatedInUkcs(Boolean prospectLocatedInUkcs) {
    this.prospectLocatedInUkcs = prospectLocatedInUkcs;
  }

  public DecimalInput getPercentageLocatedInUkcs() {
    return percentageLocatedInUkcs;
  }

  public void setPercentageLocatedInUkcs(DecimalInput percentageLocatedInUkcs) {
    this.percentageLocatedInUkcs = percentageLocatedInUkcs;
  }

  public List<String> getQuadrantBlocks() {
    return quadrantBlocks;
  }

  public void setQuadrantBlocks(List<String> quadrantBlocks) {
    this.quadrantBlocks = quadrantBlocks;
  }

  public String getQuadrantBlockSelect() {
    return quadrantBlockSelect;
  }

  public void setQuadrantBlockSelect(String quadrantBlockSelect) {
    this.quadrantBlockSelect = quadrantBlockSelect;
  }

  public InformationSource getInformationSource() {
    return informationSource;
  }

  public void setInformationSource(
      InformationSource informationSource) {
    this.informationSource = informationSource;
  }

  public String getOtherSourceOfInformation() {
    return otherSourceOfInformation;
  }

  public void setOtherSourceOfInformation(String otherSourceOfInformation) {
    this.otherSourceOfInformation = otherSourceOfInformation;
  }
}
