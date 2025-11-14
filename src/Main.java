import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * SpreadsheetApp.java
 * Classes:
 * - Spreadsheet
 * - Cell
 * - CellContent (abstract) and subclasses:
 *     EmptyContent, TextContent, NumericContent, FormulaContent
 *
 * Supports:
 * - create empty spreadsheet (fixed size)
 * - set cell content (TEXT, NUMERIC, FORMULA)
 * - view raw cell content
 * - save to S2V (rows -> lines; cells separated by ';'; for formulas, inside parentheses convert ';'->',' when writing)
 * - load from S2V (reverse conversion ','->';' inside parentheses for formulas)
 *
 * Menu options are provided in main().
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Spreadsheet sheet = null;
        S2VSerializer s2v = new S2VSerializer(); // instance used for save/load

        while (true) {
            System.out.println("\n--- Simple Spreadsheet Menu ---");
            System.out.println("1) Create new empty spreadsheet");
            System.out.println("2) Set cell content");
            System.out.println("3) View cell raw content");
            System.out.println("4) Print sheet region");
            System.out.println("5) Save to S2V");
            System.out.println("6) Load from S2V");
            System.out.println("7) Exit");
            System.out.print("Choose option: ");
            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1": {
                        System.out.print("Rows: ");
                        int rows = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Cols: ");
                        int cols = Integer.parseInt(sc.nextLine().trim());
                        sheet = new Spreadsheet(rows, cols); // uses ArrayStorage by default
                        System.out.println("Created sheet " + rows + "x" + cols + " ");
                        break;
                    }

                    case "2": {
                        if (ensureSheetExists(sheet)) break;
                        System.out.print("Enter cell coord (e.g., A1): ");
                        String coord = sc.nextLine().trim().toUpperCase();
                        int[] rc = Spreadsheet.coordToIndices(coord);
                        if (rc == null) { System.out.println("Invalid coordinate."); break; }
                        System.out.println("Select type: 1-Text 2-Numeric 3-Formula");
                        String t = sc.nextLine().trim();
                        CellContent content;
                        if ("1".equals(t)) {
                            System.out.print("Enter text: ");
                            String text = sc.nextLine();
                            content = new TextContent(text);
                        } else if ("2".equals(t)) {
                            System.out.print("Enter numeric literal (e.g., 123.4): ");
                            String num = sc.nextLine().trim();
                            try {
                                double d = Double.parseDouble(num);
                                content = new NumericContent(d);
                            } catch (NumberFormatException ex) {
                                System.out.println("Invalid number format.");
                                break;
                            }
                        } else if ("3".equals(t)) {
                            System.out.print("Enter formula (start with =, e.g. =SUMA(A1;B1) or =2+3*4): ");
                            String f = sc.nextLine();
                            if (!f.startsWith("=")) { System.out.println("Formula must start with '='."); break; }
                            content = new FormulaContent(f);
                        } else {
                            System.out.println("Invalid type option.");
                            break;
                        }
                        sheet.setCell(rc[0], rc[1], content);
                        System.out.println("Cell " + coord + " set.");
                        break;
                    }

                    case "3": {
                        if (ensureSheetExists(sheet)) break;
                        System.out.print("Enter cell coord (e.g., A1): ");
                        String coord3 = sc.nextLine().trim().toUpperCase();
                        int[] rc3 = Spreadsheet.coordToIndices(coord3);
                        if (rc3 == null) { System.out.println("Invalid coordinate."); break; }
                        Cell c = sheet.getCell(rc3[0], rc3[1]);
                        System.out.println("Raw content: " + (c.getContent().isEmpty() ? "(empty)" : c.getContent().getRaw()));
                        System.out.println("Content type: " + c.getContent().getType());
                        break;
                    }

                    case "4": {
                        if (ensureSheetExists(sheet)) break;
                        System.out.print("From coord (e.g., A1): ");
                        String from = sc.nextLine().trim().toUpperCase();
                        System.out.print("To coord (e.g., C3): ");
                        String to = sc.nextLine().trim().toUpperCase();
                        int[] r1 = Spreadsheet.coordToIndices(from), r2 = Spreadsheet.coordToIndices(to);
                        if (r1 == null || r2 == null) { System.out.println("Invalid coords."); break; }
                        sheet.printRawRegion(r1[0], r1[1], r2[0], r2[1]);
                        break;
                    }

                    case "5": {
                        if (ensureSheetExists(sheet)) break;
                        System.out.print("Enter file path to save (e.g., sheet.s2v): ");
                        String pathStr = sc.nextLine().trim();
                        Path path = Paths.get(pathStr); // no expansion, exact path
                        s2v.save(sheet, path);          // call instance method
                        System.out.println("Saved to " + path.toAbsolutePath());
                        break;
                    }

                    case "6": {
                        System.out.print("Enter file path to load (e.g., sheet.s2v): ");
                        String pathStr = sc.nextLine().trim();
                        Path path = Paths.get(pathStr);
                        if (sheet == null) {
                            System.out.println("No active sheet. Creating default 10x10 sheet to load into.");
                            sheet = new Spreadsheet(10, 10);
                        }
                        s2v.load(sheet, path);          // call instance method
                        System.out.println("Loaded sheet from " + path.toAbsolutePath());
                        break;
                    }

                    case "7":
                        System.out.println("Exiting.");
                        sc.close();
                        return;

                    default:
                        System.out.println("Unknown option.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    // helper to check if sheet exists
    private static boolean ensureSheetExists(Spreadsheet sheet) {
        if (sheet == null) {
            System.out.println("No spreadsheet created. Use option 1 to create one first.");
            return true;
        }
        return false;
    }
}

