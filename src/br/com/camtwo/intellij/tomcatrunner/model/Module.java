package br.com.camtwo.intellij.tomcatrunner.model;

import com.google.common.base.Optional;

/**
 * POJO for tomcat module representation.
 *
 * @author Vitor Zachi Junior
 * @since 23/03/16.
 */
public class Module {

    private final String context, documentBase;
    private final boolean internal, reloadable;

    public Module(String context, String documentBase, boolean internal, boolean reloadable) {
        this.context = context;
        this.documentBase = documentBase;
        this.reloadable = reloadable;
        this.internal = internal;
    }

    public String getContext() {
        return context;
    }

    public String getContextNormalized() {
        String a = Optional.fromNullable(context).or("");
        if (a.startsWith("/")) {
            return a;
        } else {
            return "/" + a;
        }
    }

    public String getDocumentBase() {
        return documentBase;
    }

    public boolean isInternal() {
        return internal;
    }

    public boolean isReloadable() {
        return reloadable;
    }
}
