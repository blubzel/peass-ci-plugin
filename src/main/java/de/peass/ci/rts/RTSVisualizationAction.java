package de.peass.ci.rts;

import java.util.List;
import java.util.Map;

import de.dagere.peass.config.DependencyConfig;
import hudson.model.Run;
import jenkins.model.RunAction2;

public class RTSVisualizationAction implements RunAction2 {
   private transient Run<?, ?> run;

   private final DependencyConfig config;
   private final Map<String, List<String>> staticSelection;
   private final List<String> dynamicSelection;
   
   //TODO Display count of calls for each test
   private final List<String> coverageSelection;

   public RTSVisualizationAction(final DependencyConfig config, final Map<String, List<String>> staticSelection, final List<String> dynamicSelection, final List<String> coverageSelection) {
      this.config = config;
      this.staticSelection = staticSelection;
      this.dynamicSelection = dynamicSelection;
      this.coverageSelection = coverageSelection;
   }
   
   public DependencyConfig getConfig() {
      return config;
   }

   public Map<String, List<String>> getStaticSelection() {
      return staticSelection;
   }

   public List<String> getDynamicSelection() {
      return dynamicSelection;
   }

   public List<String> getCoveragebasedSelection() {
      return coverageSelection;
   }

   @Override
   public String getIconFileName() {
      return "/plugin/peass-ci/images/rts.jpg";
   }

   @Override
   public String getDisplayName() {
      return "Regression Test Results";
   }

   @Override
   public String getUrlName() {
      return "rtsResults";
   }

   @Override
   public void onAttached(final Run<?, ?> run) {
      this.run = run;
      
   }

   @Override
   public void onLoad(final Run<?, ?> run) {
      this.run = run;
   }
}