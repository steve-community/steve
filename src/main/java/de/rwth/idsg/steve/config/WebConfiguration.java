/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2023 SteVe Community Team
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static de.rwth.idsg.steve.web.GlobalControllerAdvice.EXCEPTION_MODEL_KEY;
import static de.rwth.idsg.steve.web.GlobalControllerAdvice.EXCEPTION_VIEW_NAME;

/**
 * Configuration and beans of Spring Framework.
 *
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @author Emeric Chardiny <emeric@ecy-conseil.com>
 * @since 15.08.2014
 */
@Slf4j
@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {
    // -------------------------------------------------------------------------
    // Web config
    // -------------------------------------------------------------------------

    private View resolveViewName(String viewName, Locale locale) {
        if (viewName.equals(EXCEPTION_VIEW_NAME)) {
            return jsonErrorView;
        }
        return jsonView;
    }

    /**
     * JSON view for nominal case in response to 'Accept: applicatiob/json' HTTP Header
     * GET AND POST requests are supported for many Controllers
     */
    private MappingJackson2JsonView jsonView = new MappingJackson2JsonView() {
        /**
         * In case of success, controllers redirect request to overview page, with a HTTP 302 redirect.
         * In case of error, we change this behavior:
         * - put the binding result containing errors back in model
         * - set response status to HTTP 400
         */
        @Override
        protected Map<String, Object> createMergedOutputModel(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
            // lookup any BindingResult entry with errors
            Set<Object> set =
                    model.entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().startsWith(BindingResult.MODEL_KEY_PREFIX))
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toSet());

            List<Object> errors = new ArrayList<>();
            for (Object o : set)
                errors.addAll(((BeanPropertyBindingResult) o).getAllErrors());

            // if no errors, return back to normal behavior
            if (errors.isEmpty())
                return super.createMergedOutputModel(model, request, response);

            // otherwise put errors into model and switch http response status
            Map<String, Object> result = new HashMap<>();
            result.put("errors", errors);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return result;
        }
    };
//    private MappingJackson2JsonView jsonView = new MappingJackson2JsonView();

    /**
     * JSON view in case of Exception
     */
    private MappingJackson2JsonView jsonErrorView = new MappingJackson2JsonView() {
        @Override
        protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            Map<String, Object> result = new HashMap<>();
            Exception e = (Exception) model.get(EXCEPTION_MODEL_KEY);
            result.put("exception", e.getClass().getCanonicalName());
            result.put("message", e.getMessage());

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            super.renderMergedOutputModel(result, request, response);
        }
    };

    /**
     * Resolver for either JSP views/templates or JSON response.
     */
    @Bean
    public ViewResolver contentNegotiatingViewResolver(
            ContentNegotiationManager manager) {

        List<ViewResolver> resolvers = new ArrayList<>();

        // Resolver for JSP views/templates when http request header is "Accept: application/x-www-form-urlencoded"
        // Controller classes process the requests and forward to JSP files for rendering.
        resolvers.add(new InternalResourceViewResolver("/WEB-INF/views/", ".jsp"));

        // Resolver for JSON body response when http request header is "Accept: application/json"
        resolvers.add(this::resolveViewName);

        ContentNegotiatingViewResolver resolver = new ContentNegotiatingViewResolver();
        resolver.setViewResolvers(resolvers);
        resolver.setContentNegotiationManager(manager);

        return resolver;
    }

    /**
     * Resource path for static content of the Web interface.
     */
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/manager/signin").setViewName("signin");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    // -------------------------------------------------------------------------
    // API config
    // -------------------------------------------------------------------------

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                MappingJackson2HttpMessageConverter conv = (MappingJackson2HttpMessageConverter) converter;
                ObjectMapper objectMapper = conv.getObjectMapper();
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
    @Bean
    public ObjectMapper objectMapper(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        return requestMappingHandlerAdapter.getMessageConverters().stream()
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .map(conv -> ((MappingJackson2HttpMessageConverter) conv).getObjectMapper())
                .orElseThrow(() -> new RuntimeException("There is no MappingJackson2HttpMessageConverter in Spring context"));
    }

}
