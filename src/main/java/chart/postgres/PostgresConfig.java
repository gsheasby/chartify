package chart.postgres;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(as = ImmutablePostgresConfig.class)
@JsonSerialize(as = ImmutablePostgresConfig.class)
@Value.Immutable
public abstract class PostgresConfig {
    public abstract String dbName();
    public abstract String user();
    public abstract String password();

    @Value.Default
    public int port() {
        return 5432;
    }
}
