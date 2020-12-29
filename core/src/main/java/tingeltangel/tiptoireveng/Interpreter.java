/*
    Copyright (C) 2015  Jesper Zedlitz <jesper@zedlitz.de>
  
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

package tingeltangel.tiptoireveng;

import tingeltangel.core.ReadYamlFile;
import tiptoi_reveng.analysis.DepthFirstAdapter;
import tiptoi_reveng.node.*;

import java.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The interpreter parses the AST and generates script code for the TING pen. It works in two phases: First, variable
 * names, registers, identifiers and file names  are collected. Second, the script code is generated.
 */
public class Interpreter extends DepthFirstAdapter {
    private Map<String, Integer> variable2register = new HashMap<String, Integer>();
    private Map<String, Integer> identifier2oid = new HashMap<String, Integer>();
    private Map<String, Integer> filename2oid = new HashMap<String, Integer>();
    private Set<String> variables = new HashSet<String>();
    private Set<String> identifiers = new HashSet<String>();
    private Set<Integer> registers = new HashSet<Integer>();
    private Set<Integer> oids = new HashSet<Integer>();
    private Set<String> fileNames = new HashSet<String>();
    private boolean secondPhase = false;
    private int labelCounter = 1;
    private Stack<Integer> labelStack = new Stack<Integer>();
    private StringBuffer script = new StringBuffer();

    private final static Logger log = LogManager.getLogger(Interpreter.class);
    
    public Map<String, Integer> getFilename2oid() {
        return filename2oid;
    }

    public Map<String, Integer> getIdentifier2oid() {
        return identifier2oid;
    }

    public StringBuffer getScript() {
        return script;
    }

    /**
     * Start the second phase, i.e., the collection of variable names etc. has been finished and the next run
     * will generate script code.
     */
    public void startSecondPhase() {

        // assign registers
        int register = 1;
        for (String variable : variables) {
            while (registers.contains(register)) {
                register++;
            }
            variable2register.put(variable, register);
            register++;
        }

        // assign OIDs to identifiers
        int oid = ReadYamlFile.MINIMAL_OID;
        for (String identifier : identifiers) {
            if (!identifier2oid.containsKey(identifier)) {
                while (oids.contains(oid)) {
                    oid++;
                }
                identifier2oid.put(identifier, oid);
                oids.add(oid);
                oid++;
            }
        }

        // assign OIDs to file names
        for (String fileName : fileNames) {
            while (oids.contains(oid)) {
                oid++;
            }
            filename2oid.put(fileName, oid);
            oids.add(oid);
            oid++;
        }

        secondPhase = true;
    }

    public void addIdentifier(String identifier) {
        this.identifiers.add(identifier);
    }

    public Set<Integer> getOids() {
        return oids;
    }

    @Override
    public void outAVariableValue(AVariableValue node) {
        variables.add(node.getIdentifier().getText());
    }

    @Override
    public void outARegisterValue(ARegisterValue node) {
        registers.add(Integer.parseInt(node.getInteger().getText()));
    }

    @Override
    public void outAOidFileName(AOidFileName node) {
        int oid = Integer.parseInt(node.getInteger().getText())    ;
        if( oid <= ReadYamlFile.MINIMAL_OID) {
            oid += 7000;
        }
        oids.add(oid);
    }

    @Override
    public void inAConditionalStatement(AConditionalStatement node) {
        if (secondPhase) {
            labelStack.push(labelCounter++);
        }
    }

    @Override
    public void outAConditionalStatement(AConditionalStatement node) {
        if (secondPhase) {
            script.append(":label").append(labelStack.pop()).append("\n");
        }
    }

    private String convertValue(PValue value) {
        String result;
        if (value instanceof AVariableValue) {
            int register = variable2register.get(((AVariableValue) value).getIdentifier().getText());
            result = "v" + register;
        } else if (value instanceof ARegisterValue) {
            result = "v" + ((ARegisterValue) value).getInteger();
        } else if (value instanceof ANumberValue) {
            result = ((ANumberValue) value).getInteger().toString();
        } else {
            throw new RuntimeException("Unknown PValue: " + value.getClass());
        }
        return result;
    }

    @Override
    public void inABopEqComparison(ABopEqComparison node) {
        if (secondPhase) {
            script.append("cmp ").append(convertValue(node.getE1())).append(", ").append(convertValue(node.getE2())).append("\n");
            script.append("jne label").append(labelStack.peek()).append("\n");
        }
    }

    @Override
    public void inABopNeqComparison(ABopNeqComparison node) {

        if (secondPhase) {
            script.append("cmp ").append(convertValue(node.getE1())).append(", ").append(convertValue(node.getE2())).append("\n");
            script.append("je label").append(labelStack.peek()).append("\n");
        }
    }

    @Override
    public void inABopGtComparison(ABopGtComparison node) {
        if (secondPhase) {
            script.append("cmp ").append(convertValue(node.getE1())).append(", ").append(convertValue(node.getE2())).append("\n");
            script.append("jbe label").append(labelStack.peek()).append("\n");
        }
    }

    @Override
    public void inABopGteqComparison(ABopGteqComparison node) {
        if (secondPhase) {
            script.append("cmp ").append(convertValue(node.getE1())).append(", ").append(convertValue(node.getE2())).append("\n");
            script.append("jb label").append(labelStack.peek()).append("\n");
        }
    }

    @Override
    public void inABopLtComparison(ABopLtComparison node) {
        if (secondPhase) {
            script.append("cmp ").append(convertValue(node.getE1())).append(", ").append(convertValue(node.getE2())).append("\n");
            script.append("jge label").append(labelStack.peek()).append("\n");
        }
    }

    @Override
    public void inABopLteqComparison(ABopLteqComparison node) {
        if (secondPhase) {
            script.append("cmp ").append(convertValue(node.getE1())).append(", ").append(convertValue(node.getE2())).append("\n");
            script.append("jg label").append(labelStack.peek()).append("\n");
        }
    }

    private int convertFilenameToOid(PFileName fileName) {
        int result;
        if (fileName instanceof ATextFileName) {
            String identifier = ((ATextFileName) fileName).getIdentifier().getText();
            result = filename2oid.get(identifier);
        } else if (fileName instanceof AOidFileName) {
            result = Integer.parseInt(((AOidFileName) fileName).getInteger().getText());
            if( result <= ReadYamlFile.MINIMAL_OID) {
                result += 7000;
            }
        } else {
            throw new RuntimeException("unknown file name type " + fileName.getClass());
        }
        return result;
    }

    @Override
    public void outAPlayAction(APlayAction node) {
        if (secondPhase) {
            script.append("playoid ").append(convertFilenameToOid(node.getFiles().getFirst())).append("\n");
            if (node.getFiles().size() > 1) {
                log.error("More than two file names not supported, yet.");
            }
        }
    }

    @Override
    public void inATextFileName(ATextFileName node) {
        fileNames.add(node.getIdentifier().getText());
    }

    @Override
    public void inASetAction(ASetAction node) {
        if (secondPhase) {
            script.append("set ").append(convertValue(node.getLeft())).append(",").append(convertValue(node.getRight())).append("\n");
        }
    }
}
