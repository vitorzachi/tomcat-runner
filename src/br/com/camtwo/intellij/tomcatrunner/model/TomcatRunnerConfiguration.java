package br.com.camtwo.intellij.tomcatrunner.model;

import br.com.camtwo.intellij.tomcatrunner.ui.TomcatRunnerEditor;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.LocatableConfigurationBase;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RunProfileWithCompileBeforeLaunchOption;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizer;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Tomcat Runner Configuration - UI Model
 *
 * @author Vitor Zachi Junior
 * Inspired on jetty-runner by GuiKeller.
 * @see LocatableConfigurationBase
 */
public class TomcatRunnerConfiguration extends LocatableConfigurationBase implements RunProfileWithCompileBeforeLaunchOption {

    public static final String PREFIX = "TomcatRunnerV001-";
    public static final String TOMCAT_PATH_FIELD = PREFIX + "TomcatInstallation";
    public static final String PATHS_AND_LOCATIONS_FIELD = PREFIX + "PathsAndLocations";
    public static final String RUN_PORT_FIELD = PREFIX + "RunOnPort";
    public static final String VM_ARGS_FIELD = PREFIX + "VmArgs";
    public static final String PASS_PARENT_ENV_VARS_FIELD = PREFIX + "PassParentEnvVars";
    public static final String PATH_APP = "path";

    private String tomcatInstallation;
    private String runningOnPort;
    private String vmArgs;

    private Map<String, String> pathsAndLocations = new HashMap<String, String>(0);
    private Map<String, String> environmentVariables = new HashMap<String, String>(0);
    private boolean passParentEnvironmentVariables = false;

    private Project project;


    public TomcatRunnerConfiguration(Project project, ConfigurationFactory factory, String name) {
        super(project, factory, name);
        this.project = project;
    }

    @Override
    @NotNull
    public SettingsEditor<TomcatRunnerConfiguration> getConfigurationEditor() {
        // Instantiates a new UI (Conf Window)
        return new TomcatRunnerEditor(this);
    }

    @Override
    @Nullable
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        // Runner Model
        return new TomcatRunnerCommandLine(executionEnvironment, this);
    }

    // Persistence of values in disk

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        // Reads the conf file into this class
        this.tomcatInstallation = JDOMExternalizerUtil.readField(element, TOMCAT_PATH_FIELD);
        JDOMExternalizer.readMap(element, pathsAndLocations, PATHS_AND_LOCATIONS_FIELD, PATH_APP);
        this.runningOnPort = JDOMExternalizerUtil.readField(element, RUN_PORT_FIELD);
        this.vmArgs = JDOMExternalizerUtil.readField(element, VM_ARGS_FIELD);
        String passParentEnvironmentVariablesValue = JDOMExternalizerUtil.readField(element, PASS_PARENT_ENV_VARS_FIELD);
        this.passParentEnvironmentVariables = Boolean.valueOf(passParentEnvironmentVariablesValue);
        EnvironmentVariablesComponent.readExternal(element, this.environmentVariables);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        // Stores the values of this class into the parent
        JDOMExternalizerUtil.writeField(element, TOMCAT_PATH_FIELD, this.getTomcatInstallation());
        JDOMExternalizer.writeMap(element, pathsAndLocations, PATHS_AND_LOCATIONS_FIELD, PATH_APP);
        JDOMExternalizerUtil.writeField(element, RUN_PORT_FIELD, this.getRunningOnPort());
        JDOMExternalizerUtil.writeField(element, VM_ARGS_FIELD, this.getVmArgs());
        JDOMExternalizerUtil.writeField(element, PASS_PARENT_ENV_VARS_FIELD, "" + this.isPassParentEnvironmentVariables());
        if (this.environmentVariables != null && !this.environmentVariables.isEmpty()) {
            EnvironmentVariablesComponent.writeExternal(element, this.getEnvironmentVariables());
        }
    }

    @Override
    @NotNull
    public Module[] getModules() {
        ModuleManager moduleManager = ModuleManager.getInstance(this.project);
        return moduleManager.getModules();
    }

    // Getters and Setters


    public String getTomcatInstallation() {
        return tomcatInstallation;
    }

    public void setTomcatInstallation(String tomcatInstallation) {
        this.tomcatInstallation = tomcatInstallation;
    }

    public String getRunningOnPort() {
        return runningOnPort;
    }

    public void setRunningOnPort(String runningOnPort) {
        this.runningOnPort = runningOnPort;
    }


    public String getVmArgs() {
        return vmArgs;
    }

    public void setVmArgs(String vmArgs) {
        this.vmArgs = vmArgs;
    }

    public Map<String, String> getEnvironmentVariables() {
        return environmentVariables;
    }

    public void setEnvironmentVariables(Map<String, String> environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public boolean isPassParentEnvironmentVariables() {
        return passParentEnvironmentVariables;
    }

    public void setPassParentEnvironmentVariables(boolean passParentEnvironmentVariables) {
        this.passParentEnvironmentVariables = passParentEnvironmentVariables;
    }

    public Map<String, String> getPathsAndLocations() {
        return pathsAndLocations;
    }

    public void setPathsAndLocations(Map<String, String> pathsAndLocations) {
        this.pathsAndLocations = pathsAndLocations;
    }
}
