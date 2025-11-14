abstract class CellContent {
    /** return the raw text exactly as stored */
    public abstract String getRaw();
    /** returns true if empty content */
    public boolean isEmpty() { String r = getRaw(); return r == null || r.length() == 0; }
    /** content type for simple introspection */
    public abstract String getType();
}