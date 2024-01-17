package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.List;
import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;

record ResourceRecordQueryResult(
                            UUID resourceId,
                            UUID submissionDetailId,
                            ResourceType resourceType,
                            String resourceName,
                            String centreOfInterestQuadBlock,
                            Integer organisationResponsibleId,
                            List<Integer> licencesTheResourceIsOn,
                            ResponsibleTeamType responsibleTeam,
                            Integer parentFieldId,
                            FieldStatus fieldStatus) {
  public ResourceRecordQueryResult(UUID resourceId,
                                   UUID submissionDetailId,
                                   ResourceType resourceType,
                                   String resourceName,
                                   String centreOfInterestQuadBlock,
                                   Integer organisationResponsibleId,
                                   List<Integer> licencesTheResourceIsOn,
                                   ResponsibleTeamType responsibleTeam) {
    this(resourceId,
        submissionDetailId,
        resourceType,
        resourceName,
        centreOfInterestQuadBlock,
        organisationResponsibleId,
        licencesTheResourceIsOn,
        responsibleTeam,
        null,
        null);
  }
}
