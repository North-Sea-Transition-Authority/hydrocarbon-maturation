package uk.co.nstauthority.hydrocarbonmaturation.enumUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;

@ExtendWith(MockitoExtension.class)
class DisplayableEnumOptionUtilTest {

  @Test
  void getDisplayableOptions_assertMapInRightOrder() {
    var returnedMap = DisplayableEnumOptionUtil.getDisplayableOptions(DisplayableTestEnum.class);

    assertThat(returnedMap.entrySet()).containsExactly(
        entry(DisplayableTestEnum.OPTION_ONE.name(), DisplayableTestEnum.OPTION_ONE.getDisplayName()),
        entry(DisplayableTestEnum.OPTION_TWO.name(), DisplayableTestEnum.OPTION_TWO.getDisplayName()),
        entry(DisplayableTestEnum.OPTION_THREE.name(), DisplayableTestEnum.OPTION_THREE.getDisplayName())
    );
  }

  @Test
  void getDisplayableOptionsWithDescriptions_assertMapInRightOrder() {
    var returnedMap = DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(DisplayableTestEnum.class);

    assertThat(returnedMap.entrySet()).containsExactly(
        entry(DisplayableTestEnum.OPTION_ONE.name(), "%s (%s)".formatted(DisplayableTestEnum.OPTION_ONE.getDescription(), DisplayableTestEnum.OPTION_ONE.getDisplayName())),
        entry(DisplayableTestEnum.OPTION_TWO.name(), "%s (%s)".formatted(DisplayableTestEnum.OPTION_TWO.getDescription(), DisplayableTestEnum.OPTION_TWO.getDisplayName())),
        entry(DisplayableTestEnum.OPTION_THREE.name(), DisplayableTestEnum.OPTION_THREE.getDisplayName())
    );
  }

  @Test
  void getDisplayableOptionOrDefault() {
    var option3 = DisplayableTestEnum.OPTION_THREE;
    var option2 = DisplayableTestEnum.OPTION_TWO;
    Displayable nullOption = null;

    assertThat(DisplayableEnumOptionUtil.getDisplayableOptionOrDefault(nullOption, "default text"))
        .isEqualTo("default text");
    assertThat(DisplayableEnumOptionUtil.getDisplayableOptionOrDefault(option2, "default text"))
        .isEqualTo("second text");
    assertThat(DisplayableEnumOptionUtil.getDisplayableOptionOrDefault(option3, "default text"))
        .isEqualTo("third one");
  }

  enum DisplayableTestEnum implements Displayable {
    OPTION_THREE(3, "third one", ""),
    OPTION_ONE(1, "text", "first description"),
    OPTION_TWO(2, "second text", "second descriptions");

    private final int displayOrder;
    private final String displayName;
    private final String description;

    DisplayableTestEnum(int displayOrder, String displayName, String description) {
      this.displayOrder = displayOrder;
      this.displayName = displayName;
      this.description = description;
    }

    @Override
    public String getDisplayName() {
      return displayName;
    }

    @Override
    public int getDisplayOrder() {
      return displayOrder;
    }

    @Override
    public String getEnumName() {
      return this.name();
    }

    @Override
    public String getDescription() {
      return description;
    }

  }
}