package me.mmmjjkx.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.mmmjjkx.ConcurrentFileDownloader;
import me.mmmjjkx.Main;
import me.mmmjjkx.http.APIGetter;
import me.mmmjjkx.objects.GBDCommand;
import me.mmmjjkx.objects.ProjectInfoSmall;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadSpecific implements GBDCommand {
    private final ExecutorService service = Executors.newCachedThreadPool();
    @Override
    public List<String> names() {
        return List.of("downloadspecific", "ds");
    }

    @Override
    public void execute(String[] args) {
        String output = args.length == 0 ? "downloads" : args[0];
        File file = new File(Main.CURRENT, output);

        List<String> specific;

        if (args.length >= 2) {
            String[] specificArray = Arrays.copyOfRange(args, 1, args.length);
            specific = Arrays.asList(specificArray);
        } else {
            Main.LOGGER.info("请输入特定项目名称");
            return;
        }

        if (!file.exists()) {
            file.mkdirs();
        }

        Gson gson = new Gson();
        JsonObject main = JsonParser.parseString(APIGetter.get("projects", new BasicNameValuePair("per_page", "1000"))).getAsJsonObject();
        JsonArray projects = main.get("data").getAsJsonArray();

        List<ProjectInfoSmall> infos = new ArrayList<>();
        projects.forEach(e -> infos.add(gson.fromJson(e, ProjectInfoSmall.class)));

        List<ProjectInfoSmall> handled = infos.stream().filter(p -> specific.contains(p.getRepository())).toList();

        Main.LOGGER.info("开始下载...");

        for (ProjectInfoSmall info : handled) {
            Main.LOGGER.info("正在下载 " + info.getAuthor() + "/" + info.getRepository() + "/" + info.getBranch());
            String uri = APIGetter.uri + "download/"+info.getAuthor()+"/"+info.getRepository()+"/"+info.getBranch()+"/latest";
            service.submit(() -> ConcurrentFileDownloader.downloadFile(file, uri, 10));
        }

        service.shutdown();

        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            Main.LOGGER.info("已下载选定项目\n");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDescription() {
        return "下载特定项目";
    }
}
