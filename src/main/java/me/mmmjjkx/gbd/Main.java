package me.mmmjjkx.gbd;

import lombok.Getter;
import me.mmmjjkx.gbd.commands.DownloadAll;
import me.mmmjjkx.gbd.commands.DownloadExcept;
import me.mmmjjkx.gbd.commands.DownloadSpecific;
import me.mmmjjkx.gbd.commands.Help;
import me.mmmjjkx.gbd.objects.GBDCommand;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;
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

    public static void main(String[] args) throws ClassNotFoundException {
        commands = new ArrayList<>();
        CURRENT = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();

        preloadLog4jThings();

        LOGGER = LoggerFactory.getLogger(Main.class);

        setupCommands();

        LOGGER.info("""
                
                欢迎使用GuizhanBuildDownloader by lijinhong11(mmmjjkx)
                请在下面输入指令来执行对应操作, 输入help获取帮助
                """);

        Scanner scanner = new Scanner(System.in);
        System.out.print("> ");
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().replaceFirst("> ", "");
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
            System.out.print("> ");
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

    private static void preloadLog4jThings() {
        ConsoleAppender.class.getName();
        FileAppender.class.getName();
        PatternLayout.class.getName();
    }
}
