package me.mmmjjkx.objects;

import java.util.List;

public interface GBDCommand {
    List<String> names();
    void execute(String[] args);
    String getDescription();
}
