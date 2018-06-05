package chart;

import org.immutables.value.Value;

public interface SimpleChartEntry {
    int position();

    String title();

    String artist();

    @Value.Default
    String id();

    @Value.Default
    String href();

    @Value.Default
    String uri();
}
