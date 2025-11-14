public interface SpreadsheetStorage {
    int getRowCount();
    int getColCount();

    /** Return the cell at the given 0-based coordinates. Never returns null (return an Empty cell). */
    Cell getCell(int row0, int col0);

    /** Set the cell at 0-based coordinates */
    void setCell(int row0, int col0, Cell cell);

    /** Iterate row by row (0..rows-1) and give you each row as an array of Cells */
    Iterable<Cell[]> rowsIterable();

    /** Optional: return a string backing type name for diagnostics */
    default String storageType() { return this.getClass().getSimpleName(); }
}