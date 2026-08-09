/*
 * SteVe - SteckdosenVerwaltung - https://github.com/steve-community/steve
 * Copyright (C) 2013-2026 SteVe Community Team
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

import com.github.zafarkhaja.semver.Version;
import de.rwth.idsg.steve.config.SteveProperties;
import de.rwth.idsg.steve.web.dto.ReleaseReport;
import de.rwth.idsg.steve.web.dto.ReleaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.Timeout;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.datatype.joda.JodaModule;

import java.io.File;
import java.util.Collections;

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

    private final SteveProperties steveProperties;
    private final RestTemplate restTemplate;

    public GithubReleaseCheckService(SteveProperties steveProperties) {
        this.steveProperties = steveProperties;

        var timeout = Timeout.ofMilliseconds(API_TIMEOUT_IN_MILLIS);

        var requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeout).build();

        var httpClient = HttpClientBuilder.create()
            .setDefaultRequestConfig(requestConfig)
            .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);

        var mapper = JsonMapper.builder()
            .addModule(new JodaModule())
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .build();

        restTemplate = new RestTemplate(Collections.singletonList(new JacksonJsonHttpMessageConverter(mapper)));
        restTemplate.setRequestFactory(factory);
    }

    @Override
    public ReleaseReport check() {
        try {
            ReleaseResponse response = restTemplate.getForObject(API_URL, ReleaseResponse.class);
            return getReport(response);

        } catch (RestClientException e) {
            // Fallback to "there is no new version atm".
            // Probably because Github did not respond within the timeout.
            return new ReleaseReport(false);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private ReleaseReport getReport(ReleaseResponse response) {
        String githubVersion = extractVersion(response);

        Version build = Version.valueOf(steveProperties.getVersion());
        Version github = Version.valueOf(githubVersion);

        boolean isGithubMoreRecent = github.greaterThan(build);
        String downloadUrl = decideDownloadUrl(response);

        ReleaseReport ur = new ReleaseReport(isGithubMoreRecent);
        ur.setGithubVersion(githubVersion);
        ur.setDownloadUrl(downloadUrl);
        ur.setHtmlUrl(response.getHtmlUrl());
        return ur;
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
     *
     * - https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/SystemUtils.java
     * - http://stackoverflow.com/a/24861219
     *
     * So, we base or decision on file.separator property. According to
     * https://docs.oracle.com/javase/tutorial/essential/environment/sysprop.html,
     * it is "/" on UNIX and "\" on Windows.
     */
    private static boolean isWindows() {
        return FILE_SEPARATOR.equals("\\");
    }
}
