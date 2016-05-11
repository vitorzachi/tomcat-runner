package br.com.camtwo.intellij.tomcatrunner.model;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.JavaParameters;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Configurer for tomcat.
 *
 * @author Vitor Zachi Junior
 * @since 11/05/16.
 */
public class TomcatConfigurer {
    // Tomcat "Main Class" - the target (Tomcat Runner)
    private static final String MAIN_CLASS = "org.apache.catalina.startup.Bootstrap";

    public void configure(Path tomcatInstallation, JavaParameters javaParams) throws ExecutionException {
        // tomcat main class
        javaParams.setMainClass(MAIN_CLASS);

        // Working directories is the tomcat installation
        javaParams.setWorkingDirectory(tomcatInstallation.toString());

        // parameter of start
        javaParams.getProgramParametersList().add("start");

        addBinFolder(tomcatInstallation, javaParams);
        addLibFolder(tomcatInstallation, javaParams);
    }

    private void addBinFolder(Path tomcatInstallation, JavaParameters javaParams) throws ExecutionException {
        // Dynamically adds the tomcat jars to the classpath
        Path binFolder = tomcatInstallation.resolve("bin");
        if (!Files.exists(binFolder)) {
            throw new ExecutionException("The Tomcat installation configured doesn't contains a bin folder");
        }
        String[] jars = binFolder.toFile().list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        for (String jarFile : jars) {
            javaParams.getClassPath().add(binFolder.resolve(jarFile).toFile().getAbsolutePath());
        }
    }

    private void addLibFolder(Path tomcatInstallation, JavaParameters javaParams) throws ExecutionException {
        // add libs folder
        Path libFolder = tomcatInstallation.resolve("lib");
        if (!Files.exists(libFolder)) {
            throw new ExecutionException("The Tomcat installation configured doesn't contains a lib folder");
        }
        String[] jars = libFolder.toFile().list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });

        for (String jarFile : jars) {
            javaParams.getClassPath().add(libFolder.resolve(jarFile).toFile().getAbsolutePath());
        }
    }
}
