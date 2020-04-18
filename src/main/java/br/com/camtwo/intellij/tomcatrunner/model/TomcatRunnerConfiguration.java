package br.com.camtwo.intellij.tomcatrunner.model;

import br.com.camtwo.intellij.tomcatrunner.ui.TomcatRunnerEditor;
import com.google.gson.Gson;
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
 *         Inspired on jetty-runner by GuiKeller.
 * @see LocatableConfigurationBase
 */
public class TomcatRunnerConfiguration extends LocatableConfigurationBase implements RunProfileWithCompileBeforeLaunchOption {
    public static final String PREFIX = "TomcatRunnerV001-";
    public static final String TOMCAT_PATH_FIELD = PREFIX + "TomcatInstallation";
    public static final String MODULES_FIELD = PREFIX + "Modules";
    public static final String VM_ARGS_FIELD = PREFIX + "VmArgs";
    public static final String PASS_PARENT_ENV_VARS_FIELD = PREFIX + "PassParentEnvVars";
    private static final Gson GSON = new Gson();
    private String tomcatInstallation;
    private String runningOnPort;
    private String vmArgs;

    private Modules tomcatModules = new Modules();
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

        String locations = JDOMExternalizerUtil.readField(element, MODULES_FIELD);
        if (locations != null) {
            tomcatModules = (Modules) GSON.fromJson(locations, Modules.class);
        }
        this.tomcatInstallation = JDOMExternalizerUtil.readField(element, TOMCAT_PATH_FIELD);
        this.vmArgs = JDOMExternalizerUtil.readField(element, VM_ARGS_FIELD);
        String passParentEnvironmentVariablesValue = JDOMExternalizerUtil.readField(element, PASS_PARENT_ENV_VARS_FIELD);
        this.passParentEnvironmentVariables = Boolean.valueOf(passParentEnvironmentVariablesValue);
        EnvironmentVariablesComponent.readExternal(element, this.environmentVariables);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        // Stores the values of this class into the parent

        String modulesString = GSON.toJson(tomcatModules);
        JDOMExternalizerUtil.writeField(element, MODULES_FIELD, modulesString);

        JDOMExternalizerUtil.writeField(element, TOMCAT_PATH_FIELD, this.getTomcatInstallation());
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

    public Modules getTomcatModules() {
        return tomcatModules;
    }

    public void setTomcatModules(Modules tomcatModules) {
        this.tomcatModules = tomcatModules;
    }
}
