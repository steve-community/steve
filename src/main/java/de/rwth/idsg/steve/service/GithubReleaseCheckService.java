/*
 * SteVe - SteckdosenVerwaltung - https://github.com/RWTH-i5-IDSG/steve
 * Copyright (C) 2013-2022 RWTH Aachen University - Information Systems - Intelligent Distributed Systems Group (IDSG).
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
import com.github.zafarkhaja.semver.Version;
import de.rwth.idsg.steve.SteveConfiguration;
import de.rwth.idsg.steve.web.dto.ReleaseReport;
import de.rwth.idsg.steve.web.dto.ReleaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
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

    private static final String API_URL = "https://api.github.com/repos/RWTH-i5-IDSG/steve/releases/latest";

    private static final String TAG_NAME_PREFIX = "steve-";

    private static final String FILE_SEPARATOR = File.separator;

    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(API_TIMEOUT_IN_MILLIS);
        factory.setConnectTimeout(API_TIMEOUT_IN_MILLIS);

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setPropertyNamingStrategy(new PropertyNamingStrategies.SnakeCaseStrategy());

        restTemplate = new RestTemplate(Collections.singletonList(new MappingJackson2HttpMessageConverter(mapper)));
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

    private static ReleaseReport getReport(ReleaseResponse response) {
        String githubVersion = extractVersion(response);

        Version build = Version.valueOf(SteveConfiguration.CONFIG.getSteveVersion());
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
