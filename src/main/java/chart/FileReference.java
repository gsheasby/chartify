package chart;

import org.immutables.value.Value;

import java.nio.file.Path;

@Value.Immutable
public interface FileReference {
    Integer week();
    Path path();

    static FileReference of(Integer week, Path path) {
        return builder().week(week).path(path).build();
    }

    static Builder builder() {
        return new Builder();
    }

    class Builder extends ImmutableFileReference.Builder {}
}
