package br.com.camtwo.intellij.tomcatrunner.conf;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import br.com.camtwo.intellij.tomcatrunner.model.TomcatRunnerConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * Tomcat Runner Configuration Factory
 *
 * @author Vitor Zachi Junior
 * Inspired on jetty-runner by GuiKeller.
 * @see ConfigurationFactory
 */
public class TomcatRunnerConfigurationFactory extends ConfigurationFactory {

    public TomcatRunnerConfigurationFactory(@NotNull ConfigurationType type) {
        super(type);
    }

    @NotNull
    @Override
    public String getId() {
        return "TomcatRunConfiguration";
    }

    @NotNull
    @Override
    public String getName() {
        return "Tomcat Run Configuration";
    }
    @Override
    public RunConfiguration createTemplateConfiguration(Project project) {
        return new TomcatRunnerConfiguration(project, this, TomcatRunnerConfigurationType.TOMCAT_RUNNER);
    }

}
