
package tingeltangel.core.scripting;

public class SyntaxError extends Exception {
    
    private int row = -1;
    private int tingID = -1;
    
    public SyntaxError(String message) {
        super(message);
    }
    
    public void setRow(int row) {
        this.row = row;
    }
    
    public void setTingID(int tingID) {
        this.tingID = tingID;
    }
    
    public int getRow() {
        return(row);
    }
    
    public int getTingID() {
        return(tingID);
    }
    
}
