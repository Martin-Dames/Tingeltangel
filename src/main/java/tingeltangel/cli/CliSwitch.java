/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.cli;

/**
 *
 * @author mdames
 */
public abstract class CliSwitch implements Comparable {
    
    public abstract String getName();
    public abstract String getLabel();
    public abstract String getDescription();
    public abstract boolean isOptional();
    public abstract boolean hasArgument();
    // default null means complex or no default value
    public abstract String getDefault();
    public abstract boolean acceptValue(String value);
    
    
    @Override
    public int compareTo(Object object) {
        return(getName().compareTo(((CliSwitch)object).getName()));
    }
}
