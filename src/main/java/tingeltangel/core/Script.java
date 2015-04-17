package tingeltangel.core;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import tingeltangel.core.scripting.Command;
import tingeltangel.core.scripting.Commands;
import tingeltangel.core.scripting.Instance;
import tingeltangel.core.scripting.SyntaxError;
import tingeltangel.core.scripting.Disassembler;

public class Script {

    private Entry entry;
    private String code;
    
    public Script(String code, Entry entry) {
        this.entry = entry;
        this.code = code;
    }

    public Script(byte[] binary, Entry entry) {
        this.entry = entry;
        this.code = new Disassembler().disassemble(binary);
    }
    
    void changeMade() {
        entry.changeMade();
    }
    
    public void setCode(String code) {
        this.code = code;
        changeMade();
    }
    
    @Override
    public String toString() {
        return(code);
    }
    
    public int getSize(boolean calledFromScript) throws SyntaxError {
        int rc = 0;
        int size = 0;
        try {
            BufferedReader in = new BufferedReader(new StringReader(code));
            String row;
            while((row = in.readLine()) != null) {
                rc++;
                row = row.trim();
                if((!row.isEmpty()) && (!row.startsWith("//")) && (!row.startsWith(":"))) {
                    int p = row.indexOf(" ");
                    if(p != -1) {
                        row = row.substring(0, p);
                    }
                    if(!row.startsWith(":")) {
                        if(row.startsWith("call")) {
                            // extract argument
                            String callID = row.substring("call".length()).trim();
                            try {
                                Script sub = entry.getBook().getEntryByID(Integer.parseInt(callID)).getScript();
                                if(sub == null) {
                                    throw new SyntaxError("methode nicht gefunden (oid=" + callID + ")");
                                }
                                size += sub.getSize(true);
                            } catch(NumberFormatException nfe) {
                                throw new SyntaxError("call benötigt als Argument eine OID");
                            }
                        } else if(row.equals("return") && calledFromScript) {
                            size += 4; // because return gets replaced by jmp command
                        } else {
                            size += Commands.getSize(row);
                        }
                    }
                }
            }
        } catch(IOException ioe) {
            throw new Error(ioe);
        } catch(SyntaxError se) {
            se.setRow(rc);
            se.setTingID(entry.getTingID());
            throw se;
        }
        return(size + 1); // +1 for the leading 0x00
    }
    
    public boolean isSub() {
        try {
            BufferedReader in = new BufferedReader(new StringReader(code));
            String row;
            while((row = in.readLine()) != null) {
                if(row.trim().startsWith("return")) {
                    in.close();
                    return(true);
                }
            }
            in.close();
        } catch(IOException e) {
            throw new Error(e);
        }
        return(false);
    }
    
    public void execute() throws SyntaxError {
        compile();
        int p = 0;
        while(true) {
            if(p >= script.size()) {
                SyntaxError error = new SyntaxError("missing 'end' command");
                error.setTingID(entry.getTingID());
                error.setRow(-1);
                throw error;
            }
            Instance instance = script.get(p);
            if(instance.getCommand().getAsm().equals("end")) {
                return;
            } else if(instance.getCommand().getAsm().equals("call")) {
                int oid = instance.getFirstArgument();
                entry.getBook().getEntryByID(oid).getScript().execute();
            } else if(instance.getCommand().getAsm().equals("return")) {
                return;
            } else {
                boolean doJump = instance.execute(entry.getBook().getEmulator());
                if(doJump) {
                    p = instanceLabelsII.get(instance.getLabel());
                } else {
                    p++;
                }
            }
        }
    }
    
    private LinkedList<Instance> script = null;
    private HashMap<String, Integer> instanceLabelsSI = null;
    private HashMap<Integer, Integer> instanceLabelsII = null;
    
    private int labelCounter = 0;
    
