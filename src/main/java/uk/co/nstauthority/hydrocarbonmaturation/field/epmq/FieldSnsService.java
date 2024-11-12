package uk.co.nstauthority.hydrocarbonmaturation.field.epmq;

import java.time.Clock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.co.fivium.energyportalmessagequeue.sns.SnsService;
import uk.co.fivium.energyportalmessagequeue.sns.SnsTopicArn;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;
import uk.co.nstauthority.hydrocarbonmaturation.epmqmessage.FieldEpmqMessage;
import uk.co.nstauthority.hydrocarbonmaturation.epmqmessage.RemiEpmqTopics;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;

@Service
public class FieldSnsService {

  private final FieldStatusService fieldStatusService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final SnsService snsService;
  private final SnsTopicArn fieldSnsTopicArn;
  private final Clock clock;

  public FieldSnsService(
      FieldStatusService fieldStatusService,
      RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
      SnsService snsService,
      Clock clock
  ) {
    this.fieldStatusService = fieldStatusService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.snsService = snsService;
    this.fieldSnsTopicArn = snsService.getOrCreateTopic(RemiEpmqTopics.FIELDS.getName());
    this.clock = clock;
  }

  @Async
  public void publishFieldUpdatedSnsMessage(Field field) {
    var geographicalArea =
        regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(field.getResource())
        .map(RegulatorManagedInformation::getGeographicalArea).orElse(null);

    var medianLineFlag = field.getOnshoreOffshore().equals(OnshoreOffshore.OFFSHORE)
        ? !field.getFieldLocatedInUkcs()
        : !field.getFieldLocatedInEngland();

    var parentFieldId = field.getParentDeterminedField() != null
        ? field.getParentDeterminedField().getFieldIdentifier()
        : null;

    var fieldNumber = field.getFieldNumber() != null
        ? field.getFieldNumber()
        : field.getParentDeterminedField().getFieldNumber();


    var message = FieldEpmqMessage.builder(
        CorrelationIdUtil.getCorrelationIdFromMdc(),
        clock.instant()
      )
        .setFieldId(field.getFieldIdentifier())
        .setFieldName(field.getCorrectedLegalFieldName())
        .setFieldStatus(fieldStatusService.getMostRecentFieldStatus(field).map(FieldStatus::getStatusValue).orElse(null))
        .setSubtype(determinationStatusToSubType(field.getDeterminationStatus()))
        .setInfrastructureHost(field.getInfrastructureHost())
        .setOnshoreOffshore(onshoreOffshoreConversion(field.getOnshoreOffshore()))
        .setMedianLineFlag(medianLineFlag)
        .setParentFieldId(parentFieldId)
        .setGeographicalAreaMnemonic(geographicalAreaToMnemonic(geographicalArea))
        .setFieldNumber(fieldNumber)
        .build();

    snsService.publishMessage(fieldSnsTopicArn, message);
  }

  private String determinationStatusToSubType(DeterminationStatus determinationStatus) {
    return switch (determinationStatus) {
      case DETERMINED -> "D";
      case UNDETERMINED -> "U";
      case SUB_AREA_OF_DETERMINED_FIELD -> "S";
    };
  }

  private String onshoreOffshoreConversion(OnshoreOffshore onshoreOffshore) {
    return switch (onshoreOffshore) {
      case ONSHORE -> "L";
      case OFFSHORE -> "O";
    };
  }

  private String geographicalAreaToMnemonic(GeographicalArea geographicalArea) {
    if (geographicalArea == null) {
      return null;
    }

    return switch (geographicalArea) {
      case CENTRAL_NORTH_SEA -> "CNS";
      case EAST_IRISH_SEA -> "IS";
      case NORTHERN_NORTH_SEA -> "NNS";
      case SOUTHERN_NORTH_SEA -> "SNS";
      case WEST_OF_SHETLAND -> "WoS";
      case ONSHORE -> null;
    };
  }

}
