package chart;

import java.util.List;

import org.joda.time.DateTime;

public interface SimpleChart {
    int week();

    DateTime date();

    List<? extends SimpleChartEntry> entries();
}
