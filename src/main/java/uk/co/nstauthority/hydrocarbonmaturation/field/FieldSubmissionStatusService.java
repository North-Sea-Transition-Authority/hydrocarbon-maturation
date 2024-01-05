package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class FieldSubmissionStatusService {
  private final FieldStatusService fieldStatusService;
  private final FieldRepository fieldRepository;

  @Autowired
  public FieldSubmissionStatusService(FieldStatusService fieldStatusService,
                                      FieldRepository fieldRepository) {
    this.fieldStatusService = fieldStatusService;
    this.fieldRepository = fieldRepository;
  }

  public void updateFieldStatusForSubmission(SubmissionDetail submissionDetail) {
    var resource = submissionDetail.getSubmission().getResource();
    var field = fieldRepository.findByResource(resource).orElseThrow(() ->
        new RemiEntityNotFoundException("Could not find field for the resource with id %s".formatted(resource.getId())));
    getNewStatusForSubmission(submissionDetail)
        .ifPresent(newStatus -> fieldStatusService.setMostRecentFieldStatus(field, newStatus));
  }

  public void updateFieldStatusForAcceptance(SubmissionDetail submissionDetail) {
    var resource = submissionDetail.getSubmission().getResource();
    var field = fieldRepository.findByResource(resource).orElseThrow(() ->
        new RemiEntityNotFoundException("Could not find field for the resource with id %s".formatted(resource.getId())));
    getNewStatusForAcceptance(submissionDetail)
        .ifPresent(newStatus -> fieldStatusService.setMostRecentFieldStatus(field, newStatus));
  }

  private static Optional<FieldStatus> getNewStatusForSubmission(SubmissionDetail submissionDetail) {
    return switch (submissionDetail.getSubmissionType()) {
      case CREATE_CSR -> Optional.of(FieldStatus.STATUS_300);
      case CREATE_FDP -> Optional.of(FieldStatus.STATUS_500);
      default -> Optional.empty();
    };
  }

  private static Optional<FieldStatus> getNewStatusForAcceptance(SubmissionDetail submissionDetail) {
    return switch (submissionDetail.getSubmissionType()) {
      case CREATE_CSR -> Optional.of(FieldStatus.STATUS_400);
      case CREATE_FDP -> Optional.of(FieldStatus.STATUS_600);
      case CREATE_COP -> Optional.of(FieldStatus.STATUS_900);
      default -> Optional.empty();
    };
  }
}
