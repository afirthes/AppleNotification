package com.example.applenotification;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import com.eatthepath.pushy.apns.util.ApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.util.concurrent.Future;

@SpringBootApplication
public class AppleNotificationApplication {

    @Value("${teamId}")
    private String teamId;

    @Value("${keyPassword}")
    private String keyPassword;

    @Value("${p8FilePath}")
    private String p8FilePath;

    @Value("${deviceToken}")
    private String deviceToken;

    @Value("${appBundleId}")
    private String appBundleId;


    public static void main(String[] args) {
        SpringApplication.run(AppleNotificationApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {

            System.out.println("Sending...");

            final ApnsClient apnsClient = new ApnsClientBuilder()
                    .setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST)
                    .setSigningKey(ApnsSigningKey.loadFromPkcs8File(new File(p8FilePath),
                            teamId, keyPassword))
                    .build();


            final SimpleApnsPushNotification pushNotification;


//            final ApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();
//            payloadBuilder.setAlertTitle("Alert!")
//                    .setAlertBody("Alert body!")
//                    .set


            final String payload = """
                    {
                       "aps" : {
                          "alert" : {
                             "title" : "Notification Title",
                             "subtitle" : "Notification subtitle",
                             "body" : "This is the body of push notification :)"
                          },
                          "sound":"default",
                       },
                       "text":"some text",
                       "image":"https://picsum.photos/536/354"
                    }
                    """;
            final String token = TokenUtil.sanitizeTokenString(deviceToken);

            pushNotification = new SimpleApnsPushNotification(token, appBundleId, payload);

            var future = apnsClient.sendNotification(pushNotification);


            var response = future.get();

            System.out.println("End");

            apnsClient.close();


        };
    }

}
