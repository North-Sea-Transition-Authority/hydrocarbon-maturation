package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class PlannedUnsanctionedWellsService {
  private final PlannedUnsanctionedWellsRepository plannedUnsanctionedWellsRepository;

  @Autowired
  public PlannedUnsanctionedWellsService(PlannedUnsanctionedWellsRepository plannedUnsanctionedWellsRepository) {
    this.plannedUnsanctionedWellsRepository = plannedUnsanctionedWellsRepository;
  }

  public PlannedUnsanctionedWellsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var plannedUnsanctionedWells = plannedUnsanctionedWellsRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new PlannedUnsanctionedWells());

    var form = new PlannedUnsanctionedWellsForm();

    form.setTotalNumberOfPlannedUnsanctionedWells(
        DecimalInput.createDecimalInput(plannedUnsanctionedWells.getNumberOfPlannedUnsanctionedWells())
    );
    form.setTotalCapex(DecimalInput.createDecimalInput(
        plannedUnsanctionedWells.getTotalCapex())
    );

    return form;
  }

  @Transactional
  public void savePlannedUnsanctionedWells(PlannedUnsanctionedWellsForm form, SubmissionDetail submissionDetail) {
    var plannedUnsanctionedWells = plannedUnsanctionedWellsRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new PlannedUnsanctionedWells());

    plannedUnsanctionedWells.setSubmissionDetail(submissionDetail);
    plannedUnsanctionedWells.setNumberOfPlannedUnsanctionedWells(
        form.getTotalNumberOfPlannedUnsanctionedWells().createBigDecimalOrNull()
    );
    plannedUnsanctionedWells.setTotalCapex(
        form.getTotalCapex().createBigDecimalOrNull()
    );

    plannedUnsanctionedWellsRepository.save(plannedUnsanctionedWells);
  }
}
