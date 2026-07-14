package com.serviceportal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.serviceportal.model.ServiceOffer;
import com.serviceportal.model.User;
import com.serviceportal.repository.ServiceOfferRepository;
import com.serviceportal.repository.UserRepository;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner dataLoader(UserRepository userRepo, ServiceOfferRepository offerRepo, PasswordEncoder encoder) {
        return args -> {
            if (userRepo.count() == 0) {
                User admin = new User(null, "admin", encoder.encode("admin"), "ROLE_ADMIN,ROLE_USER");
                User user = new User(null, "user", encoder.encode("user"), "ROLE_USER");
                userRepo.save(admin);
                userRepo.save(user);
            }
            if (offerRepo.count() == 0) {
                offerRepo.save(new ServiceOffer(null, "Personalausweis beantragen", "Beantragen Sie einen neuen Personalausweis."));
                offerRepo.save(new ServiceOffer(null, "KFZ-Anmeldung", "Fahrzeug anmelden oder ummelden."));
            }
        };
    }
}
