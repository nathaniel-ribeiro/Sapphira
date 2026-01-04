public record IntrinsicPerformanceRatingParameters(double sensitivity, double consistency) {
    public IntrinsicPerformanceRatingParameters {
        if(sensitivity <= 0) throw new IllegalArgumentException("Sensitivity must be positive");
        if(consistency < 0) throw new IllegalArgumentException("Consistency must be non-negative");
    }
}
