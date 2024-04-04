package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@ExtendWith(MockitoExtension.class)
class ProjectCapexServiceTest {
  @Mock
  private ProjectCapexRepository projectCapexRepository;
  @InjectMocks
  private ProjectCapexService projectCapexService;

  private Project project;

  @Test
  void givenSaveNewProjectCapex_verifyCall() {
    var form = new ProjectCapexForm();
    form.setDateProvidedDay("3");
    form.setDateProvidedMonth("11");
    form.setDateProvidedYear("2002");
    form.setTotalCapex(DecimalInput.createDecimalInput(24));
    form.setSourceOfInformation(ProjectCapexSource.DRAFT_CSR);

    projectCapexService.saveNewProjectCapex(form, project);

    var captor = ArgumentCaptor.forClass(ProjectCapex.class);
    verify(projectCapexRepository).save(captor.capture());

    assertThat(captor.getValue())
        .extracting(
            ProjectCapex::getProject,
            ProjectCapex::getStatus,
            ProjectCapex::getDateProvided,
            ProjectCapex::getTotalCapex,
            ProjectCapex::getSourceOfInformation
        )
        .contains(
            project,
            ProjectCapexStatus.CREATED,
            LocalDate.of(2002, 11, 3),
            BigDecimal.valueOf(24),
            ProjectCapexSource.DRAFT_CSR
        );
  }

  @Test
  void getOrderedProjectCapexs_assertOrderedList() {
    final var FIXED_DATE = LocalDate.now();

    var firstCreatedCapex = new ProjectCapex();
    firstCreatedCapex.setStatus(ProjectCapexStatus.CREATED);
    firstCreatedCapex.setDateProvided(FIXED_DATE);

    var secondCreatedCapex = new ProjectCapex();
    secondCreatedCapex.setStatus(ProjectCapexStatus.CREATED);
    secondCreatedCapex.setDateProvided(FIXED_DATE.plusDays(1));

    var removedCapex = new ProjectCapex();
    removedCapex.setStatus(ProjectCapexStatus.REMOVED);
    removedCapex.setDateProvided(FIXED_DATE.plusMonths(1));

    var lastCreatedCapex = new ProjectCapex();
    lastCreatedCapex.setStatus(ProjectCapexStatus.CREATED);
    lastCreatedCapex.setDateProvided(FIXED_DATE.plusYears(1));

    when(projectCapexRepository.findAllByProject(project))
        .thenReturn(List.of(removedCapex, lastCreatedCapex, firstCreatedCapex, secondCreatedCapex));

    assertThat(projectCapexService.getOrderedCreatedProjectCapexs(project))
        .isEqualTo(List.of(firstCreatedCapex, secondCreatedCapex, lastCreatedCapex));
  }

  @Test
  void getProjectCapex_whenPresent() {
    final var PROJECT_CAPEX_ID = UUID.randomUUID();
    var projectCapex = new ProjectCapex(PROJECT_CAPEX_ID);
    when(projectCapexRepository.findById(PROJECT_CAPEX_ID))
        .thenReturn(Optional.of(projectCapex));

    assertThat(projectCapexService.getProjectCapex(PROJECT_CAPEX_ID))
        .get()
        .isEqualTo(projectCapex);
  }

  @Test
  void getProjectCapex_whenEmpty() {
    final var PROJECT_CAPEX_ID = UUID.randomUUID();
    when(projectCapexRepository.findById(PROJECT_CAPEX_ID))
        .thenReturn(Optional.empty());

    assertThat(projectCapexService.getProjectCapex(PROJECT_CAPEX_ID))
        .isEmpty();
  }

  @Test
  void getProjectCapexView_whenPresent() {
    final var PROJECT_CAPEX_ID = UUID.randomUUID();
    var projectCapex = new ProjectCapex(PROJECT_CAPEX_ID);
    projectCapex.setDateProvided(LocalDate.of(2002, 11, 3));
    projectCapex.setTotalCapex(BigDecimal.valueOf(24));
    projectCapex.setSourceOfInformation(ProjectCapexSource.DRAFT_CSR);

    when(projectCapexRepository.findById(PROJECT_CAPEX_ID))
        .thenReturn(Optional.of(projectCapex));

    assertThat(projectCapexService.getProjectCapexView(PROJECT_CAPEX_ID))
        .get()
        .usingRecursiveComparison()
        .isEqualTo(
            new ProjectCapexView("3 November 2002", "£24 million", ProjectCapexSource.DRAFT_CSR.getDisplayName())
        );
  }

  @Test
  void getProjectCapexView_whenEmpty() {
    final var PROJECT_CAPEX_ID = UUID.randomUUID();
    when(projectCapexRepository.findById(PROJECT_CAPEX_ID))
        .thenReturn(Optional.empty());

    assertThat(projectCapexService.getProjectCapexView(PROJECT_CAPEX_ID))
        .isEmpty();
  }

  @Test
  void removeProjectCapex() {
    var projectCapex = new ProjectCapex();
    projectCapex.setStatus(ProjectCapexStatus.CREATED);

    projectCapexService.removeProjectCapex(projectCapex);
    verify(projectCapexRepository).save(projectCapex);

    assertThat(projectCapex.getStatus()).isEqualTo(ProjectCapexStatus.REMOVED);
  }
}