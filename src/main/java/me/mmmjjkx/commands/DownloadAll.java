package me.mmmjjkx.commands;

import com.google.gson.*;
import me.mmmjjkx.ConcurrentFileDownloader;
import me.mmmjjkx.Main;
import me.mmmjjkx.http.APIGetter;
import me.mmmjjkx.objects.GBDCommand;
import me.mmmjjkx.objects.ProjectInfoSmall;
import org.apache.hc.core5.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DownloadAll implements GBDCommand {
    private final ExecutorService service = Executors.newCachedThreadPool();
    @Override
    public List<String> names() {
        return List.of("downloadall", "da");
    }

    @Override
    public void execute(String[] args) {
        String output = args.length == 0 ? "downloads" : args[0];
        File file = new File(Main.CURRENT, output);

        if (!file.exists()) {
            file.mkdirs();
        }

        Gson gson = new Gson();
        JsonObject main = JsonParser.parseString(APIGetter.get("projects", new BasicNameValuePair("per_page", "1000"))).getAsJsonObject();
        JsonArray projects = main.get("data").getAsJsonArray();

        List<ProjectInfoSmall> infos = new ArrayList<>();
        projects.forEach(e -> infos.add(gson.fromJson(e, ProjectInfoSmall.class)));

        Main.LOGGER.info("开始下载...");

        for (ProjectInfoSmall info : infos) {
            Main.LOGGER.info("正在下载 " + info.getAuthor() + "/" + info.getRepository() + "/" + info.getBranch());
            String uri = APIGetter.uri + "download/"+info.getAuthor()+"/"+info.getRepository()+"/"+info.getBranch()+"/latest";
            service.submit(() -> ConcurrentFileDownloader.downloadFile(file, uri, 10));
        }

        service.shutdown();

        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            Main.LOGGER.info("已下载所有项目\n");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDescription() {
        return "下载全部项目";
    }
}
