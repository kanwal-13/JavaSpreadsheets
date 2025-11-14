import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class Spreadsheet {
    private SpreadsheetStorage storage;

    // Public constructor: pass an implementation (e.g. new ArrayStorage(rows, cols))
    public Spreadsheet(SpreadsheetStorage storage) {
        if (storage == null) throw new IllegalArgumentException("storage required");
        this.storage = storage;
    }

    // convenience constructor to preserve old usage
    public Spreadsheet(int rows, int cols) {
        this(new ArrayStorage(rows, cols));
    }

    // expose counts (1-based consumers use getRows/getCols to know sheet size)
    public int getRows() { return storage.getRowCount(); }
    public int getCols() { return storage.getColCount(); }

    /** Set cell content using 1-based coordinates (row1, col1) */
    public void setCell(int row1, int col1, CellContent content) {
        int r0 = row1 - 1, c0 = col1 - 1;
        if (r0 < 0 || c0 < 0) throw new IndexOutOfBoundsException("1-based indexes required");
        storage.setCell(r0, c0, new Cell(content));
    }

    /** Get cell object (1-based coordinates) */
    public Cell getCell(int row1, int col1) {
        int r0 = row1 - 1, c0 = col1 - 1;
        if (r0 < 0 || c0 < 0) throw new IndexOutOfBoundsException("1-based indexes required");
        return storage.getCell(r0, c0);
    }

    /** Print raw contents in region (inclusive), coords are 1-based indices */
    public void printRawRegion(int r1, int c1, int r2, int c2) {
        int rr1 = Math.min(r1, r2), rr2 = Math.max(r1, r2);
        int cc1 = Math.min(c1, c2), cc2 = Math.max(c1, c2);
        // bounds-check using storage size
        if (rr1 < 1 || rr2 > getRows() || cc1 < 1 || cc2 > getCols())
            throw new IndexOutOfBoundsException("Region out of sheet bounds");
        for (int r = rr1; r <= rr2; r++) {
            for (int c = cc1; c <= cc2; c++) {
                CellContent cc = getCell(r, c).getContent();
                String raw = cc.getRaw();
                if (raw == null || raw.length() == 0) raw = "(empty)";
                System.out.print("[" + raw + "]");
                if (c < cc2) System.out.print("\t");
            }
            System.out.println();
        }
    }

    int getInternalRowCount() { return storage.getRowCount(); }
    int getInternalColCount() { return storage.getColCount(); }
    Cell getCellInternal(int row0, int col0) { return storage.getCell(row0, col0); }   // 0-based
    void setCellInternal(int row0, int col0, Cell cell) { storage.setCell(row0, col0, cell); }

    // -------------------------
    // Utilities: coordinate parsing / conversions
    // -------------------------
    /** Convert e.g. "A1" -> {row, col} 1-based indices; return null on invalid */
    public static int[] coordToIndices(String coord) {
        if (coord == null || coord.length() == 0) return null;
        coord = coord.trim().toUpperCase();
        int i = 0;
        while (i < coord.length() && Character.isLetter(coord.charAt(i))) i++;
        if (i == 0 || i == coord.length()) return null;
        String colPart = coord.substring(0, i);
        String rowPart = coord.substring(i);
        int row;
        try { row = Integer.parseInt(rowPart); } catch (NumberFormatException ex) { return null; }
        int col = colLabelToIndex(colPart);
        if (col <= 0) return null;
        return new int[]{row, col};
    }

    /** Convert column label A->1, B->2, Z->26, AA->27 ... */
    private static int colLabelToIndex(String label) {
        int res = 0;
        for (int i = 0; i < label.length(); i++) {
            char ch = label.charAt(i);
            if (ch < 'A' || ch > 'Z') return -1;
            res = res * 26 + (ch - 'A' + 1);
        }
        return res;
    }

}