package com.vn.test.bshoes.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Prints the ABSOLUTE path of the rolling log file once the app is up, so whoever
 * runs a test on a deploy machine can see in the console exactly which file to
 * collect and send back — instead of guessing where "logs/" resolved to.
 *
 * <p>The directory is {@code ./logs} by default (relative to the working directory
 * the app was started from); override it with {@code -DLOG_DIR=/abs/path} or a
 * {@code LOG_DIR} environment variable. See logback-spring.xml.
 *
 * <p>Note: this directory lives on the server filesystem only. It is intentionally
 * NOT mapped to any HTTP route (WebConfig serves /api/uploads/** and nothing else)
 * and there is no log-download endpoint, so the frontend cannot read these logs.
 */
@Component
public class LogLocationReporter {

    private static final Logger log = LoggerFactory.getLogger(LogLocationReporter.class);

    static String resolveLogDir() {
        String fromSysProp = System.getProperty("LOG_DIR");
        if (fromSysProp != null && !fromSysProp.isBlank()) return fromSysProp;
        String fromEnv = System.getenv("LOG_DIR");
        if (fromEnv != null && !fromEnv.isBlank()) return fromEnv;
        return "logs";
    }

    @EventListener(ApplicationReadyEvent.class)
    public void reportLogLocation() {
        File f = new File(resolveLogDir(), "bshoes.log");
        log.info("==============================================================");
        log.info("BShoes log file (collect this after a test run):");
        log.info("  {}", f.getAbsolutePath());
        log.info("Override the folder with -DLOG_DIR=<path> or LOG_DIR=<path>.");
        log.info("==============================================================");
    }
}
