package top.dteam.dust.commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Command(name = "create", aliases = "init", mixinStandardHelpOptions = true,
        description = "Create a dust project.")
public class Creation implements Runnable {

    @Parameters(index = "0", defaultValue = ".", description = "The project path, default value is current path.")
    private String path;

    @Override
    public void run() {
        Path projectPath = Paths.get(path).toAbsolutePath().normalize();

        if (projectPath.toFile().isFile()) {
            System.err.println("path must be a directory, please try again.");
            return;
        }

        try {
            copyFolder(projectPath);
            System.out.println("A dust project is created.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyFolder(Path dest) throws IOException, URISyntaxException {
        Path src = FileSystems.newFileSystem(URI.create("jar:" + getClass()
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation())
                , new HashMap<>()).getPath("/template");
        Files.walk(src).forEach(source -> copy(source, dest.resolve(src.relativize(source).toString())));
    }

    private void copy(Path source, Path dest) {
        try {
            Files.copy(source, dest, REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
