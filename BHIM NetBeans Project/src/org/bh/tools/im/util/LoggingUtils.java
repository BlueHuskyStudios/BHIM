package org.bh.tools.im.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bh.app.im.Main;
import org.bh.tools.log.handlers.DialogHandler;



/**
 * LoggingUtils, made for BHIM, is copyright Blue Husky Programming ©2015 GPLv3 <hr/>
 *
 * @author Kyli of Blue Husky Programming
 * @version 1.0.0
 * - 2015-08-29 (1.0.0) - Kyli created LoggingUtils
 * @since 2015-08-29
 */
@SuppressWarnings("ClassWithMultipleLoggers")
public class LoggingUtils {
    public static final Logger FOREGROUND = Logger.getLogger(Main.APP_NAME + " - FOREGROUND");
    public static final Logger BACKGROUND = Logger.getLogger(Main.APP_NAME + " - BACKGROUND");

    static {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);

        FOREGROUND.addHandler(new DialogHandler(Level.INFO));
        FOREGROUND.addHandler(new ConsoleHandler());

        BACKGROUND.addHandler(new ConsoleHandler());
    }

    private LoggingUtils() {
    }
}
