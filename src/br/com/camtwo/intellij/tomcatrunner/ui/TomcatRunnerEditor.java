package br.com.camtwo.intellij.tomcatrunner.ui;

import br.com.camtwo.intellij.tomcatrunner.conf.TomcatRunnerConfigurationType;
import br.com.camtwo.intellij.tomcatrunner.model.TomcatRunnerConfiguration;
import com.google.common.base.Optional;
import com.intellij.compiler.impl.ModuleCompileScope;
import com.intellij.notification.*;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileTask;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.*;

/**
 * Controller - Tomcat Runner Editor
 *
 * @author Vitor Zachi Junior
 *         Inspired on jetty-runner by GuiKeller.
 * @see SettingsEditor
 */
public class TomcatRunnerEditor extends SettingsEditor<TomcatRunnerConfiguration> {

    private TomcatRunnerConfPanel configurationPanel;
    private String mainOutputDirectory = "";

    public TomcatRunnerEditor(TomcatRunnerConfiguration tomcatRunnerConfiguration) {
        this.configurationPanel = new TomcatRunnerConfPanel();
        super.resetFrom(tomcatRunnerConfiguration);
    }

    /**
     * This is invoked when the form is first loaded.
     * The values may be stored in disk, if not, set some defaults
     *
     * @param tomcatRunnerConfiguration tomcatRunnerConfiguration
     */
    @Override
    protected void resetEditorFrom(TomcatRunnerConfiguration tomcatRunnerConfiguration) {
        Project project = tomcatRunnerConfiguration.getProject();

        // Tomcat installation
        String tomcatInstallation = tomcatRunnerConfiguration.getTomcatInstallation();
        if (tomcatInstallation != null && !"".equals(tomcatInstallation.trim())) {
            this.configurationPanel.getTomcatField().setText(tomcatInstallation);
        } else {
            String projectName = project.getName();
            this.configurationPanel.getTomcatField().setText("Specify path of Tomcat installation");
        }


        // modules
        Map<String, String> ctxs = Optional.fromNullable(tomcatRunnerConfiguration.getPathsAndLocations()).or(new HashMap<String, String>());

        ModuleTableModel model = new ModuleTableModel();
        for (Map.Entry<String, String> entry : ctxs.entrySet()) {
            model.addModule(new br.com.camtwo.intellij.tomcatrunner.model.Module(entry.getKey(), entry.getValue()));
        }
        this.configurationPanel.getContexts().setModel(model);

        // Env Vars (Optional)
        Map<String, String> environmentVariables = tomcatRunnerConfiguration.getEnvironmentVariables();
        if (environmentVariables != null && !environmentVariables.isEmpty()) {
            this.configurationPanel.getEnvironmentVariables().setEnvs(environmentVariables);
        }
        // Vm Args (Optional)
        this.configurationPanel.getVmArgsField().setText(tomcatRunnerConfiguration.getVmArgs());
    }

    /**
     * This is invoked when the user fills the form and pushes apply/ok
     *
     * @param tomcatRunnerConfiguration tomcatRunnerConfiguration
     * @throws ConfigurationException ex
     */
    @Override
    protected void applyEditorTo(TomcatRunnerConfiguration tomcatRunnerConfiguration) throws ConfigurationException {
        tomcatRunnerConfiguration.setTomcatInstallation(this.configurationPanel.getTomcatField().getText());
        tomcatRunnerConfiguration.setVmArgs(this.configurationPanel.getVmArgsField().getText());
        tomcatRunnerConfiguration.setPassParentEnvironmentVariables(this.configurationPanel.getEnvironmentVariables().isPassParentEnvs());

        // parse map of modules
        Map<String, String> ctxs = new HashMap<>();

        List<br.com.camtwo.intellij.tomcatrunner.model.Module> elements = ((ModuleTableModel) (this.configurationPanel.getContexts().getModel())).getModules();
        for (br.com.camtwo.intellij.tomcatrunner.model.Module element : elements) {
            ctxs.put(element.getContext().trim(), element.getDocumentBase().trim());
        }

        tomcatRunnerConfiguration.setPathsAndLocations(ctxs);


        // Deals with adding / removing env vars before saving to the conf file
        Map<String, String> envVars = this.configurationPanel.getEnvironmentVariables().getEnvs();
        addOrRemoveEnvVar(tomcatRunnerConfiguration.getEnvironmentVariables(), envVars);
        tomcatRunnerConfiguration.setEnvironmentVariables(envVars);
        try {
            // Not entirely sure if 'I have' to do this - the IntelliJ framework may do
            tomcatRunnerConfiguration.writeExternal(new Element(TomcatRunnerConfiguration.PREFIX + UUID.randomUUID().toString()));
        } catch (WriteExternalException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return this.configurationPanel.getMainPanel();
    }

    // Helpers

    /**
     * Retrieves the output directory for the main module
     *
     * @param project Project
     * @return String value
     */
    private String getMainOutputDirectory(final Project project) {
        // Preparing things up for a sneaky "CompileTask"
        final CompilerManager compilerManager = CompilerManager.getInstance(project);
        final Module[] modules = ModuleManager.getInstance(project).getModules();
        final ModuleCompileScope compileScope = new ModuleCompileScope(project, modules, false);
        final Module mainModule = modules[0];
        // Though a "CompileTask" I can get hold of the "CompileContext"
        CompileTask compileTask = new CompileTask() {
            public boolean execute(CompileContext compileContext) {
                // Through the "CompileContext" I can get the output directory of the main module
                VirtualFile mainOutputDirectory = compileContext.getModuleOutputDirectory(mainModule);
                if (mainOutputDirectory != null) {
                    String mainOutputDirectoryValue = mainOutputDirectory.getPresentableUrl();
                    TomcatRunnerEditor.this.mainOutputDirectory = mainOutputDirectoryValue;
                } else {
                    // Project hasn't been compiled yet, so there is no output directory
                    NotificationGroup notificationGroup = new NotificationGroup("IDEA Tomcat Runner", NotificationDisplayType.BALLOON, true);
                    Notification notification = notificationGroup.createNotification("Tomcat Runner - Couldn't determine the classes folder:<br>Please compile / make your project before creating the conf.", NotificationType.ERROR);
                    Notifications.Bus.notify(notification, project);
                }
                return true;
            }
        };
        // Executes the task (synchronously), which invokes that internal 'execute' method
        compilerManager.executeTask(compileTask, compileScope, TomcatRunnerConfigurationType.RUNNER_ID, null);
        return this.mainOutputDirectory;
    }


    /**
     * Adds / removes variables to the System Environment Variables
     *
     * @param currentVars Map<String,String>
     * @param newVars     Map<String,String>
     */
    private void addOrRemoveEnvVar(Map<String, String> currentVars, Map<String, String> newVars) {
        // Removes the current env vars
        if (currentVars != null && !currentVars.isEmpty()) {
            Set<String> keys = currentVars.keySet();
            for (String key : keys) {
                System.clearProperty(key);
            }
        }
        // Adds the new env vars
        if (newVars != null && !newVars.isEmpty()) {
            Set<String> keys = newVars.keySet();
            for (String key : keys) {
                String value = newVars.get(key);
                System.setProperty(key, value);
            }
        }
    }

    public void setConfigurationPanel(TomcatRunnerConfPanel configurationPanel) {
        this.configurationPanel = configurationPanel;
    }
}
