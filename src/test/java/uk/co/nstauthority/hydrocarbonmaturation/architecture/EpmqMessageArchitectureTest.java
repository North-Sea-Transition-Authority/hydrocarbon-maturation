package uk.co.nstauthority.hydrocarbonmaturation.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import uk.co.fivium.energyportalmessagequeue.message.EpmqMessage;

@AnalyzeClasses(
    packages = "uk.co.nstauthority.hydrocarbonmaturation",
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class EpmqMessageArchitectureTest {

  @ArchTest
  final ArchRule epmqMessageDefaultConstructorRule = classes().that()
      .doNotHaveModifier(JavaModifier.ABSTRACT)
      .and()
      .areAssignableTo(EpmqMessage.class)
      .should(new ArchCondition<>("have a public constructor without parameters") {
        @Override
        public void check(JavaClass javaClass, ConditionEvents events) {
          boolean satisfied = javaClass.getConstructors().stream()
              .anyMatch(constructor -> constructor.getModifiers().contains(JavaModifier.PUBLIC)
                  && constructor.getParameters().isEmpty());

          String message = "%s %s a public constructor without parameters".formatted(
              javaClass.getDescription(),
              satisfied ? "has" : "does not have"
          );

          events.add(new SimpleConditionEvent(javaClass, satisfied, message));
        }
      });

}
