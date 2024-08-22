package uk.co.nstauthority.hydrocarbonmaturation.field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@Service
public class NewSubmissionFromFieldService {

  private final SubmissionCreateService submissionCreateService;
  private final FieldPersistenceService fieldPersistenceService;

  @Autowired
  public NewSubmissionFromFieldService(SubmissionCreateService submissionCreateService,
                                       FieldPersistenceService fieldPersistenceService) {
    this.submissionCreateService = submissionCreateService;
    this.fieldPersistenceService = fieldPersistenceService;
  }

  public SubmissionDetail createSubmissionFromField(FieldSelectForm form, SubmissionType submissionType, User user) {
    var field = fieldPersistenceService.getFieldOrThrow(Integer.parseInt(form.getFieldSelectedOption()));
    var resource = field.getResource();

    switch (submissionType) {
      case CREATE_CSR -> {
        return submissionCreateService.createCsrFieldSubmissionDetail(resource, user);
      }
      case CREATE_FDP -> {
        return submissionCreateService.createFdpFieldSubmissionDetail(resource, user);
      }
      case CREATE_FDPA -> {
        return submissionCreateService.createFdpaFieldSubmissionDetail(resource, user);
      }
      case CREATE_COP -> {
        return submissionCreateService.createCoPFieldSubmissionDetail(resource, user);
      }
      default ->
          throw new ActionNotAllowedException("No field statuses for submission type: %s".formatted(submissionType));
    }
  }
}