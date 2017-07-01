package chart;

public class ChartCli {
    public static void main(String[] args) {
        int week = Integer.parseInt(args[0]);
        ChartReader reader = new ChartReader(week);
    }
}
