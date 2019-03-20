import org.flywaydb.core.Flyway;

import java.util.Map;

public class MigrationApp {

    public static void main(String[] args) {

        Map dustConfig = DustConfiguration.load("dust-config.json");

        String key;
        if (args.length == 0) {
            key = "development";
        } else {
            key = args[0];
        }

        Map dataSource = (Map) dustConfig.get(key);
        if (dataSource == null) {
            System.err.println("*********************");
            System.err.println("Unknow datasource: " + key);
            System.err.println("*********************");
        } else {
            System.out.println("*********************");
            System.out.println("Using datasource: " + key);
            System.out.println("jdbc url        : " + dataSource.get("url"));
            System.out.println("user            : " + dataSource.get("user"));
            System.out.println("password        : ***");
            System.out.println("*********************");

            Flyway flyway = Flyway.configure()
                    .dataSource((String) dataSource.get("url")
                            , (String) dataSource.get("user")
                            , (String) dataSource.get("password"))
                    .locations(".")
                    .baselineVersion("0")
                    .baselineOnMigrate(true)
                    .load();
            flyway.migrate();
        }
    }

}
