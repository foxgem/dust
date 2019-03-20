package top.dteam.dust;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import top.dteam.dust.commands.Creation;
import top.dteam.dust.commands.Deployment;
import top.dteam.dust.commands.Test;

@Command(description = "A tool suite for database development"
        , name = "dust", mixinStandardHelpOptions = true, version = "0.1"
        , subcommands = {Creation.class, Test.class, Deployment.class})
public class Cli implements Runnable {

    public static void main(String[] args) {
        CommandLine.run(new Cli(), args);
    }

    @Override
    public void run() {
        System.out.println(new CommandLine(this).getUsageMessage());
    }

}
