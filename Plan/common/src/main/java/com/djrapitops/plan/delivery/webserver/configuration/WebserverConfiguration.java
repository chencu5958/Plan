/*
 *  This file is part of Player Analytics (Plan).
 *
 *  Plan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License v3 as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Plan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Plan. If not, see <https://www.gnu.org/licenses/>.
 */
package com.djrapitops.plan.delivery.webserver.configuration;

import com.djrapitops.plan.delivery.webserver.auth.AllowedIpList;
import com.djrapitops.plan.delivery.webserver.http.AccessAddressPolicy;
import com.djrapitops.plan.settings.config.PlanConfig;
import com.djrapitops.plan.settings.config.paths.WebserverSettings;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class WebserverConfiguration {

    private final PlanConfig config;
    private final AllowedIpList allowedIpList;
    private final WebserverLogMessages webserverLogMessages;

    @Inject
    public WebserverConfiguration(PlanConfig config, AllowedIpList allowedIpList, WebserverLogMessages webserverLogMessages) {
        this.config = config;
        this.allowedIpList = allowedIpList;
        this.webserverLogMessages = webserverLogMessages;
    }

    public WebserverLogMessages getWebserverLogMessages() {
        return webserverLogMessages;
    }

    public boolean isAuthenticationDisabled() {
        return config.isTrue(WebserverSettings.DISABLED_AUTHENTICATION);
    }

    public AccessAddressPolicy getAccessAddressPolicy() {
        return config.isTrue(WebserverSettings.IP_USE_X_FORWARDED_FOR)
                ? AccessAddressPolicy.X_FORWARDED_FOR_HEADER
                : AccessAddressPolicy.SOCKET_IP;
    }

    public AllowedIpList getAllowedIpList() {
        return allowedIpList;
    }

    public String getAllowedCorsOrigin() {
        return config.get(WebserverSettings.CORS_ALLOW_ORIGIN);
    }

    public int getPort() {
        return config.get(WebserverSettings.PORT);
    }

    public boolean isWebserverDisabled() {
        return config.isTrue(WebserverSettings.DISABLED);
    }
}
