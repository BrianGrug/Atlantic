package me.scifi.hcf.customtimers;

import lombok.Getter;
import lombok.Setter;
import me.scifi.hcf.managers.IManager;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class CustomTimerManager implements IManager {

    private List<CustomTimer> customTimers = new ArrayList<>();

    public void createTimer(CustomTimer timer){
        customTimers.add(timer);
    }

    public void deleteTimer(CustomTimer timer){
        customTimers.remove(timer);
    }

    public CustomTimer getCustomTimer(String name){
        return customTimers.stream().filter(timer -> timer.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
