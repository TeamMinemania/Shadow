package net.shadow.feature.command;

import com.google.gson.JsonParser;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.scripting.Parser;

public class EvaluateCmd extends Command {
    public EvaluateCmd() {
        super("evaluate", "run some code quickly");
    }

    @Override
    public void call(String[] args) {
        Parser.compileChunkAndRun(new JsonParser().parse(String.join(" ", args)).getAsJsonObject());
    }
}
