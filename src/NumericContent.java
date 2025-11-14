class NumericContent extends CellContent {
    private final double value;
    public NumericContent(double v) { this.value = v; }
    @Override public String getRaw() { return Double.toString(value); }
    @Override public String getType() { return "NUMERIC"; }
    public double getValue() { return value; }
}
