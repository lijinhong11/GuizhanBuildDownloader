package me.mmmjjkx.gbd.commands;

import me.mmmjjkx.gbd.Main;
import me.mmmjjkx.gbd.objects.GBDCommand;

import java.util.List;

public class Help implements GBDCommand {
    @Override
    public List<String> names() {
        return List.of("help");
    }

    @Override
    public void execute(String[] args) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");

        for (GBDCommand cmd : Main.getCommands()) {
            String names = cmd.names().toString();
            String parsed = names.substring(1, names.length() - 1).replace(" ", "");

            builder.append(parsed).append(" - ").append(cmd.getDescription());
            if (Main.getCommands().indexOf(cmd) != Main.getCommands().size() - 1) {
                builder.append("\n");
            }
        }
        Main.LOGGER.info(builder.toString());
    }

    @Override
    public String getDescription() {
        return "获取帮助";
    }
}
