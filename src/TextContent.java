/** Textual content */
class TextContent extends CellContent {
    private final String text;
    public TextContent(String t) { this.text = t == null ? "" : t; }
    @Override public String getRaw() { return text; }
    @Override public String getType() { return "TEXT"; }
}