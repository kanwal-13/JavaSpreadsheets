class Cell {
    private CellContent content;

    public Cell(CellContent c) { this.content = c == null ? new EmptyContent() : c; }

    public CellContent getContent() { return content; }
    public void setContent(CellContent c) { this.content = c == null ? new EmptyContent() : c; }
}