package net.engineeringdigest.journalApp.scheduler;

import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.SentimentData;
import net.engineeringdigest.journalApp.entity.UserEntity;
import net.engineeringdigest.journalApp.enums.Sentiment;
import net.engineeringdigest.journalApp.repository.UserRepositoryImpl;
import net.engineeringdigest.journalApp.service.EmailService;
import net.engineeringdigest.journalApp.service.SentimentalAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private AppCache appCache;

    @Autowired
    private SentimentalAnalysisService sentimentalAnalysisService;

    @Autowired
    KafkaTemplate<String, SentimentData> kafkaTemplate;

    @Scheduled(cron = "0 0 9 * * SUN")
    public void getSentimentalUsersAndSendMails(){
        List<UserEntity> users = userRepository.getUserBySentimentalAnalysis();
        for(UserEntity user: users){
            List<JournalEntry> journalEntries = user.getJournalEntries();
//            List<String> filteredContent = journalEntries.stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x -> x.getContent()).collect(Collectors.toList());
//
//            String entry = String.join(" ", filteredContent);
//            int sentiment = sentimentalAnalysisService.getSentiment(entry);

            List<Sentiment> sentiments = journalEntries.stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x -> x.getSentiment()).collect(Collectors.toList());
            Map<Sentiment, Integer> sentimentMap = new HashMap<>();
            for(Sentiment sentiment: sentiments){
                if(sentiment != null){
                    sentimentMap.put(sentiment, sentimentMap.getOrDefault(sentiment, 0)+1);
                }
            }

            int maxCount = 0;
            Sentiment maxSentiment = null;

            for(Map.Entry<Sentiment, Integer> entry: sentimentMap.entrySet()){
                int count = entry.getValue();
                if(maxCount < count){
                    maxCount = count;
                    maxSentiment = entry.getKey();
                }
            }

            if(maxSentiment != null){
                SentimentData sentimentData = SentimentData.builder().email(user.getEmail()).sentiment("Sentiment for last week"+maxSentiment).build();
                try{
                    kafkaTemplate.send("weekly-sentiments", sentimentData.getEmail(), sentimentData);
                } catch (Exception e) {
                    emailService.sendEmail(user.getEmail(), "Sentiment for last week", "Wah, this is Sunday-funday and \n\n your score:"+maxSentiment.toString());
                }
            }

        }
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void refreshAppCache(){
        appCache.init();
    }

}
