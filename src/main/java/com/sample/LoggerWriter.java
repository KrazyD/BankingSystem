package com.sample;

import com.sample.enums.LoggerTypes;
import org.apache.log4j.Logger;

public class LoggerWriter {

    private final static Logger logger = Logger.getLogger(LoggerWriter.class);

    public void createMessage(LoggerTypes type, String parameter) {
        switch (type) {
            case INFO:
                logger.info("WARNING: " + parameter);
                break;
            case WARN:
                logger.warn("WARNING: " + parameter);
                break;
            case ERROR:
                logger.error("ERROR: " + parameter);
                break;
            case FATAL:
                logger.fatal("FATAL: " + parameter);
                break;
        }
    }
}
