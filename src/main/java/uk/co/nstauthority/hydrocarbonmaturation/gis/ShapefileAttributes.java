package uk.co.nstauthority.hydrocarbonmaturation.gis;

import java.util.List;

record ShapefileAttributes(List<String> headers, List<List<String>> rows) {
}
