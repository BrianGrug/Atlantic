package me.scifi.hcf.eventgame;

import me.scifi.hcf.HCF;
import me.scifi.hcf.managers.IManager;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Class that can handle schedules for game events.
 */
public class EventScheduler implements IManager {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy,MM,dd,hh,mm,a", Locale.ENGLISH);
    private static final String FILE_NAME = "event-schedules.txt";
    private static final long QUERY_DELAY = TimeUnit.SECONDS.toMillis(60L);

    private long lastQuery;

    private final Map<LocalDateTime, String> scheduleMap = new LinkedHashMap<>();
    private final HCF plugin;

    public EventScheduler(HCF plugin) {
        this.plugin = plugin;
        this.reloadSchedules();
    }

    private void reloadSchedules() {
        File file = new File(plugin.getDataFolder(), FILE_NAME);
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.startsWith("#")) {
                    continue;
                }

                String[] args = currentLine.split(":");
                if (args.length == 2) {
                    try {
                        this.scheduleMap.put(LocalDateTime.parse(args[0], DATE_TIME_FORMATTER), args[1]);
                    } catch (DateTimeParseException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Bukkit.getConsoleSender().sendMessage("Could not find file " + FILE_NAME + '.');
            boolean success = false;
            try {
                if (file.createNewFile()) {
                    success = true;
                    Bukkit.getConsoleSender().sendMessage("Created file " + FILE_NAME + '.');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!success)
                Bukkit.getConsoleSender().sendMessage("Could not create file " + FILE_NAME + '.');
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Map<LocalDateTime, String> getScheduleMap() {
        long millis = System.currentTimeMillis();
        if (millis - QUERY_DELAY > lastQuery) {
            this.reloadSchedules();
            this.lastQuery = millis;
        }

        return this.scheduleMap;
    }
}
