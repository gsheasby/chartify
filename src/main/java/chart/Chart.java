package chart;

import java.util.List;

import org.joda.time.DateTime;

public interface Chart {
    int week();

    DateTime date();

    List<? extends ChartEntry> entries();

    List<? extends ChartEntry> dropouts();
}
