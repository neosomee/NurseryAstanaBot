package com.example.nurseryAstana.configuration;

import com.pengrad.telegrambot.TelegramBot;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Route;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
@Slf4j
public class NurseryAstanaConfiguration {

    @Value("${telegram.bot.token}")
    private String token;

    @Value("${proxy.telegram.host}")
    private String proxyHost;

    @Value("${proxy.telegram.port}")
    private int proxyPort;

    @Value("${proxy.telegram.username}")
    private String proxyUser;

    @Value("${proxy.telegram.password}")
    private String proxyPass;

    @Bean
    public TelegramBot telegramBot() {

        Proxy proxy = new Proxy(
                Proxy.Type.HTTP,
                new InetSocketAddress(proxyHost, proxyPort)
        );

        Authenticator proxyAuthenticator = new Authenticator() {
            @Override
            public Request authenticate(Route route, okhttp3.Response response) {
                String credential = Credentials.basic(proxyUser, proxyPass);
                return response.request().newBuilder()
                        .header("Proxy-Authorization", credential)
                        .build();
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(proxy)
                .proxyAuthenticator(proxyAuthenticator)
                .build();

        TelegramBot telegramBot = new TelegramBot.Builder(token)
                .okHttpClient(client)
                .build();

        log.info("TelegramBot bean created with HTTP proxy");

        return telegramBot;
    }
}