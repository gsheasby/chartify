package chart;

public interface ChartPrinter<T extends Chart> {
    void print(T chart);
}
