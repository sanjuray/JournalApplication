package net.engineeringdigest.journalApp.cache;

import net.engineeringdigest.journalApp.entity.JournalConfigEntity;
import net.engineeringdigest.journalApp.repository.JournalConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    public enum KEYS{

        WEATHER_API("weather_api");

        private final String name;

        KEYS(String name) {
            this.name =name;
        }

        public String getName(){
            return name;
        }
    }

     public Map<String, String> APPCACHE = new HashMap<>();

    @Autowired
     private JournalConfigRepository journalConfigRepository;

    @PostConstruct
    public void init(){
        try {
            List<JournalConfigEntity> journalConfigEntityList = journalConfigRepository.findAll();
            for (JournalConfigEntity entity : journalConfigEntityList) {
                APPCACHE.put(entity.getKey(), entity.getValue());
            }
        }catch (Exception e){
            System.out.println("the error in getting config keys in: "+e);
        }
    }

}
