package uk.co.nstauthority.hydrocarbonmaturation.file.virus;

import java.io.File;

public interface VirusCheckService {

  boolean hasVirus(File file);

}