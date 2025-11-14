class FormulaContent extends CellContent {
    private final String formulaRaw;
    public FormulaContent(String formulaRaw) {
        this.formulaRaw = formulaRaw == null ? "" : formulaRaw;
    }
    @Override public String getRaw() { return formulaRaw; }
    @Override public String getType() { return "FORMULA"; }
}
