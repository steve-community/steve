/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2025 SteVe Community Team
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth.idsg.steve.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.service.DummyReleaseCheckService;
import de.rwth.idsg.steve.service.GithubReleaseCheckService;
import de.rwth.idsg.steve.service.ReleaseCheckService;
import de.rwth.idsg.steve.utils.InternetChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

/**
 * Configuration and beans of Spring Framework.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 15.08.2014
 */
@Slf4j
@Configuration
@EnableWebMvc
@EnableScheduling
@ComponentScan("de.rwth.idsg.steve")
public class BeanConfiguration implements WebMvcConfigurer {

    @Bean(destroyMethod = "close")
    public DelegatingTaskScheduler asyncTaskScheduler() {
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("SteVe-TaskScheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.initialize();

        return new DelegatingTaskScheduler(scheduler);
    }

    @Bean(destroyMethod = "close")
    public DelegatingTaskExecutor asyncTaskExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setThreadNamePrefix("SteVe-TaskExecutor-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        return new DelegatingTaskExecutor(executor);
    }

    /**
     * There might be instances deployed in a local/closed network with no internet connection. In such situations,
     * it is unnecessary to try to access Github every time, even though the request will time out and result
     * report will be correct (that there is no new version). With DummyReleaseCheckService we bypass the intermediate
     * steps and return a "no new version" report immediately.
     */
    @Bean
    public ReleaseCheckService releaseCheckService(SteveConfiguration config) {
        if (InternetChecker.isInternetAvailable(config.getSteveCompositeVersion())) {
            return new GithubReleaseCheckService(config);
        } else {
            return new DummyReleaseCheckService();
        }
    }

    // -------------------------------------------------------------------------
    // API config
    // -------------------------------------------------------------------------

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (var converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter conv) {
                ObjectMapper objectMapper = conv.getObjectMapper();
                objectMapper.findAndRegisterModules();
                // if the client sends unknown props, just ignore them instead of failing
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                // default is true
                objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                break;
            }
        }
    }

    /**
     * Find the ObjectMapper used in MappingJackson2HttpMessageConverter and initialized by Spring automatically.
     * MappingJackson2HttpMessageConverter is not a Bean. It is created in {@link WebMvcConfigurationSupport#addDefaultHttpMessageConverters(List)}.
     * Therefore, we have to access it via proxies that reference it. RequestMappingHandlerAdapter is a Bean, created in
     * {@link WebMvcConfigurationSupport#requestMappingHandlerAdapter(ContentNegotiationManager, FormattingConversionService, org.springframework.validation.Validator)}.
     */
    @Primary
    @Bean
    public ObjectMapper jacksonObjectMapper(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        return requestMappingHandlerAdapter.getMessageConverters().stream()
                .filter(MappingJackson2HttpMessageConverter.class::isInstance)
                .findAny()
                .map(conv -> ((MappingJackson2HttpMessageConverter) conv).getObjectMapper())
                .orElseThrow(() ->
                        new IllegalStateException("There is no MappingJackson2HttpMessageConverter in Spring context"));
    }
}
