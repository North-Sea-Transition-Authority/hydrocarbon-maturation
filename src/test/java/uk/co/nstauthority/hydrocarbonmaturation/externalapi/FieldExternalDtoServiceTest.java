package uk.co.nstauthority.hydrocarbonmaturation.externalapi;

import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;

@ExtendWith(MockitoExtension.class)
class FieldExternalDtoServiceTest {
  @Mock
  private FieldInternalDtoRepository fieldInternalDtoRepository;
  @InjectMocks
  private FieldExternalDtoService underTest;
  private final String FIELD_NAME = "name";
  private final List<Integer> IDS = List.of(1, 2, 3);

  @Test
  void getFieldExternalDtos_withNullFieldStatusIn() {
    var result = underTest.searchFields(IDS, FIELD_NAME, null);

    verify(fieldInternalDtoRepository).searchFields(FIELD_NAME, null, IDS);
  }

  @Test
  void getFieldExternalDtos_withNonNullFieldStatusIn() {
    var fieldStatusIn = List.of("STATUS200");

    var result = underTest.searchFields(IDS, FIELD_NAME, fieldStatusIn);

    verify(fieldInternalDtoRepository).searchFields(FIELD_NAME, List.of(FieldStatus.STATUS_200), IDS);
  }
}