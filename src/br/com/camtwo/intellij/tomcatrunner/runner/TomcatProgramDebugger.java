package br.com.camtwo.intellij.tomcatrunner.runner;

import br.com.camtwo.intellij.tomcatrunner.conf.TomcatRunnerConfigurationType;
import br.com.camtwo.intellij.tomcatrunner.model.TomcatRunnerConfiguration;
import com.intellij.debugger.engine.DebuggerUtils;
import com.intellij.debugger.impl.GenericDebuggerRunner;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tomcat Program Debugger - Tomcat Runner on Debug mode or Normal mode
 *
 * @author Vitor Zachi Junior
 *         Inspired on jetty-runner by GuiKeller.
 * @see GenericDebuggerRunner
 */
public class TomcatProgramDebugger extends GenericDebuggerRunner {

    // These are JVM 6 onwards hence the JVM6 as min requirement
    private static final String XDEBUG = "-Xdebug";
    private static final String JDWP = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=";

    private static final String LOCALHOST = "127.0.0.1";


    public TomcatProgramDebugger() {
        super();
    }

    @Override
    @NotNull
    public String getRunnerId() {
        return TomcatRunnerConfigurationType.RUNNER_ID;
    }

    @Override
    public boolean canRun(@NotNull String value, @NotNull RunProfile runProfile) {
        // It can only run TomcatRunnerConfigurations
        if (!(runProfile instanceof TomcatRunnerConfiguration)) {
            return false;
        }
        return true;
    }

    @Override
    @Nullable
    protected RunContentDescriptor createContentDescriptor(RunProfileState state, ExecutionEnvironment env)
            throws ExecutionException {
        // Now we figure out if it the Debug button has been hit
        Executor executor = env.getExecutor();
        // If was the debug, then we do some extra magic
        if (executor instanceof DefaultDebugExecutor) {
            // Get hold of the JavaParameters
            JavaCommandLine javaCommandLine = (JavaCommandLine) state;
            JavaParameters javaParameters = javaCommandLine.getJavaParameters();
            // Making the assumption that it's JVM 7 onwards
            javaParameters.getVMParametersList().addParametersString(XDEBUG);
            // Debugger port
            String debuggerPort = DebuggerUtils.getInstance().findAvailableDebugAddress(true);
            String remotePort = JDWP + debuggerPort;
            javaParameters.getVMParametersList().addParametersString(remotePort);
            // Creating a 'Remote' configuration on the fly
            RemoteConnection connection = new RemoteConnection(true, LOCALHOST, debuggerPort, false);
            // Attaches the remote configuration to the VM and then starts it up
            return super.attachVirtualMachine(state, env, connection, true);
        } else {
            // If it was something else then we don't do anything special
            return super.createContentDescriptor(state, env);
        }
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull Project project, @NotNull RunProfileState state, RunContentDescriptor contentToReuse, @NotNull ExecutionEnvironment env) throws ExecutionException {
        return super.doExecute(project, state, contentToReuse, env);
    }

}
