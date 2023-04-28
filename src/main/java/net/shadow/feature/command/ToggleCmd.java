package net.shadow.feature.command;

import net.shadow.feature.ModuleRegistry;
import net.shadow.feature.base.Command;
import java.util.List;
import net.shadow.feature.base.Module;
import net.shadow.utils.ChatUtils;

public class ToggleCmd extends Command {
    
    @Override
    public List<String> completions(int index, String[] args){
        if(index == 0){
            return ModuleRegistry.getAll().stream().map(module -> module.getName()).toList();
        }
        return List.of(new String[0]);
    }

    public ToggleCmd() {
        super("toggle", "switches modules on and off");
    }

    @Override
    public void call(String[] args) {
        Module m = ModuleRegistry.find(args[0].toLowerCase());
        if (m == null) {
            ChatUtils.message("No Such Module!");
        } else m.toggle();
    }
}
