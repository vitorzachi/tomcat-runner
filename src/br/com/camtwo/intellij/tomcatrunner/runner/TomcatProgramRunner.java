package br.com.camtwo.intellij.tomcatrunner.runner;

import br.com.camtwo.intellij.tomcatrunner.conf.TomcatRunnerConfigurationType;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import br.com.camtwo.intellij.tomcatrunner.model.TomcatRunnerConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * Tomcat Program Runner
 * @see DefaultProgramRunner
 * @author Gui Keller
 */
public class TomcatProgramRunner extends DefaultProgramRunner {

    private static final String RUN = "Run";

    public TomcatProgramRunner(){
        super();
    }

    @NotNull
    public String getRunnerId() {
        return TomcatRunnerConfigurationType.RUNNER_ID;
    }

    @Override
    public boolean canRun(@NotNull String value, @NotNull RunProfile runProfile) {
        // It can only run TomcatRunnerConfigurations
        if(!(runProfile instanceof TomcatRunnerConfiguration)){
            return false;
        }
        // Values passed are: Run or Debug
        if(!RUN.equals(value)) {
            // Fallback on the TomcatProgramDebugger
            return false;
        }
        return true;
    }

    @Override
    protected RunContentDescriptor doExecute(@NotNull RunProfileState state,
                                             @NotNull ExecutionEnvironment env) throws ExecutionException {
        return super.doExecute(state, env);
    }
}
