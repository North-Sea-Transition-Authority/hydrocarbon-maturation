package uk.co.nstauthority.hydrocarbonmaturation.externalapi.fielddevelopmentplan;

import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;

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

  @Transactional
  List<FieldDevelopmentPlanExternalDto> createFdpDtosByFieldIds(List<Integer> fieldIds) {
    var fields = fieldPersistenceService.getFields(fieldIds);

    var resources = fields.stream()
        .map(Field::getResource)
        .toList();

    Map<Resource, Field> resourceFieldMap = fields
        .stream()
        .collect(Collectors.toMap(Field::getResource, field -> field));

    var submissionDetails = submissionDetailService.getAcceptedExtantSubmissionDetailsForResources(resources);

    var generalDetails = generalDetailsPersistenceService.getGeneralDetailsForSubmissionDetails(submissionDetails);

    Set<GeneralDetail> fdpGeneralDetails = generalDetails
        .stream()
        .filter(gd -> gd.getSubmissionDetail().isFieldDevelopmentPlanSubmission())
        .collect(Collectors.toSet());


    Map<Field, GeneralDetail> fdpMap = new HashMap<>();
    fdpGeneralDetails.forEach(generalDetail ->
        fdpMap.put(
            resourceFieldMap.get(generalDetail.getSubmissionDetail().getSubmission().getResource()),
            generalDetail
        )
    );

    Set<GeneralDetail> fdpaGeneralDetails = generalDetails
        .stream()
        .filter(gd -> gd.getSubmissionDetail().isFieldDevelopmentPlanAddendumSubmission())
        .collect(Collectors.toSet());

    Map<Resource, List<GeneralDetail>> fdpaGeneralDetailMap =
        fdpaGeneralDetails
            .stream()
            .collect(
                Collectors.groupingBy(gd -> gd.getSubmissionDetail().getSubmission().getResource(),
                Collectors.toList())
            );

    Map<Field, List<GeneralDetail>> fdpaMap = new HashMap<>();
    fdpaGeneralDetailMap.forEach((resource, generalDetail) -> fdpaMap.put(resourceFieldMap.get(resource), generalDetail));

    return createFdpDtos(fdpMap, fdpaMap);
  }

  private List<FieldDevelopmentPlanExternalDto> createFdpDtos(
      Map<Field, GeneralDetail> fdpMap,
      Map<Field, List<GeneralDetail>> fdpaMap) {
    return fdpMap.entrySet().stream()
        .map(fdp -> new FieldDevelopmentPlanExternalDto(
            fdp.getKey().getFieldIdentifier(),
            fdp.getValue().getSubmissionDetail().getSubmission().getId(),
            fdp.getValue().getFdpDate(),
            fdp.getValue().getFdpTitle(),
            createFdpaDtos(fdpaMap.get(fdp.getKey()))
        ))
        .toList();
  }

  private List<FieldDevelopmentPlanAddendumExternalDto> createFdpaDtos(List<GeneralDetail> fdpas) {
    if (fdpas == null) {
      return Collections.emptyList();
    }
    return fdpas.stream()
        .map(fdpa ->
            new FieldDevelopmentPlanAddendumExternalDto(
                fdpa.getFdpDate(),
                fdpa.getFdpTitle()
            )
        )
        .sorted(Comparator.comparing(FieldDevelopmentPlanAddendumExternalDto::date))
        .toList();
  }

}
