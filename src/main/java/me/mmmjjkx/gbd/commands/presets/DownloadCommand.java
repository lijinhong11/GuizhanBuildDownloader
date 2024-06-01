package me.mmmjjkx.gbd.commands.presets;

import me.mmmjjkx.gbd.Main;
import me.mmmjjkx.gbd.http.APIGetter;
import me.mmmjjkx.gbd.objects.GBDCommand;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public interface DownloadCommand extends GBDCommand {
    default void download(File dir, String url) {
        File fileName = new File(dir, APIGetter.getFileName(url));
        Main.LOGGER.info("开始下载 {}...", fileName.getName());
        try {
            fileName.createNewFile();
            URL fileUrl = new URL(url);
            InputStream inputStream = fileUrl.openStream();
            Path path = fileName.toPath();
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            Main.LOGGER.error("无法下载 {}", fileName.getName(), e);
        }
    }
}
