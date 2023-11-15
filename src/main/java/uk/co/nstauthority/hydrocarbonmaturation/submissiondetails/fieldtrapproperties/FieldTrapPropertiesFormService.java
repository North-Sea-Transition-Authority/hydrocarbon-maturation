package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapType;

@Service
public class FieldTrapPropertiesFormService {

  private final TrapPropertiesRepository trapPropertiesRepository;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  public FieldTrapPropertiesFormService(TrapPropertiesRepository trapPropertiesRepository,
                                        ProbabilityDistributionFormService probabilityDistributionFormService,
                                        ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.trapPropertiesRepository = trapPropertiesRepository;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  public FieldTrapPropertiesForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new FieldTrapPropertiesForm();

    // set probability distributions
    form.setDepthToCrest(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_FIELD
    ));
    form.setDepthToSpillPoint(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_FIELD
    ));
    form.setProductiveArea(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_FIELD
    ));

    // set other fields
    var trapProperty = trapPropertiesRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new TrapProperty());
    var trapType = trapProperty.getTrapType();
    if (TrapType.OTHER.equals(trapType)) {
      form.setOtherTrapType(trapProperty.getOtherTrapType());
    }
    form.setTrapType(trapType);
    form.setClosureMechanisms(trapProperty.getClosureMechanisms());
    form.setVerticallySeparateHcPools(DecimalInput.createDecimalInput(trapProperty.getVerticallySeparateHcPools()));
    form.setStructuralCompartments(DecimalInput.createDecimalInput(trapProperty.getStructuralCompartments()));
    form.setOriginalHydrocarbonColumnHeight(DecimalInput.createDecimalInput(trapProperty.getOriginalHydrocarbonColumnHeight()));

    return form;
  }

  @Transactional
  public void saveFieldTrapProperties(FieldTrapPropertiesForm form, SubmissionDetail submissionDetail) {
    // save probability distributions
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_FIELD,
        form.getDepthToCrest(),
        submissionDetail
    );
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_FIELD,
        form.getDepthToSpillPoint(),
        submissionDetail
    );
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_FIELD,
        form.getProductiveArea(),
        submissionDetail
    );

    // save properties
    var trapProperty = trapPropertiesRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new TrapProperty());
    trapProperty.setSubmissionDetail(submissionDetail);

    var trapType = form.getTrapType();
    trapProperty.setTrapType(trapType);
    if (TrapType.OTHER.equals(trapType)) {
      trapProperty.setOtherTrapType(form.getOtherTrapType());
    } else {
      // clear any saved other trap property
      trapProperty.setOtherTrapType(null);
    }
    trapProperty.setClosureMechanisms(form.getClosureMechanisms());
    trapProperty.setVerticallySeparateHcPools(form.getVerticallySeparateHcPools().createBigDecimalOrNull());
    trapProperty.setStructuralCompartments(form.getStructuralCompartments().createBigDecimalOrNull());
    trapProperty.setOriginalHydrocarbonColumnHeight(form.getOriginalHydrocarbonColumnHeight().createBigDecimalOrNull());

    trapPropertiesRepository.save(trapProperty);
  }
}
