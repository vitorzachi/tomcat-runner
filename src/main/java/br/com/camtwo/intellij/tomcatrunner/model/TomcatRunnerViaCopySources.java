package br.com.camtwo.intellij.tomcatrunner.model;

import br.com.camtwo.intellij.tomcatrunner.util.FileUtil;
import com.intellij.execution.ExecutionException;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Vitor Zachi Junior
 * @since 11/05/16.
 */
public class TomcatRunnerViaCopySources {

    public void configure(Path tomcatInstallationPath, Modules modules) throws ExecutionException {

        cleanWebappFolder(tomcatInstallationPath);

        copySources(tomcatInstallationPath, modules);

    }

    private void copySources(Path tomcatInstallationPath, Modules modules) throws ExecutionException {
        // copy webapp folders configured to webapps folder in tomcat installation
        Path webappFolder = tomcatInstallationPath.resolve("webapps");
        File webappFile = webappFolder.toFile();
        for (Module entry : modules.getModules()) {
            File appDeployTarget = new File(webappFile, "/".equals(entry.getContext()) ? "ROOT" : entry.getContext());
            File appSource = new File(entry.getDocumentBase());
            try {
                FileUtil.copyFolder(appSource, appDeployTarget);
            } catch (IOException e) {
                throw new ExecutionException(e);
            }
        }
    }

    private void cleanWebappFolder(Path tomcatInstallationPath) throws ExecutionException {
        // clean webapps folder in tomcat installation
        Path webappFolder = tomcatInstallationPath.resolve("webapps");
        try {
            if (Files.exists(webappFolder)) {
                DirectoryStream<Path> paths = Files.newDirectoryStream(webappFolder);
                for (Path path : paths) {
                    FileUtil.deleteFolderRecursive(path.toFile());
                }
            } else {
                Files.createDirectory(webappFolder);
            }
        } catch (IOException e) {
            throw new ExecutionException(e);
        }
    }

}
