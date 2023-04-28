package net.shadow.feature.base;

import java.util.ArrayList;
import java.util.List;

public class Command {
    final String name;
    final String desc;

    public Command(String name, String de) {
        this.name = name;
        this.desc = de;
    }

    public void call(String[] args) {

    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    public List<String> completions(int index, String[] args){
        return new ArrayList<String>();
    }
}