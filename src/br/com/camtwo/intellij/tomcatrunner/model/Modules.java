package br.com.camtwo.intellij.tomcatrunner.model;

import com.google.common.base.Optional;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;

/**
 * @author Vitor Zachi Junior
 * @since 10/05/16.
 */
public class Modules {

    private ArrayList<Module> modules = new ArrayList<>();

    public ArrayList<Module> getModules() {
        return modules;
    }

    public void setModules(ArrayList<Module> modules) {
        this.modules = modules;
    }

    public void add(Module module) {
        this.modules = Optional.fromNullable(modules).or(new ArrayList<Module>());
        this.modules.add(module);
    }

    public void remove(Module module) {
        this.modules = Optional.fromNullable(modules).or(new ArrayList<Module>());
        this.modules.remove(module);
    }

    public void remove(int index) {
        this.modules = Optional.fromNullable(modules).or(new ArrayList<Module>());
        this.modules.remove(index);
    }

    public int size() {
        this.modules = Optional.fromNullable(modules).or(new ArrayList<Module>());
        return modules.size();
    }

    public Module get(int row) {
        this.modules = Optional.fromNullable(modules).or(new ArrayList<Module>());
        return modules.get(row);
    }
}
