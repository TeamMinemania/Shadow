package net.shadow.feature.command;

import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.scripting.Parser;

public class LoadCmd extends Command {
    public LoadCmd() {
        super("load", "load a script file");
    }

    @Override
    public void call(String[] args) {
        Parser.compile(args[0]);
    }
}
