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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;



public class Commands {
    
    private final static HashMap<String, HashSet<Command>> commands = new HashMap<String, HashSet<Command>>();
    private final static HashMap<Integer, Command> opcode2command = new HashMap<Integer, Command>();
    
    static {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(Commands.class.getResourceAsStream("/commands.properties")));
            String row;
            while((row = in.readLine()) != null) {
                row = row.trim();
                if((!row.isEmpty()) && (!row.startsWith("#"))) {
                    String[] args = row.split("\\|");
                    int code = Integer.parseInt(args[0].substring(2), 16);
                    if(args.length == 4) {
                        addCmd(new Command(args[1], args[2], code, args[3]));
                    } else if(args.length == 5) {
                        addCmd(new Command(args[1], args[2], code, parseArg(args[3]), args[4]));
                    } else if(args.length == 6) {
                        addCmd(new Command(args[1], args[2], code, parseArg(args[3]), parseArg(args[4]), args[5]));
                    } else {
                        throw new Error();
                    }
                }
            }
            in.close();
        } catch(IOException ioe) {
            throw new Error(ioe);
        }
    }
 
    private static int parseArg(String s) {
        if(s.equals("L")) {
            return(Command.LABEL);
        } else if(s.equals("R")) {
            return(Command.REGISTER);
        } else if(s.equals("V")) {
            return(Command.VALUE);
        } else {
            throw new Error();
        }
    }
    
    public static Command getCommand(int opcode) {
        return(opcode2command.get(opcode));
    }
    
    public static Command getCommand(String cmd) {
        HashSet<Command> set = commands.get(cmd);
        if(set.size() != 1) {
            throw new Error();
        }
        return(set.iterator().next());
    }
    
    public static boolean isJump(String cmd) {
        HashSet<Command> set = commands.get(cmd);
        if((set == null) || set.isEmpty()) {
            return(false);
        }
        return(set.iterator().next().firstArgumentIsLabel());
    }
    
    private static boolean isValue(String s) {
        if(s.startsWith("0x")) {
            try {
                Integer.parseInt(s.substring(2), 16);
            } catch(NumberFormatException e) {
                return(false);
            }
            return(true);
        } else {
            try {
                Integer.parseInt(s);
            } catch(NumberFormatException e) {
                return(false);
            }
            return(true);
        }
    }
    
    private static boolean isRegister(String s) {
        if(!s.toLowerCase().startsWith("v")) {
            return(false);
        }
        s = s.substring(1);
        int i;
        try {
            i = Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return(false);
        }
        return(i >= 0 && i <= Emulator.REGISTERS);
    }
    
    public static Command getCommand(String cmd, String firstArgument) throws SyntaxError {
        HashSet<Command> set = commands.get(cmd);
        Command cmd1 = set.iterator().next();
        if(cmd1.firstArgumentIsLabel()) {
            return(cmd1);
        }
        // not cmd
        if(cmd1.getAsm().toLowerCase().equals("not")) {
            return(cmd1);
        }
        
        Iterator<Command> cmds = set.iterator();
        while(cmds.hasNext()) {
            cmd1 = cmds.next();
            if(isValue(firstArgument) && cmd1.firstArgumentIsValue()) {
                return(cmd1);
            } else if((isRegister(firstArgument)) && cmd1.firstArgumentIsRegister()) {
                return(cmd1);
            }
        }
        throw new SyntaxError("bad number of arguments or bad argument types (cmd=" + cmd + "; arg1=" + firstArgument + ")");
    }
    
    public static Command getCommand(String cmd, String firstArgument, String secondArgument) throws SyntaxError {
        Iterator<Command> cmds = commands.get(cmd).iterator();
        while(cmds.hasNext()) {
            Command cmd1 = cmds.next();
            boolean match1 = false;
            boolean match2 = false;
            if(isValue(firstArgument) && cmd1.firstArgumentIsValue()) {
                match1 = true;
            } else if((isRegister(firstArgument)) && cmd1.firstArgumentIsRegister()) {
                match1 = true;
            }
            if(isValue(secondArgument) && cmd1.secondArgumentIsValue()) {
                match2 = true;
            } else if((isRegister(secondArgument)) && cmd1.secondArgumentIsRegister()) {
                match2 = true;
            }
            if(match1 && match2) {
                return(cmd1);
            }
        }
        throw new SyntaxError("bad number of arguments or bad argument types (cmd=" + cmd + "; arg1=" + firstArgument + "; arg2=" + secondArgument + ")");
    }
    
    private static void addCmd(Command command) {
        HashSet<Command> set = commands.get(command.getAsm());
        if(set == null) {
            set = new HashSet<Command>();
            commands.put(command.getAsm(), set);
        }
        set.add(command);
        opcode2command.put(command.getCode(), command);
    }
    
    public static Iterator<Command> iterator() {
        TreeSet<Command> all = new TreeSet<Command>();
        Iterator<HashSet<Command>> i = commands.values().iterator();
        while(i.hasNext()) {
            all.addAll(i.next());
        }
        return(all.iterator());
    }
    
    public static int getSize(String cmd) throws SyntaxError {
        HashSet<Command> set = commands.get(cmd);
        if(set == null) {
            throw new SyntaxError("unknown command (" + cmd + ")");
        }
        Command c = set.iterator().next();
        return(c.getNumberOfArguments() * 2 + 2);
    }
    
    public static int getArguments(String cmd) throws SyntaxError {
        
        if(cmd.toLowerCase().equals("not")) {
            return(1);
        }
        
        HashSet<Command> set = commands.get(cmd);
        if(set == null) {
            throw new SyntaxError("unknown command (" + cmd + ")");
        }
        return(set.iterator().next().getNumberOfArguments());
    }
    
    
}
