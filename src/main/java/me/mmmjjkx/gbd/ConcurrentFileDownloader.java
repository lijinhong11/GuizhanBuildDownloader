package me.mmmjjkx.gbd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ConcurrentFileDownloader {
    private static final int BUFFER_SIZE = 1024;

    private static final int TIMEOUT_MILLIS = 30 * 60 * 1000; // 超时30分钟

    public static void downloadFile(File folder, String fileUrl, int numThreads) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            String fileName = getFileName(connection);
            File file = new File(folder, fileName);

            if (file.exists()) {
                Main.LOGGER.info("文件已存在：" + file.getCanonicalPath());
                return;
            }

            System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(TIMEOUT_MILLIS));
            System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(TIMEOUT_MILLIS));

            int statusCode = connection.getResponseCode();

            if (statusCode != HttpURLConnection.HTTP_OK) {
                Main.LOGGER.error("文件不存在或无法访问："+fileUrl);
                return;
            }

            int fileLength = connection.getContentLength();
            int blockSize = fileLength / numThreads;

            Thread[] threads = new Thread[numThreads];
            for (int i = 0; i < numThreads; i++) {
                int startByte = i * blockSize;
                int endByte = (i == numThreads - 1) ? fileLength - 1 : (i + 1) * blockSize - 1;

                threads[i] = new DownloadThread(url, file, startByte, endByte);
                threads[i].start();
            }

            //?
            List<File> incompleteFiles = new ArrayList<>();

            for (Thread thread : threads) {
                thread.join();
                if (((DownloadThread) thread).hasException()) {
                    incompleteFiles.add(file);
                }
            }

            if (!incompleteFiles.isEmpty()) {
                Main.LOGGER.info("文件下载失败或有残缺：{}", file.getCanonicalPath());
            } else {
                Main.LOGGER.info("文件下载成功：{}", file.getCanonicalPath());
            }
        } catch (Exception e) {
            Main.LOGGER.info("文件下载失败：{}", e.getMessage());
        }
    }

    private static String getFileName(URLConnection connection) {
        String fileName;
        String contentDisposition = connection.getHeaderField("Content-Disposition");

        if (contentDisposition != null && contentDisposition.contains("filename=")) {
            fileName = contentDisposition.substring(contentDisposition.indexOf("filename=") + 9);
            if (fileName.startsWith("\"")) {
                fileName = fileName.substring(1, fileName.length() - 1);
            }
        } else {
            fileName = connection.getURL().getFile();
            fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        }

        return fileName;
    }

    private static class DownloadThread extends Thread {
        private static final int MAX_RETRIES = 3;
        private static final int RETRY_DELAY_MILLIS = 3000; // 3秒

        private final URL url;
        private final File file;
        private final int startByte;
        private final int endByte;

        private boolean hasException = false;

        public boolean hasException() {
            return hasException;
        }

        public DownloadThread(URL url, File file, int startByte, int endByte) {
            this.url = url;
            this.file = file;
            this.startByte = startByte;
            this.endByte = endByte;
        }

        @Override
        public void run() {
            int retries = 0;
            boolean success = false;

            while (retries < MAX_RETRIES && !success) {
                try {
                    URLConnection connection = url.openConnection();
                    connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
                    InputStream inputStream = connection.getInputStream();
                    hasException = false;
                    FileOutputStream outputStream = new FileOutputStream(file, true);

                    byte[] buffer = new byte[BUFFER_SIZE];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    inputStream.close();
                    outputStream.close();

                    success = true;
                } catch (Exception e) {
                    retries++;
                    hasException = true;
                    System.out.println("下载失败，尝试重新获取（尝试次数：" + retries + "）");
                    try {
                        Thread.sleep(RETRY_DELAY_MILLIS);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }
}
