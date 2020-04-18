package br.com.camtwo.intellij.tomcatrunner.conf;

import br.com.camtwo.intellij.tomcatrunner.util.IconUtil;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Tomcat Runner Configuration Type
 *
 * @author Vitor Zachi Junior
 *         Inspired on jetty-runner by GuiKeller.
 * @see ConfigurationType
 */
public class TomcatRunnerConfigurationType implements ConfigurationType {

    public static final String TOMCAT_RUNNER = "Tomcat Runner";
    public static final String INTELLI_J_IDEA = "IntelliJ IDEA";
    public static final String RUNNER_ID = "TomcatRunner-By-Camtwo";

    public TomcatRunnerConfigurationType() {
        super();
    }

    @Override
    public String getDisplayName() {
        return TOMCAT_RUNNER;
    }

    @Override
    public String getConfigurationTypeDescription() {
        return new StringBuilder(INTELLI_J_IDEA).append(" ").append(TOMCAT_RUNNER).toString();
    }

    @Override
    public Icon getIcon() {
        return IconUtil.getInstance().getIcon();
    }

    @Override
    @NotNull
    public String getId() {
        return RUNNER_ID;
    }

    @Override
    public ConfigurationFactory[] getConfigurationFactories() {
        TomcatRunnerConfigurationFactory factory = new TomcatRunnerConfigurationFactory(this);
        return new ConfigurationFactory[]{factory};
    }
}
