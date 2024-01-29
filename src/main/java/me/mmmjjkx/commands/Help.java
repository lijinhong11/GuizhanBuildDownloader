package me.mmmjjkx.commands;

import me.mmmjjkx.Main;
import me.mmmjjkx.objects.GBDCommand;

import java.util.List;

public class Help implements GBDCommand {
    @Override
    public List<String> names() {
        return List.of("help");
    }

    @Override
    public void execute(String[] args) {
        for (GBDCommand cmd : Main.getCommands()) {
            String names = cmd.names().toString();
            String parsed = names.substring(1, names.length() - 1).replace(" ", "");
            Main.LOGGER.info(parsed + " " + cmd.getDescription());
        }
    }

    @Override
    public String getDescription() {
        return "获取帮助";
    }
}
