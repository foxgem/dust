package top.dteam.dust.commands;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import picocli.CommandLine;

import java.nio.file.Path;
import java.nio.file.Paths;

@CommandLine.Command(name = "test", mixinStandardHelpOptions = true,
        description = "Run a test for a dust project.")
public class Test implements Runnable {

    @Override
    public void run() {
        Path projectPath = Paths.get(".").toAbsolutePath().normalize();
        try (ProjectConnection connection = GradleConnector.newConnector().forProjectDirectory(projectPath.toFile()).connect()) {
            connection.newBuild().forTasks("test")
                    .setStandardOutput(System.out)
                    .setStandardError(System.err)
                    .run();
        }
    }

}
