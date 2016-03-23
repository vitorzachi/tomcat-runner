package br.com.camtwo.intellij.tomcatrunner.model;

/**
 * POJO for tomcat module representation.
 *
 * @author Vitor Zachi Junior
 * @since 23/03/16.
 */
public class Module {

    private final String context, documentBase;

    public Module(String context, String documentBase) {
        this.context = context;
        this.documentBase = documentBase;
    }

    public String getContext() {
        return context;
    }

    public String getDocumentBase() {
        return documentBase;
    }
}
