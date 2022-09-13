package uk.co.nstauthority.hydrocarbonmaturation.file.virus;

import java.io.File;
import java.io.IOException;

public class VirusCheckFailedException extends RuntimeException {
  public VirusCheckFailedException(File file, IOException e) {
    super(String.format("Failed to virus scan file %s", file.toString()), e);
  }
}
