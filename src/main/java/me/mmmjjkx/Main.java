package me.mmmjjkx;

import lombok.Getter;
import me.mmmjjkx.commands.DownloadAll;
import me.mmmjjkx.commands.DownloadExcept;
import me.mmmjjkx.commands.DownloadSpecific;
import me.mmmjjkx.commands.Help;
import me.mmmjjkx.objects.GBDCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static File CURRENT;
    public static Logger LOGGER;
    @Getter
    private static List<GBDCommand> commands;

    public static void main(String[] args) {
        commands = new ArrayList<>();
        CURRENT = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
        LOGGER = LoggerFactory.getLogger(Main.class);

        setupCommands();

        LOGGER.info("""
                欢迎使用GuizhanBuildDownloader by lijinhong11(mmmjjkx)
                请在下面输入指令来执行对应操作，
                输入help获取帮助
                """);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            String[] split = input.split(" ");
            GBDCommand cmd = getCmd(split[0]);
            if (!input.isBlank()) {
                if (cmd == null) {
                    LOGGER.error("此指令不存在");
                } else {
                    split = Arrays.copyOfRange(split, 1, split.length);
                    cmd.execute(split);
                }
            }
        }
    }

    private static void setupCommands() {
        commands.add(new Help());
        commands.add(new DownloadAll());
        commands.add(new DownloadExcept());
        commands.add(new DownloadSpecific());

        //TODO commands.add(new GetBuildInfo());
    }

    private static GBDCommand getCmd(String name) {
        for (GBDCommand cmd : commands) {
            if (cmd.names().contains(name.toLowerCase())) {
                return cmd;
            }
        }
        return null;
    }
}
