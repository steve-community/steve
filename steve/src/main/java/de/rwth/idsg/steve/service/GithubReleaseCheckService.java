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
package de.rwth.idsg.steve.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.zafarkhaja.semver.Version;
import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.web.dto.ReleaseReport;
import de.rwth.idsg.steve.web.dto.ReleaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 19.04.2016
 */
@Slf4j
public class GithubReleaseCheckService implements ReleaseCheckService {

    /**
     * If the Github api is slow to respond, we don't want the client of this class to wait forever (until the default
     * timeout kicks in).
     */
    private static final int API_TIMEOUT_IN_MILLIS = 4_000;

    private static final String API_URL = "https://api.github.com/repos/steve-community/steve/releases/latest";

    private static final String TAG_NAME_PREFIX = "steve-";

    private static final String FILE_SEPARATOR = File.separator;

    private final String steveVersion;
    private final RestTemplate restTemplate;

    public GithubReleaseCheckService(SteveProperties steveProperties) {
        this.steveVersion = steveProperties.getSteveVersion();
        this.restTemplate = createRestTemplate(createGitHubMapper(), "steve/" + steveProperties.getSteveVersion());
    }

    private static RestTemplate createRestTemplate(ObjectMapper mapper, String userAgent) {
        var timeout = Timeout.ofMilliseconds(API_TIMEOUT_IN_MILLIS);
        var requestConfig =
                RequestConfig.custom().setConnectionRequestTimeout(timeout).build();
        var httpClient =
                HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        var factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        var messageConverter = new MappingJackson2HttpMessageConverter(mapper);
        var restTemplate = new RestTemplate(List.of(messageConverter));
        restTemplate.setRequestFactory(factory);

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("User-Agent", userAgent);
            request.getHeaders().setAccept(List.of(MediaType.valueOf("application/vnd.github+json")));
            return execution.execute(request, body);
        });
        return restTemplate;
    }

    private static ObjectMapper createGitHubMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @Override
    public ReleaseReport check() {
        try {
            var response = restTemplate.getForObject(API_URL, ReleaseResponse.class);
            return getReport(response, steveVersion);
        } catch (RestClientException e) {
            // Fallback to "there is no new version atm".
            // Probably because Github did not respond within the timeout.
            return ReleaseReport.builder().moreRecent(false).build();
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private static ReleaseReport getReport(ReleaseResponse response, String buildVersion) {
        var githubVersion = extractVersion(response);

        var build = Version.parse(buildVersion);
        var github = Version.parse(githubVersion);

        var isGithubMoreRecent = github.isHigherThan(build);
        var downloadUrl = decideDownloadUrl(response);

        return ReleaseReport.builder()
                .moreRecent(isGithubMoreRecent)
                .githubVersion(githubVersion)
                .downloadUrl(downloadUrl)
                .htmlUrl(response.getHtmlUrl())
                .build();
    }

    private static String decideDownloadUrl(ReleaseResponse response) {
        if (isWindows()) {
            return response.getZipballUrl();
        } else {
            return response.getTarballUrl();
        }
    }

    private static String extractVersion(ReleaseResponse response) {
        return response.getTagName().replaceFirst(TAG_NAME_PREFIX, "");
    }

    /**
     * A little bit hacky, but good-enough solution. We only need to find out the family of the os (whether unix
     * or win). Therefore, we don't need full blown os detection, such as
     * <p>
     * - https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/SystemUtils.java
     * - http://stackoverflow.com/a/24861219
     * <p>
     * So, we base or decision on file.separator property. According to
     * https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html,
     * it is "/" on UNIX and "\" on Windows.
     */
    private static boolean isWindows() {
        return FILE_SEPARATOR.equals("\\");
    }
}
