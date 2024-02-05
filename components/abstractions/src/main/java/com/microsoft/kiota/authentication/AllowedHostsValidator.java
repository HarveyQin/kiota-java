package com.microsoft.kiota.authentication;

import jakarta.annotation.Nonnull;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/** Maintains a list of valid hosts and allows authentication providers to check whether a host is valid before authenticating a request */
public class AllowedHostsValidator {
    private HashSet<String> validHosts;

    /**
     * Creates a new AllowedHostsValidator.
     * @param allowedHosts The list of valid hosts.
     */
    public AllowedHostsValidator(@Nonnull final String... allowedHosts) {
        final HashSet<String> value = new HashSet<>(allowedHosts.length);
        for (final String val : allowedHosts) {
            value.add(val);
        }
        this.setAllowedHosts(value);
    }

    /**
     * Gets the allowed hosts. Read-only.
     * @return the allowed hosts.
     */
    @Nonnull public Set<String> getAllowedHosts() {
        return Collections.unmodifiableSet(this.validHosts);
    }

    /**
     * Sets the allowed hosts.
     * @param allowedHosts the allowed hosts.
     */
    public void setAllowedHosts(@Nonnull final Set<String> allowedHosts) {
        validHosts = new HashSet<String>();
        if (allowedHosts != null) {
            for (String host : allowedHosts) {
                if (host != null && !host.isEmpty()) {
                    host = host.trim().toLowerCase(Locale.ROOT);
                    if (host.startsWith("http://") || host.startsWith("https://")) {
                        throw new IllegalArgumentException(
                                "host should not contain http or https prefix");
                    }
                    validHosts.add(host);
                }
            }
        }
    }

    /**
     * Checks if the provided host is allowed.
     * @param uri the uri to check the host for.
     * @return true if the host is allowed, false otherwise.
     */
    public boolean isUrlHostValid(@Nonnull final URI uri) {
        return validHosts.isEmpty()
                || validHosts.contains(uri.getHost().trim().toLowerCase(Locale.ROOT));
    }
}
