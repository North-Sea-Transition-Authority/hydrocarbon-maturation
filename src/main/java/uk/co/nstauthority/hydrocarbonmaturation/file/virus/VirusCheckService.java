package uk.co.nstauthority.hydrocarbonmaturation.file.virus;

import java.io.IOException;
import java.io.InputStream;

public interface VirusCheckService {

  boolean hasVirus(InputStream inputStream);

}