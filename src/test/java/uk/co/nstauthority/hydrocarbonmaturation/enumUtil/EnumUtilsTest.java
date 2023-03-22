package uk.co.nstauthority.hydrocarbonmaturation.enumUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.EnumUtils;

@ExtendWith(MockitoExtension.class)
public class EnumUtilsTest {

  @Test
  void getEnumNamesFromCollection() {
    var enumNames = EnumUtils.getEnumNames(Set.of(EnumUtilTestEnum.ENUM_ONE, EnumUtilTestEnum.ENUM_TWO));
    assertThat(enumNames).containsExactlyInAnyOrder(EnumUtilTestEnum.ENUM_ONE.name(), EnumUtilTestEnum.ENUM_TWO.name());
  }

  @Test
  void getEnumNamesFromClass() {
    var enumNames = EnumUtils.getEnumNames(EnumUtilTestEnum.class);
    assertThat(enumNames).containsExactlyInAnyOrder(
        EnumUtilTestEnum.ENUM_ONE.name(),
        EnumUtilTestEnum.ENUM_TWO.name(),
        EnumUtilTestEnum.ENUM_THREE.name()
    );
  }

  @Test
  void getEnumNamesFromClassAndString() {
    var enumValue = EnumUtils.getEnumValue(EnumUtilTestEnum.class, "ENUM_ONE");
    assertThat(enumValue).isEqualTo(EnumUtilTestEnum.ENUM_ONE);
  }

  @Test
  void getEnumNamesFromClassAndInvalidString() {
    var test = "TEST";
    IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
      EnumUtils.getEnumValue(EnumUtilTestEnum.class, test);
    });

    assertEquals(thrown.getMessage(),
        String.format("Enum value '%s' is not a valid value of enum '%s'", test, EnumUtilTestEnum.class.getName()));
  }

  enum EnumUtilTestEnum {
    ENUM_ONE,
    ENUM_TWO,
    ENUM_THREE
  }
}
