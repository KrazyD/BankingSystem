package com.sample;

import com.sample.enums.LoggerTypes;
import org.apache.log4j.Logger;

public class LoggerWriter {

    private final static Logger logger = Logger.getLogger(LoggerWriter.class);

    public static void createMessage(LoggerTypes type, String parameter) {
        switch (type) {
            case INFO:
                logger.info(parameter);
                break;
            case WARN:
                logger.warn(parameter);
                break;
            case ERROR:
                logger.error(parameter);
                break;
            case FATAL:
                logger.fatal(parameter);
                break;
        }
    }
}
