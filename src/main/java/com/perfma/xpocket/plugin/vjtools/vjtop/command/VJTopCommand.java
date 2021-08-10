package com.perfma.xpocket.plugin.vjtools.vjtop.command;

import com.perfma.xlab.xpocket.spi.command.AbstractXPocketCommand;
import com.perfma.xlab.xpocket.spi.command.CommandInfo;
import com.perfma.xlab.xpocket.spi.process.XPocketProcess;
import com.perfma.xpocket.plugin.vjtools.vjtop.VJTopAdapter;

/**
 * @author xinxian
 * @create 2020-11-18 16:38
 **/
@CommandInfo(name = "vjtop", usage = "vjtop [options...] <PID>")
public class VJTopCommand extends AbstractXPocketCommand {

    @Override
    public void invoke(XPocketProcess process) throws Throwable {
        final String[] args = process.getArgs();
        VJTopAdapter.invoke(args);
        process.end();
    }

    @Override
    public String details(String cmd) {
        return " Option                        Description                            \n" +
                "------                        -----------                            \n" +
                "-?, -h, --help                shows this help                        \n" +
                "-c, --content                 output content:                        \n" +
                "                               all(default): jvm info and theads info\n" +
                "                               jvm: only jvm info                    \n" +
                "                               thread: only thread info              \n" +
                "-d, -i, --interval <Integer>  interval between each output iteration \n" +
                "                                (defaults to 10s)                    \n" +
                "-f, --filter                  Thread name filter ( no default)       \n" +
                "-j, --jmxurl                  give JMX url like 127.0.0.1:7001 when  \n" +
                "                                VM attach doesn't work               \n" +
                "-l, --limit <Integer>         Number of threads to display ( default \n" +
                "                                to 10 threads)                       \n" +
                "-m, --mode                    number of thread display mode:         \n" +
                "                               cpu(default): display thread cpu      \n" +
                "                                usage and sort by its delta cpu time \n" +
                "                               syscpu: display thread cpu usage and  \n" +
                "                                sort by delta syscpu time            \n" +
                "                               totalcpu: display thread cpu usage    \n" +
                "                                and sort by total cpu time           \n" +
                "                               totalsyscpu: display thread cpu usage \n" +
                "                                and sort by total syscpu time        \n" +
                "                               memory: display thread memory         \n" +
                "                                allocated and sort by delta          \n" +
                "                               totalmemory: display thread memory    \n" +
                "                                allocated and sort by total          \n" +
                "-n, --iteration <Integer>     vjtop will exit after n output         \n" +
                "                                iterations  (defaults to unlimit)    \n" +
                "-o, --output                  output format:                         \n" +
                "                               console(default): console with        \n" +
                "                                warning and flush ansi code          \n" +
                "                               clean: console without warning and    \n" +
                "                                flush ansi code                      \n" +
                "                               text: plain text like /proc/status    \n" +
                "                                for 3rd tools                        \n" +
                "-w, --width <Integer>         Number of columns for the console      \n" +
                "                                display (defaults to 100)";
    }
}
