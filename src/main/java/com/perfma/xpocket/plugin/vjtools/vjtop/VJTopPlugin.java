package com.perfma.xpocket.plugin.vjtools.vjtop;

import com.perfma.xlab.xpocket.spi.AbstractXPocketPlugin;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;

/**
 *
 * @author gongyu <yin.tong@perfma.com>
 */
public class VJTopPlugin extends AbstractXPocketPlugin {

    private static final String LOGO = " __     __      _   _____    ___    ____  \n" +
                                       " \\ \\   / /     | | |_   _|  / _ \\  |  _ \\ \n" +
                                       "  \\ \\ / /   _  | |   | |   | | | | | |_) |\n" +
                                       "   \\ V /   | |_| |   | |   | |_| | |  __/ \n" +
                                       "    \\_/     \\___/    |_|    \\___/  |_|    \n" +
                                       "                                          ";
    
    @Override
    public void printLogo(XPocketProcess process) {
        process.output(LOGO);
    }

}
