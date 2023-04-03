package uk.co.nstauthority.hydrocarbonmaturation.gis;

import com.esri.core.geometry.OperatorExportToGeoJson;
import com.esri.core.geometry.OperatorSimplify;
import com.esri.core.geometry.OperatorSimplifyOGC;
import com.esri.core.geometry.OperatorUnion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EsriGeometryOperatorProvider {
  @Bean
  OperatorExportToGeoJson operatorExportToGeoJson() {
    return OperatorExportToGeoJson.local();
  }

  @Bean
  OperatorUnion operatorUnion() {
    return OperatorUnion.local();
  }

  @Bean
  OperatorSimplifyOGC operatorSimplifyOgc() {
    return OperatorSimplifyOGC.local();
  }

  @Bean
  OperatorSimplify operatorSimplify() {
    return OperatorSimplify.local();
  }
}
