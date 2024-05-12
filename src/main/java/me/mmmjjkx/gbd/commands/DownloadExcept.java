package me.mmmjjkx.gbd.commands;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.mmmjjkx.gbd.ConcurrentFileDownloader;
import me.mmmjjkx.gbd.Main;
import me.mmmjjkx.gbd.http.APIGetter;
import me.mmmjjkx.gbd.objects.GBDCommand;
import me.mmmjjkx.gbd.objects.ProjectInfoSmall;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadExcept implements GBDCommand {
    private final ExecutorService service = Executors.newCachedThreadPool();
    @Override
    public List<String> names() {
        return List.of("downloadexcept", "de");
    }

    @Override
    public void execute(String[] args) {
        String output = args.length == 0 ? "downloads" : args[0];
        File file = new File(Main.CURRENT, output);

        List<String> excepts;

        if (args.length >= 2) {
            String[] exceptArray = Arrays.copyOfRange(args, 1, args.length);
            excepts = Arrays.asList(exceptArray);
        } else {
            excepts = new ArrayList<>();
        }

        if (!file.exists()) {
            file.mkdirs();
        }

        Gson gson = new Gson();
        JsonObject main = JsonParser.parseString(APIGetter.get("projects", new BasicNameValuePair("per_page", "1000"))).getAsJsonObject();
        JsonArray projects = main.get("data").getAsJsonArray();

        List<ProjectInfoSmall> infos = new ArrayList<>();
        projects.forEach(e -> infos.add(gson.fromJson(e, ProjectInfoSmall.class)));

        List<ProjectInfoSmall> handled = infos.stream().filter(p -> !excepts.contains(p.getRepository())).toList();

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
        return "下载经过排除的项目";
    }
}
