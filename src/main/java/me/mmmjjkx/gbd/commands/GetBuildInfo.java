package me.mmmjjkx.gbd.commands;

import me.mmmjjkx.gbd.Main;
import me.mmmjjkx.gbd.objects.GBDCommand;

import java.util.List;

public class GetBuildInfo implements GBDCommand {
    @Override
    public List<String> names() {
        return List.of("getbuildinfo", "buildinfo", "gbi", "bi");
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            Main.LOGGER.error("请输入作者！");
            return;
        }

        if (args.length == 1) {
            Main.LOGGER.error("请输入存储库名称！");
            return;
        }

        if (args.length == 2) {
            Main.LOGGER.error("请输入分支名！");
            return;
        }

        String buildid = args.length == 3 ? "latest" : args[4];

    }

    @Override
    public String getDescription() {
        return "获取构建信息";
    }
}
