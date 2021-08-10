package de.dagere.peass.ci.rts;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.dagere.peass.ci.PeassProcessConfiguration;
import de.dagere.peass.dependency.ResultsFolders;
import de.dagere.peass.dependency.analysis.data.ChangedEntity;
import de.dagere.peass.dependency.analysis.data.TestCase;
import de.dagere.peass.dependency.analysis.data.TestSet;
import de.dagere.peass.dependency.persistence.Dependencies;
import de.dagere.peass.dependency.persistence.ExecutionData;
import de.dagere.peass.dependency.persistence.Version;
import de.dagere.peass.dependency.traces.coverage.CoverageSelectionInfo;
import de.dagere.peass.dependency.traces.coverage.CoverageSelectionVersion;
import de.dagere.peass.utils.Constants;
import hudson.model.Run;

public class RTSVisualizationCreator {

   private static final Logger LOG = LogManager.getLogger(RTSVisualizationCreator.class);

   private final ResultsFolders localWorkspace;
   private final PeassProcessConfiguration peassConfig;

   public RTSVisualizationCreator(final ResultsFolders localWorkspace, final PeassProcessConfiguration peassConfig) {
      this.localWorkspace = localWorkspace;
      this.peassConfig = peassConfig;
   }

   public void visualize(final Run<?, ?> run) {
      try {
         Map<String, List<String>> changesList = new LinkedHashMap<String, List<String>>();
         readStaticSelection(run, changesList);

         List<String> traceSelectedTests = readDynamicSelection(run);
         CoverageSelectionVersion coverageSelectedTests = readCoverageSelection(run);

         System.out.println("Selected: " + traceSelectedTests + " Coverage: " + coverageSelectedTests);

         RTSVisualizationAction rtsVisualizationAction = new RTSVisualizationAction(peassConfig.getDependencyConfig(), changesList, traceSelectedTests, coverageSelectedTests);
         run.addAction(rtsVisualizationAction);

         for (String traceSelectedTest : traceSelectedTests) {
            TestCase testcase = new TestCase(traceSelectedTest);
            File traceFolder = localWorkspace.getVersionDiffFolder(peassConfig.getMeasurementConfig().getVersion());
            File traceFile = new File(traceFolder, testcase.getShortClazz() + "#" + testcase.getMethod() + ".txt");
            System.out.println("Trace file: " + traceFile.getAbsolutePath());
            String traceSource = "";
            if (traceFile.exists()) {
               traceSource = FileUtils.readFileToString(traceFile, StandardCharsets.UTF_8);
            }

            RTSTraceAction traceAction = new RTSTraceAction(traceSelectedTest, traceSource);
            run.addAction(traceAction);
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   private List<String> readDynamicSelection(final Run<?, ?> run) throws IOException, JsonParseException, JsonMappingException {
      List<String> selectedTests = new LinkedList<>();
      File executionfile = localWorkspace.getExecutionFile();
      if (executionfile.exists()) {
         ExecutionData executions = Constants.OBJECTMAPPER.readValue(executionfile, ExecutionData.class);
         TestSet tests = executions.getVersions().get(peassConfig.getMeasurementConfig().getVersion());

         for (TestCase test : tests.getTests()) {
            selectedTests.add(test.getExecutable());
         }
      } else {
         LOG.info("File {} was not found, RTS execution info might be incomplete", executionfile.getAbsoluteFile());
      }
      return selectedTests;
   }

   private CoverageSelectionVersion readCoverageSelection(final Run<?, ?> run) throws IOException, JsonParseException, JsonMappingException {
      File coverageInfoFile = localWorkspace.getCoverageInfoFile();
      if (coverageInfoFile.exists()) {
         LOG.info("Reading {}", coverageInfoFile);
         CoverageSelectionInfo executions = Constants.OBJECTMAPPER.readValue(coverageInfoFile, CoverageSelectionInfo.class);
         CoverageSelectionVersion currentVersion = executions.getVersions().get(peassConfig.getMeasurementConfig().getVersion());
         return currentVersion;
      } else {
         LOG.info("File {} was not found, RTS coverage based selection info might be incomplete", coverageInfoFile.getAbsoluteFile());
      }
      return null;
   }

   private void readStaticSelection(final Run<?, ?> run, final Map<String, List<String>> changesList) throws IOException, JsonParseException, JsonMappingException {
      File dependencyfile = localWorkspace.getDependencyFile();
      if (dependencyfile.exists()) {
         Dependencies dependencies = Constants.OBJECTMAPPER.readValue(dependencyfile, Dependencies.class);
         Version version = dependencies.getVersions().get(peassConfig.getMeasurementConfig().getVersion());

         for (Map.Entry<ChangedEntity, TestSet> entry : version.getChangedClazzes().entrySet()) {
            List<String> tests = new LinkedList<>();
            for (TestCase test : entry.getValue().getTests()) {
               tests.add(test.getExecutable());
            }
            changesList.put(entry.getKey().toString(), tests);
         }
      } else {
         LOG.error("File {} was not found, RTS selection seems to not have worked at all");
      }
   }
}