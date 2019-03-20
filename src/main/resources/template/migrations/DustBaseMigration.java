import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Statement;

public abstract class DustBaseMigration extends BaseJavaMigration {

    protected abstract String[] files();

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement dml = context.getConnection().createStatement()) {
            String[] files = files();
            for (String file : files) {
                dml.execute(new String(Files.readAllBytes(Paths.get(file))));
            }
        }
    }
}
