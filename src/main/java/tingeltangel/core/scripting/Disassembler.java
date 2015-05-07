/*
    Copyright (C) 2015   Jesper Zedlitz <jesper@zedlitz.de>
  
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

import java.util.HashMap;
import java.util.Map;

public class Disassembler {

    
    Map<Integer, Integer> labels = new HashMap<Integer, Integer>(); // f(offset)=label
    int labelCount = 1;
    StringBuffer sb = new StringBuffer();
    int offset = 0;
    byte[] b;

    private void disassembleCommandRegisterRegister(String command) {
        sb.append(command);
        sb.append(" v");
        int register1 = ((b[offset + 2] & 0xff) << 8) + (b[offset + 3] & 0xff);
        int register2 = ((b[offset + 4] & 0xff) << 8) + (b[offset + 5] & 0xff);
        sb.append(register1);
        sb.append(",v");
        sb.append(register2);
        sb.append('\n');

        offset =  offset + 6;
    }
    
    private void disassembleCommandRegister(String command) {
        sb.append(command);
        sb.append(" v");
        int register1 = ((b[offset + 2] & 0xff) << 8) + (b[offset + 3] & 0xff);
        sb.append(register1);
        sb.append('\n');

        offset =  offset + 4;
    }

    private void disassembleCommandRegisterValue( String command) {
        sb.append(command);
        sb.append(" v");
        int register = ((b[offset + 2] & 0xff) << 8) + (b[offset + 3] & 0xff);
        int value = ((b[offset + 4] & 0xff) << 8) + (b[offset + 5] & 0xff);
        sb.append(register);
        sb.append(",");
        sb.append(value);
        sb.append('\n');

        offset =  offset + 6;
    }

    private void disassembleJump( String command) {
        sb.append(command);
        sb.append(" ");
        int label = ((b[offset + 2] & 0xff) << 8) + (b[offset + 3] & 0xff);
        //labels.put(label, labelCount);
        sb.append('l');
        sb.append(labels.get(label));
        sb.append('\n');
        //labelCount++;
        offset =  offset + 4;
    }

    /**
     * Disassemble the specified binary and set the script.
     */
    public String disassemble(byte[] b) throws SyntaxError {

        this.b = b;

        
        // first pass (collect jump targets)
        while (offset < b.length) {
            if (offset == b.length - 1) {
                if (b[offset] != 0) {
                    throw new SyntaxError("Last byte must be 0x00.");
                }
                offset += 1;
            } else {
                int opcode = ((b[offset] & 0xff) << 8) | (b[offset + 1] & 0xff);
                Command command = Commands.getCommand(opcode);
                if(command == null) {
                    String msb = Integer.toHexString(b[offset]);
                    String lsb =  Integer.toHexString(b[offset+1]);
                    throw new SyntaxError("unknown byte code 0x"+(msb.length()==1?'0':"")+msb+" 0x"+(lsb.length()==1?'0':"")+lsb);
                } else if(command.firstArgumentIsLabel()) {
                    // jump
                    int label = ((b[offset + 2] & 0xff) << 8) | (b[offset + 3] & 0xff);
                    if(!labels.containsKey(label)) {
                        labels.put(label, labelCount++);
                    }
                    offset += 4;
                } else {
                    offset += (command.getNumberOfArguments() + 1) * 2;
                }
            }
        }
        
        // second pass
        offset = 0;
        while (offset < b.length) {
            
            
            if (labels.containsKey(offset)) {
                sb.append("\n:l");
                sb.append(labels.get(offset));
                sb.append('\n');
            }

            if (offset == b.length - 1) {
                if (b[offset] != 0) {
                    throw new RuntimeException("Last byte must be 0x00.");
                }
                offset += 1;
            } else if (b[offset] == 0x00 && b[offset + 1] == 0x00) {
                sb.append("end\n");
                offset += 2;
            } else if (b[offset] == 0x01 && b[offset + 1] == 0x00) {
                sb.append("clearver\n");
                offset += 2;
            } else if (b[offset] == 0x02 && b[offset + 1] == 0x01) {
                disassembleCommandRegisterValue( "set");
            } else if (b[offset] == 0x02 && b[offset + 1] == 0x02) {
                disassembleCommandRegisterRegister( "set");
            } else if (b[offset] == 0x03 && b[offset + 1] == 0x01) {
                disassembleCommandRegisterValue( "cmp");
            } else if (b[offset] == 0x03 && b[offset + 1] == 0x02) {
                disassembleCommandRegisterRegister( "cmp");
            } else if (b[offset] == 0x04 && b[offset + 1] == 0x01) {
                disassembleCommandRegisterValue( "and");
            } else if (b[offset] == 0x04 && b[offset + 1] == 0x02) {
                disassembleCommandRegisterRegister( "and");
            } else if (b[offset] == 0x05 && b[offset + 1] == 0x01) {
                disassembleCommandRegisterRegister( "or");
            } else if (b[offset] == 0x05 && b[offset + 1] == 0x02) {
                disassembleCommandRegisterRegister( "or");
            } else if (b[offset] == 0x06 && b[offset + 1] == 0x02) {
                disassembleCommandRegister( "not");
            } else if (b[offset] == 0x08 && b[offset + 1] == 0x00) {
                disassembleJump( "jmp");
            } else if (b[offset] == 0x09 && b[offset + 1] == 0x00) {
                disassembleJump( "je");
            } else if (b[offset] == 0x0A && b[offset + 1] == 0x00) {
                disassembleJump( "jne");
            } else if (b[offset] == 0x0B && b[offset + 1] == 0x00) {
                disassembleJump( "jg");
            } else if (b[offset] == 0x0C && b[offset + 1] == 0x00) {
                disassembleJump( "jge");
            } else if (b[offset] == 0x0D && b[offset + 1] == 0x00) {
                disassembleJump( "jb");
            } else if (b[offset] == 0x0E && b[offset + 1] == 0x00) {
                disassembleJump( "jbe");
            } else if (b[offset] == 0x0F && b[offset + 1] == 0x01) {
                disassembleCommandRegisterValue( "and");
            } else if (b[offset] == 0x0F && b[offset + 1] == 0x02) {
                disassembleCommandRegisterRegister( "and");
            } else if (b[offset] == 0x10 && b[offset + 1] == 0x01) {
                disassembleCommandRegisterValue( "sub");
            } else if (b[offset] == 0x10 && b[offset + 1] == 0x02) {
                disassembleCommandRegisterRegister( "sub");
            } else if (b[offset] == 0x14 && b[offset + 1] == 0x00) {
                sb.append("return\n");
                offset += 2;
            } else if (b[offset] == 0x16 && b[offset + 1] == 0x01) {
                sb.append("playoid ");
                int oid = ((b[offset + 2] & 0xff) << 8) + (b[offset + 3] & 0xff);
                sb.append(oid);
                sb.append('\n');
                offset += 4;
            } else if (b[offset] == 0x16 && b[offset + 1] == 0x02) {
                sb.append("playoid v");
                int register = ((b[offset + 2] & 0xff) << 8) + (b[offset + 3] & 0xff);
                sb.append(register);
                sb.append('\n');
                offset += 4;
            } else if (b[offset] == 0x17 && b[offset + 1] == 0x01) {
                sb.append("pause ");
                int value = ((b[offset + 2] & 0xff) << 8) + (b[offset + 3] & 0xff);
                sb.append(value);
                sb.append('\n');
                offset += 4;
            } else if (b[offset] == 0x17 && b[offset + 1] == 0x02) {
                sb.append("pause v");
                int register = ((b[offset + 2] & 0xff) << 8) + (b[offset + 3] & 0xff);
                sb.append(register);
                sb.append('\n');
                offset += 4;
            } else {
                String msb = Integer.toHexString(b[offset]);
                String lsb =  Integer.toHexString(b[offset+1]);
                throw new SyntaxError("unknown byte code 0x"+(msb.length()==1?'0':"")+msb+" 0x"+(lsb.length()==1?'0':"")+lsb);
            }
        }
        return sb.toString();
    }

}
