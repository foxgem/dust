package top.dteam.dust.commands;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.nio.file.Paths;

@CommandLine.Command(name = "deploy", mixinStandardHelpOptions = true,
        description = "Deploy artifacts in a dust project.")
public class Deployment implements Runnable {

    @Option(names = {"--datasource"}, defaultValue = "development", description = "datasource for deployment")
    String datasource;

    @Override
    public void run() {
        Path projectPath = Paths.get(".").toAbsolutePath().normalize();
        try (ProjectConnection connection = GradleConnector.newConnector().forProjectDirectory(projectPath.toFile()).connect()) {
            connection.newBuild().forTasks("deploy").setJvmArguments("-Dexec.args=" + datasource)
                    .setStandardOutput(System.out)
                    .setStandardError(System.err)
                    .run();
        }
    }
}