    private String mergeCodeOnCalls() throws IOException, SyntaxError {
        String returnLabel = "return_" + (labelCounter++);
        StringBuilder mergedCode = new StringBuilder();
        BufferedReader in = new BufferedReader(new StringReader(code));
        String row;
        int rc = 0;
        while((row = in.readLine()) != null) {
            rc++;
            row = row.trim();
            if((!row.isEmpty()) && (!row.startsWith("//"))) {
                if(row.startsWith("call")) {
                    try {
                        int oid = Integer.parseInt(row.substring("code".length()).trim());
                        String subCode = entry.getBook().getEntryByID(oid).getScript().mergeCodeOnCalls();
                        mergedCode.append(subCode);
                    } catch(NumberFormatException nfe) {
                        SyntaxError error = new SyntaxError("call needs a value as argument");
                        error.setRow(rc);
                        error.setTingID(entry.getTingID());
                        throw error;
                    }
                } else if(row.equals("return")) {
                    mergedCode.append("jmp ").append(returnLabel).append("\n");
                } else {
                    mergedCode.append(row).append("\n");
                }
            }
        }
        in.close();
        mergedCode.append(":").append(returnLabel).append("\n");
        return(mergedCode.toString());
    }
    
    public byte[] compile() throws SyntaxError {
        HashMap<String, Integer> labels = new HashMap<String, Integer>();
        instanceLabelsSI = new HashMap<String, Integer>();
        instanceLabelsII = new HashMap<Integer, Integer>();
        script = new LinkedList<Instance>();
        int rc = 0;
        try {
            
            String mergedCode = mergeCodeOnCalls();
            
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(bout);
        
            // generate label map
            BufferedReader in = new BufferedReader(new StringReader(mergedCode.toString()));
            int position = 0;
            int instancePos = 0;
            String row;
            while((row = in.readLine()) != null) {
                rc++;
                row = row.trim();
                if((!row.isEmpty()) && (!row.startsWith("//"))) {
                    row = row.trim();
                    if(row.startsWith(":")) {
                        labels.put(row.substring(1).trim(), position);
                        instanceLabelsSI.put(row, instancePos);
                    } else {
                        int p = row.indexOf(" ");
                        String cmd = row;
                        if(p != -1) {
                            cmd = row.substring(0, p);
                        }
                        position += Commands.getSize(cmd);
                        instancePos++;
                    }
                }
            }
            in.close();
            
            
            // generate binary
            in = new BufferedReader(new StringReader(mergedCode.toString()));
            rc = 0;
            while((row = in.readLine()) != null) {
                rc++;
                row = row.trim();
                if((!row.isEmpty()) && (!row.startsWith("//")) && (!row.startsWith(":"))) {
                    
                    
                    int p = row.indexOf(" ");
                    String cmd = row;
                    String args = null;
                    if(p != -1) {
                        cmd = row.substring(0, p).trim();
                        args = row.substring(p).trim();
                    }
                    Command command = null;
                    
                    
                    String arg1 = null;
                    String arg2 = null;
                    switch(Commands.getArguments(cmd)) {
                        case 0:
                            command = Commands.getCommand(cmd);
                            break;
                        case 1:
                            arg1 = args;
                            command = Commands.getCommand(cmd, arg1);
                            break;
                        case 2:
                            p = args.indexOf(",");
                            arg1 = args.substring(0, p).trim();
                            arg2 = args.substring(p + 1).trim();
                            command = Commands.getCommand(cmd, arg1, arg2);
                            break;
                    }
                    
                    Instance instance = new Instance(command);
                    if(command.firstArgumentIsLabel()) {
                        instance.setLabel(labels.get(arg1));
                        instanceLabelsII.put(instance.getLabel(), instanceLabelsSI.get(arg1));
                    } else {
                        if(Commands.getArguments(cmd) > 0) {
                            
                            instance.setFirstArgument(arg1);
                        }
                        if(Commands.getArguments(cmd) > 1) {
                            instance.setSecondArgument(arg2);
                        }
                    }
                    
                    instance.compile(out);
                    
                    script.add(instance);
                }
            }    
            in.close();
            
            out.write(0x00);
            
            out.flush();
            byte[] result = bout.toByteArray();
            out.close();
            return(result);
        } catch(IOException ioe) {
            throw new Error(ioe);
        } catch(SyntaxError se) {
            
            if(se.getRow() < 0) {
            
                se.setRow(rc);
                se.setTingID(entry.getTingID());
            }
            throw se;
        }
    }
}
