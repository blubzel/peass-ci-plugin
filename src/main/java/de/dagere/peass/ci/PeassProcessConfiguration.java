package de.dagere.peass.ci;

import java.io.Serializable;

import de.dagere.peass.config.DependencyConfig;
import de.dagere.peass.config.MeasurementConfig;
import de.dagere.peass.execution.utils.EnvironmentVariables;

public class PeassProcessConfiguration implements Serializable {
   private static final long serialVersionUID = 5858433989302224348L;

   private final boolean updateSnapshotDependencies;
   private final MeasurementConfig measurementConfig;
   private final DependencyConfig dependencyConfig;
   private final EnvironmentVariables envVars;


   private final boolean displayRTSLogs;
   private final boolean displayLogs;
   private final boolean displayRCALogs;

   public PeassProcessConfiguration(final boolean updateSnapshotDependencies, final MeasurementConfig measurementConfig, final DependencyConfig dependencyConfig, final EnvironmentVariables envVars,
         final boolean displayRTSLogs, final boolean displayLogs, final boolean displayRCALogs) {
      this.updateSnapshotDependencies = updateSnapshotDependencies;
      this.measurementConfig = measurementConfig;
      this.dependencyConfig = dependencyConfig;
      this.envVars = envVars;
      this.displayRTSLogs = displayRTSLogs;
      this.displayLogs = displayLogs;
      this.displayRCALogs = displayRCALogs;
   }

   public boolean isUpdateSnapshotDependencies() {
      return updateSnapshotDependencies;
   }

   public MeasurementConfig getMeasurementConfig() {
      return measurementConfig;
   }

   public DependencyConfig getDependencyConfig() {
      return dependencyConfig;
   }

   public EnvironmentVariables getEnvVars() {
      return envVars;
   }

   public boolean isDisplayRTSLogs() {
      return displayRTSLogs;
   }
   
   public boolean isDisplayLogs() {
      return displayLogs;
   }

   public boolean isDisplayRCALogs() {
      return displayRCALogs;
   }
}
