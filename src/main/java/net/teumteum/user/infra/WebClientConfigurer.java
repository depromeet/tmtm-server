package net.teumteum.user.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfigurer {

    @Bean
    public WebClient gpt4WebClient() {
        return WebClient.create("https://api.openai.com");
    }

}
