package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import java.util.Set;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ApplyFormErrorOrdering;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ErrorSummaryDisplayOrder;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionSectionType;

@ApplyFormErrorOrdering
class GeneralReservoirInformationForm implements SubmissionForm {

  @ErrorSummaryDisplayOrder(1)
  private String reservoirName;

  @ErrorSummaryDisplayOrder(2)
  private Set<String> associatedDiscoveryReservoirs;

  @ErrorSummaryDisplayOrder(3)
  private String associatedDiscoveryReservoirSelect;

  @ErrorSummaryDisplayOrder(4)
  private HydrocarbonsDiscovered hydrocarbonsDiscovered;

  @ErrorSummaryDisplayOrder(5)
  private Boolean isReservoirBeingDevelopedUnderFuturePhase;

  @ErrorSummaryDisplayOrder(6)
  private Set<ReservoirSubmissionSectionType> reservoirSubmissionSections = Set.of();

  public String getReservoirName() {
    return reservoirName;
  }

  public void setReservoirName(String reservoirName) {
    this.reservoirName = reservoirName;
  }

  public Set<String> getAssociatedDiscoveryReservoirs() {
    return associatedDiscoveryReservoirs;
  }

  public void setAssociatedDiscoveryReservoirs(Set<String> associatedDiscoveryReservoirs) {
    this.associatedDiscoveryReservoirs = associatedDiscoveryReservoirs;
  }

  public String getAssociatedDiscoveryReservoirSelect() {
    return associatedDiscoveryReservoirSelect;
  }

  public void setAssociatedDiscoveryReservoirSelect(String associatedDiscoveryReservoirSelect) {
    this.associatedDiscoveryReservoirSelect = associatedDiscoveryReservoirSelect;
  }

  public HydrocarbonsDiscovered getHydrocarbonsDiscovered() {
    return hydrocarbonsDiscovered;
  }

  public void setHydrocarbonsDiscovered(
      HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    this.hydrocarbonsDiscovered = hydrocarbonsDiscovered;
  }

  public Boolean getIsReservoirBeingDevelopedUnderFuturePhase() {
    return isReservoirBeingDevelopedUnderFuturePhase;
  }

  public void setIsReservoirBeingDevelopedUnderFuturePhase(Boolean reservoirBeingDevelopedUnderFuturePhase) {
    this.isReservoirBeingDevelopedUnderFuturePhase = reservoirBeingDevelopedUnderFuturePhase;
  }

  public Set<ReservoirSubmissionSectionType> getReservoirSubmissionSections() {
    return reservoirSubmissionSections;
  }

  public void setReservoirSubmissionSections(Set<ReservoirSubmissionSectionType> reservoirSubmissionSections) {
    this.reservoirSubmissionSections = reservoirSubmissionSections;
  }
}