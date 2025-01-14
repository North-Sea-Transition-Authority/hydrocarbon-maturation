package uk.co.nstauthority.hydrocarbonmaturation.externalapi.fielddevelopmentplan;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

@Service
public class FieldDevelopmentPlanExternalService {

  private final FieldPersistenceService fieldPersistenceService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final SubmissionDetailService submissionDetailService;

  public FieldDevelopmentPlanExternalService(
      FieldPersistenceService fieldPersistenceService,
      GeneralDetailsPersistenceService generalDetailsPersistenceService,
      SubmissionDetailService submissionDetailService
  ) {
    this.fieldPersistenceService = fieldPersistenceService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.submissionDetailService = submissionDetailService;
  }

  Map<Integer, FieldDevelopmentPlanExternalDto> createFdpDtosByFieldIds(Set<Integer> fieldIds) {
    return fieldIds.stream()
        .map(fieldPersistenceService::getField)
        .flatMap(Optional::stream)
        .collect(StreamUtil.toLinkedHashMap(
            Field::getFieldIdentifier,
            this::createFdpDtoByField
        ));
  }

  private FieldDevelopmentPlanExternalDto createFdpDtoByField(Field field) {
    var submissionDetails = submissionDetailService.getAcceptedExtantSubmissionDetailsForResource(field.getResource());

    var fdpGeneralDetail = submissionDetails.stream()
        .filter(SubmissionDetail::isFieldDevelopmentPlanSubmission)
        .map(generalDetailsPersistenceService::getGeneralDetailOrThrow)
        .findFirst();

    return fdpGeneralDetail.map(generalDetail ->
        new FieldDevelopmentPlanExternalDto(
            generalDetail.getFdpDate(),
            generalDetail.getFdpTitle(),
            createFdpaDtos(submissionDetails)
        )
    ).orElse(FieldDevelopmentPlanExternalDto.empty());
  }

  private List<FieldDevelopmentPlanAddendumExternalDto> createFdpaDtos(List<SubmissionDetail> submissionDetails) {
    return submissionDetails.stream()
        .filter(SubmissionDetail::isFieldDevelopmentPlanAddendumSubmission)
        .map(generalDetailsPersistenceService::getGeneralDetailBySubmissionDetail)
        .flatMap(Optional::stream)
        .map(generalDetail ->
            new FieldDevelopmentPlanAddendumExternalDto(
                generalDetail.getFdpDate(),
                generalDetail.getFdpTitle()
            )
        )
        .toList();
  }

}
