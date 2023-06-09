package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;

@Entity
@Table(name = "reservoir_properties")
class ReservoirProperty implements SubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  @Enumerated(EnumType.STRING)
  private Formation formationName;

  private String formationNameManual;

  @Enumerated(EnumType.STRING)
  private ReservoirSubUnit subUnit;

  private String subUnitManual;

  @Enumerated(EnumType.STRING)
  private AgeSeries ageStage;

  private String ageStageManual;

  @Enumerated(EnumType.STRING)
  private GroupName groupName;

  @Enumerated(EnumType.STRING)
  private ReservoirLithology lithology;

  private String lithologyOther;

  private String reservoirFacies;
  private String reservoirConnectivity;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  public Formation getFormationName() {
    return formationName;
  }

  public void setFormationName(Formation formationName) {
    this.formationName = formationName;
  }

  public String getFormationNameManual() {
    return formationNameManual;
  }

  public void setFormationNameManual(String formationNameManual) {
    this.formationNameManual = formationNameManual;
  }

  public ReservoirSubUnit getSubUnit() {
    return subUnit;
  }

  public void setSubUnit(
      ReservoirSubUnit subUnit) {
    this.subUnit = subUnit;
  }

  public String getSubUnitManual() {
    return subUnitManual;
  }

  public void setSubUnitManual(String subUnitManual) {
    this.subUnitManual = subUnitManual;
  }

  public AgeSeries getAgeStage() {
    return ageStage;
  }

  public void setAgeStage(AgeSeries ageStage) {
    this.ageStage = ageStage;
  }

  public String getAgeStageManual() {
    return ageStageManual;
  }

  public void setAgeStageManual(String ageStageManual) {
    this.ageStageManual = ageStageManual;
  }

  public GroupName getGroupName() {
    return groupName;
  }

  public void setGroupName(
      GroupName groupName) {
    this.groupName = groupName;
  }

  public ReservoirLithology getLithology() {
    return lithology;
  }

  public void setLithology(
      ReservoirLithology lithology) {
    this.lithology = lithology;
  }

  public String getLithologyOther() {
    return lithologyOther;
  }

  public void setLithologyOther(String lithologyOther) {
    this.lithologyOther = lithologyOther;
  }

  public String getReservoirFacies() {
    return reservoirFacies;
  }

  public void setReservoirFacies(String reservoirFacies) {
    this.reservoirFacies = reservoirFacies;
  }

  public String getReservoirConnectivity() {
    return reservoirConnectivity;
  }

  public void setReservoirConnectivity(String reservoirConnectivity) {
    this.reservoirConnectivity = reservoirConnectivity;
  }
}
