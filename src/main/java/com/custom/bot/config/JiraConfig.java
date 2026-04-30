package com.custom.bot.config;

import com.custom.bot.integration.jira.JiraApi;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class JiraConfig {

    @Value("${jira.host}")
    private String jiraHost;

    @Value("${jira.email}")
    private String jiraEmail;

    @Value("${jira.api-token}")
    private String jiraApiToken;

    @Bean
    public JiraApi jiraApi() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(
                        chain.request().newBuilder()
                                .header("Authorization", Credentials.basic(jiraEmail, jiraApiToken))
                                .header("Accept", "application/json")
                                .build()
                ))
                .build();

        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return new Retrofit.Builder()
                .baseUrl("https://" + jiraHost + "/rest/api/3/")
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build()
                .create(JiraApi.class);
    }
}
