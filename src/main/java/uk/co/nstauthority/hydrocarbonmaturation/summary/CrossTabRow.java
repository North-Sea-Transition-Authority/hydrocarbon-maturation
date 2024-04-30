package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.List;

public interface CrossTabRow {
  String getKey();

  List<String> getValues();
}