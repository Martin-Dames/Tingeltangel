/*
    Copyright (C) 2015   Martin Dames <martin@bastionbytes.de>
  
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
  
*/

package tingeltangel.core.scripting;

import java.lang.reflect.Method;

public class Command implements Comparable {

    public final static int NONE = 0;
    public final static int REGISTER = 1;
    public final static int VALUE = 2;
    public final static int LABEL = 3;
    
    private String asm;
    private int code;
    private int firstArgument;
    private int secondArgument;
    private String method;
    private String description;
    
    public String getAsm() {
        return(asm);
    }
    
    public String getDescription() {
        return(description);
    }
    
    public Method getMethod() {
        try {
            return(Methods.class.getMethod(method, Emulator.class, Integer.class, Integer.class));
        } catch(NoSuchMethodException e) {
            throw new Error(e);
        }
    }
    
    public int getCode() {
        return(code);
    }
    
    public Command(String method, String asm, int code, String description) {
        init(method, asm, code, NONE, NONE, description);
    }
    
    public Command(String method, String asm, int code, int argument, String description) {
        init(method, asm, code, argument, NONE, description);
    }
    
    public Command(String method, String asm, int code, int firstArgument, int secondArgument, String description) {
        init(method, asm, code, firstArgument, secondArgument, description);
    }
    
    private void init(String method, String asm, int code, int firstArgument, int secondArgument, String description) {
        this.asm = asm;
        this.code = code;
        this.firstArgument = firstArgument;
        this.secondArgument = secondArgument;
        this.method = method;
        this.description = description;
    }
    
    
    public int getNumberOfArguments() {
        if(firstArgument == NONE) {
            return(0);
        } else if(secondArgument == NONE) {
            return(1);
        } else {
            return(2);
        }
    }
    
    public boolean firstArgumentIsRegister() {
        return(firstArgument == REGISTER);
    }
    
    public boolean firstArgumentIsValue() {
        return(firstArgument == VALUE);
    }
    
    public boolean firstArgumentIsLabel() {
        return(firstArgument == LABEL);
    }
    
    public boolean secondArgumentIsRegister() {
        return(secondArgument == REGISTER);
    }
    
    public boolean secondArgumentIsValue() {
        return(secondArgument == VALUE);
    }

    @Override
    public int compareTo(Object obj) {
        Command c = (Command)obj;
        return(Integer.valueOf(code).compareTo(c.code));
    }
}
