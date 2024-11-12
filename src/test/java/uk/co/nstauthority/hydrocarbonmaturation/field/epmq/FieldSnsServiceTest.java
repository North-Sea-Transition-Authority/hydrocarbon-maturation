package uk.co.nstauthority.hydrocarbonmaturation.field.epmq;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalmessagequeue.sns.SnsService;
import uk.co.fivium.energyportalmessagequeue.sns.SnsTopicArn;
import uk.co.nstauthority.hydrocarbonmaturation.correlationid.CorrelationIdUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
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

@ExtendWith(MockitoExtension.class)
class FieldSnsServiceTest {

  private final SnsTopicArn snsTopicArn = new SnsTopicArn(RemiEpmqTopics.FIELDS.getName());

  @Mock
  private FieldStatusService fieldStatusService;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  private SnsService snsService;

  private Clock clock;

  private FieldSnsService fieldSnsService;

  @Captor
  private ArgumentCaptor<FieldEpmqMessage> captor;


  @BeforeEach
  void setUp() {
    when(snsService.getOrCreateTopic(RemiEpmqTopics.FIELDS.getName())).thenReturn(snsTopicArn);

    fieldSnsService = spy(new FieldSnsService(
      fieldStatusService,
      regulatorManagedInformationPersistenceService,
      snsService,
      clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
    ));
  }

  @Test
  void publishFieldUpdatedSnsMessage() {
    var field = createField();

    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setGeographicalArea(GeographicalArea.ONSHORE);

    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.of(FieldStatus.STATUS_000));
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(field.getResource()))
        .thenReturn(Optional.of(regulatorManagedInformation));

    var message = FieldEpmqMessage.builder(
            CorrelationIdUtil.getCorrelationIdFromMdc(),
            clock.instant()
        )
        .setFieldId(field.getFieldIdentifier())
        .setFieldName(field.getCorrectedLegalFieldName())
        .setFieldStatus(FieldStatus.STATUS_000.getStatusValue())
        .setSubtype("D")
        .setInfrastructureHost(field.getInfrastructureHost())
        .setOnshoreOffshore("L")
        .setMedianLineFlag(!field.getFieldLocatedInEngland())
        .setParentFieldId(null)
        .setGeographicalAreaMnemonic(null)
        .setFieldNumber(field.getFieldNumber())
        .build();

    fieldSnsService.publishFieldUpdatedSnsMessage(field);

    verify(snsService).publishMessage(eq(snsTopicArn), captor.capture());

    assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(message);
  }

  @Test
  void publishFieldUpdatedSnsMessage_subAreaField() {
    var parentField = createField();
    parentField.setFieldIdentifier(2);
    parentField.setFieldNumber(2);

    var field = createField();
    field.setFieldNumber(null);
    field.setParentDeterminedField(parentField);

    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setGeographicalArea(GeographicalArea.ONSHORE);

    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.of(FieldStatus.STATUS_000));
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(field.getResource()))
        .thenReturn(Optional.of(regulatorManagedInformation));

    var message = FieldEpmqMessage.builder(
            CorrelationIdUtil.getCorrelationIdFromMdc(),
            clock.instant()
        )
        .setFieldId(field.getFieldIdentifier())
        .setFieldName(field.getCorrectedLegalFieldName())
        .setFieldStatus(FieldStatus.STATUS_000.getStatusValue())
        .setSubtype("D")
        .setInfrastructureHost(field.getInfrastructureHost())
        .setOnshoreOffshore("L")
        .setMedianLineFlag(!field.getFieldLocatedInEngland())
        .setParentFieldId(parentField.getFieldIdentifier())
        .setGeographicalAreaMnemonic(null)
        .setFieldNumber(parentField.getFieldNumber())
        .build();

    fieldSnsService.publishFieldUpdatedSnsMessage(field);

    verify(snsService).publishMessage(eq(snsTopicArn), captor.capture());

    assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(message);
  }

  private Field createField() {
    var resource = new Resource();

    var field = new Field();
    field.setResource(resource);
    field.setFieldIdentifier(1);
    field.setCorrectedLegalFieldName("test");
    field.setDeterminationStatus(DeterminationStatus.DETERMINED);
    field.setInfrastructureHost(true);
    field.setOnshoreOffshore(OnshoreOffshore.ONSHORE);
    field.setFieldLocatedInUkcs(null);
    field.setFieldLocatedInEngland(true);
    field.setParentDeterminedField(null);
    field.setFieldNumber(1);

    return field;
  }

}
