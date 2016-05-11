package br.com.camtwo.intellij.tomcatrunner.model;

import br.com.camtwo.intellij.tomcatrunner.util.FileUtil;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaCommandLineState;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Command to run.
 *
 * @author Vitor Zachi Junior
 *         Inspired on jetty-runner by GuiKeller.
 * @see JavaCommandLineState
 */
public class TomcatRunnerCommandLine extends JavaCommandLineState {

    private static final TomcatRunnerViaServerConf TOMCAT_RUNNER_VIA_CONF = new TomcatRunnerViaServerConf();
    private static final TomcatConfigurer TOMCAT_CONFIGURER = new TomcatConfigurer();

    private ExecutionEnvironment environment;
    private TomcatRunnerConfiguration model;

    public TomcatRunnerCommandLine(@NotNull ExecutionEnvironment environment, TomcatRunnerConfiguration model) {
        super(environment);
        this.environment = environment;
        this.model = model;
    }

    @Override
    public JavaParameters createJavaParameters() throws ExecutionException {

        Path tomcatInstallationPath = Paths.get(model.getTomcatInstallation());

        TOMCAT_RUNNER_VIA_CONF.configure(tomcatInstallationPath, model.getTomcatModules());

        JavaParameters javaParams = new JavaParameters();
        // Use the same JDK as the project
        Project project = this.environment.getProject();
        ProjectRootManager manager = ProjectRootManager.getInstance(project);
        javaParams.setJdk(manager.getProjectSdk());

        // All modules to use the same things
//        Module[] modules = ModuleManager.getInstance(project).getModules();
//        if (modules != null && modules.length > 0) {
//            for (Module module : modules) {
//                javaParams.configureByModule(module, JavaParameters.JDK_AND_CLASSES);
//            }
//        }

        TOMCAT_CONFIGURER.configure(tomcatInstallationPath, javaParams);


        // VM Args
        String vmArgs = this.getVmArgs();
        if (vmArgs != null) {
            javaParams.getVMParametersList().addParametersString(vmArgs);
        }
        // Env Vars
        Map<String, String> environmentVariables = this.getEnvVars();
        if (!environmentVariables.isEmpty()) {
            // The below should work, but does not
            boolean passParentEnvironmentVariables = this.isPassParentEnvironmentVariables();
            javaParams.setupEnvs(environmentVariables, passParentEnvironmentVariables);
            // This is a workaround for the problem above..
            Set<String> keys = environmentVariables.keySet();
            for (String key : keys) {
                String value = environmentVariables.get(key);
                javaParams.getVMParametersList().addProperty(key, value);
            }
        }


        // All done, run it
        return javaParams;
    }

    /**
     * Retrieves the "VM Args" parameter
     *
     * @return String
     */
    protected String getVmArgs() {
        String vmArgs = model.getVmArgs();
        return vmArgs != null && !vmArgs.isEmpty() ? vmArgs : null;
    }

    /**
     * Retrieves the Env Vars
     *
     * @return Map<String, String>
     */
    protected Map<String, String> getEnvVars() {
        Map<String, String> environmentVariables = model.getEnvironmentVariables();
        if (environmentVariables != null && !environmentVariables.isEmpty()) {
            return model.getEnvironmentVariables();
        }
        return new HashMap<String, String>(0);
    }

    /**
     * Returns whether to pass through system / ide environment variables
     *
     * @return boolean
     */
    protected boolean isPassParentEnvironmentVariables() {
        return model.isPassParentEnvironmentVariables();
    }

    public void setModel(TomcatRunnerConfiguration model) {
        this.model = model;
    }

}
