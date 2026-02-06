package uk.co.nstauthority.hydrocarbonmaturation.architecture;

import com.tngtech.archunit.core.domain.properties.HasName;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchTests;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;
import uk.co.fivium.digitalenummaterialisationlibrary.enummaterialisation.MaterialisableEnum;
import uk.co.nstauthority.hydrocarbonmaturation.HydrocarbonMaturationApplication;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityRule;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

@AnalyzeClasses(
    packages = "uk.co.nstauthority.hydrocarbonmaturation",
    importOptions = ImportOption.DoNotIncludeTests.class
)
class ArchitectureTest {

  @ArchTest
  final ArchTests securityRules = ArchTests.in(SecurityRule.class);

  @Test
  void displayableEnumsAreMaterialised() {
    var javaClasses = new ClassFileImporter()
        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
        .importPackagesOf(HydrocarbonMaturationApplication.class);

    ArchRuleDefinition.classes()
        .that().areEnums()
        .and().containAnyMethodsThat(
            HasName.Predicates.name("getDisplayName").or(HasName.Predicates.name("getDisplayOrder"))
        )
        .should().implement(MaterialisableEnum.class)
        .check(javaClasses);

    ArchRuleDefinition.theClass(Displayable.class)
        .should().beAssignableTo(MaterialisableEnum.class);
  }
}
