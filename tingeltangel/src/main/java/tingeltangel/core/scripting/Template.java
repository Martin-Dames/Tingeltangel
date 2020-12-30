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
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;


public class Template {
    
    private final LinkedList<Parameter> params = new LinkedList<Parameter>();
    private final LinkedList<String> work = new LinkedList<String>();
    private final String code;
    private String name = "";
    private int labelCounter = 0;
    
    private final static String[] TEMPLATES = {
        "mul",
        "div",
        "mod",
        "divmod",
        "min",
        "max",
        "exp2",
        "rnd",
        "volume",
        "lastid",
        "setbit",
        "unsetbit",
        "getbit",
        "lock",
        "unlock",
        "speak"
    };
    private final static HashMap<String, Template> templates = new HashMap<String, Template>();
    
    static {
        for(int i = 0; i < TEMPLATES.length; i++) {
            templates.put(TEMPLATES[i], new Template(TEMPLATES[i]));
        }
    }
    
    
    private Template(String file) {
        name = file;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(Commands.class.getResourceAsStream("/templates/" + name)));
            StringBuilder _code = new StringBuilder();
            String row;
            boolean inHead = true;
            while((row = in.readLine()) != null) {
                row = row.trim().toLowerCase();
                if((!row.isEmpty()) && (!row.startsWith("//"))) {
                    if(inHead) {
                        if(row.startsWith("params=")) {
                            row = row.substring("params=".length()).trim();
                            if(!row.isEmpty()) {
                                String[] p = row.split(",");
                                for(int i = 0; i < p.length; i++) {
                                    int k = p[i].indexOf(":");
                                    String def = p[i].substring(0, k).trim();
                                    Parameter param = new Parameter();
                                    param.name = p[i].substring(k + 1).trim();
                                    param.register = def.contains("r");
                                    param.value = def.contains("v");
                                    params.add(param);
                                }
                            }
                        } else if(row.startsWith("work=")) {
                            row = row.substring("work=".length()).trim();
                            if(!row.isEmpty()) {
                                String[] p = row.split(",");
                                for(int i = 0; i < p.length; i++) {
                                    work.add(p[i].trim());
                                }
                            }
                        } else if(row.equals("---")) {
                            inHead = false;
                        } else {
                            throw new Error();
                        }
                    } else {
                        _code.append(row).append("\n");
                    }
                }
            }
            this.code = _code.toString();
        } catch(IOException ioe) {
            throw new Error(ioe);
        }
    }
    
    public static Template getTemplate(String name) {
        return(templates.get(name));
    }
    
    public String getName() {
        return(name);
    }
    
    public String getCode(LinkedList<String> args, HashSet<Integer> usableRegisters) throws SyntaxError {
        if(args.size() != params.size()) {
            throw new SyntaxError("falsche Anzahl der Argumente in " + name);
        }
        if(usableRegisters.size() < work.size()) {
            throw new SyntaxError("es stehen zu wenig freie Register zur Verfügung in " + name);
        }
        
        String _code = code;
        
        // replace working regs
        Iterator<String> works = work.iterator();
        Iterator<Integer> regs = usableRegisters.iterator();
        while(works.hasNext()) {
            String w = works.next();
            int r = regs.next();
            _code = _code.replaceAll("\\" + w, "v" + Integer.toString(r));
        }
        
        
        // replace params
        Iterator<String> as = args.iterator();
        Iterator<Parameter> ps = params.iterator();
        int k = 0;
        while(as.hasNext()) {
            k++;
            String a = as.next().toLowerCase();
            Parameter p = ps.next();
            
            if(a.startsWith("v")) {
                if(!p.register) {
                    throw new SyntaxError("register in " + name + " als " + k + ". argument nicht erlaubt");
                }
            } else {
                if(!p.value) {
                    throw new SyntaxError("wert in " + name + " als " + k + ". argument nicht erlaubt");
                }
            }
            
            _code = _code.replaceAll("\\" + p.name, a);
        }
        
        // fix labels
        labelCounter++;
        BufferedReader in = new BufferedReader(new StringReader(_code));
        StringBuilder out = new StringBuilder();
        String row;
        try {
            while((row = in.readLine()) != null) {
                row = row.trim().toLowerCase();
                boolean rowAdded = false;
                if(row.startsWith(":")) {
                    rowAdded = true;
                    // fix label
                    out.append(":").append("templatelabel_").append(name);
                    out.append("_").append(labelCounter).append("_").append(row.substring(1));
                } else {
                    int p = row.indexOf(" ");
                    if(p != -1) {
                        if(Commands.isJump(row.substring(0, p))) {
                            rowAdded = true;
                            // fix jump
                            out.append(row.substring(0, p)).append(" templatelabel_").append(name);
                            out.append("_").append(labelCounter).append("_").append(row.substring(p).trim());
                        }
                    }
                }
                if(!rowAdded) {
                    out.append(row);
                }
                out.append("\n");
            }
        } catch (IOException ex) {
            throw new Error(ex);
        }
        
        return(out.toString());
    }
    
    
}

class Parameter {
    
    public String name;
    public boolean register;
    public boolean value;
    
}