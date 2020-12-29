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


public class Methods {

    public static void callidV(Emulator emulator, Integer arg1, Integer arg2) {
        // do nothing
    }
    
    public static void callidR(Emulator emulator, Integer arg1, Integer arg2) {
        // do nothing
    }
    
    public static void setV(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setRegister(arg1, arg2);
    }
    
    public static void setR(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setRegister(arg1, emulator.getRegister(arg2));
    }
    
    public static void end(Emulator emulator, Integer arg1, Integer arg2) {
        ;
    }
    
    public static void clearver(Emulator emulator, Integer arg1, Integer arg2) {
        for(int i = 0; i <= emulator.getMaxRegister(); i++) {
            emulator.setRegister(i, 0);
        }
    }
    
    public static void andV(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setRegister(arg1, emulator.getRegister(arg1) & arg2);
    }
    
    public static void andR(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setRegister(arg1, emulator.getRegister(arg1) & emulator.getRegister(arg2));
    }
    
    public static void orV(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setRegister(arg1, emulator.getRegister(arg1) | arg2);
    }
    
    public static void orR(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setRegister(arg1, emulator.getRegister(arg1) | emulator.getRegister(arg2));
    }
    
    public static void not(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setRegister(arg1, (emulator.getRegister(arg1) ^ 0xffff) & 0xffff);
    }
    
    public static void addV(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setRegister(arg1, (emulator.getRegister(arg1) + arg2) % 0x10000);
    }
    
    public static void addR(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setRegister(arg1, (emulator.getRegister(arg1) + emulator.getRegister(arg2)) % 0x10000);
    }
    
    public static void subV(Emulator emulator, Integer arg1, Integer arg2) {
        int r = emulator.getRegister(arg1) - arg2;
        if(r < 0) {
            r += 0x10000;
        }
        emulator.setRegister(arg1, r);
    }
    
    public static void subR(Emulator emulator, Integer arg1, Integer arg2) {
        int r = emulator.getRegister(arg1) - emulator.getRegister(arg2);
        if(r < 0) {
            r += 0x10000;
        }
        emulator.setRegister(arg1, r);
    }
    
    public static void pauseV(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.pause(arg1);
    }
    
    public static void pauseR(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.pause(emulator.getRegister(arg1));
    }
    
    public static void playoidV(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.play(arg1);
    }
    
    public static void playoidR(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.play(emulator.getRegister(arg1));
    }
    
    public static void cmpV(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setLeftValue(emulator.getRegister(arg1));
        emulator.setRightValue(arg2);
    }
    
    public static void cmpR(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setLeftValue(emulator.getRegister(arg1));
        emulator.setRightValue(emulator.getRegister(arg2));
    }
    
    public static Boolean jmp(Emulator emulator, Integer arg1, Integer arg2) {
        return(true);
    }
    
    public static Boolean je(Emulator emulator, Integer arg1, Integer arg2) {
        return(emulator.getLeftValue() == emulator.getRightValue());
    }
    
    public static Boolean jne(Emulator emulator, Integer arg1, Integer arg2) {
        return(emulator.getLeftValue() != emulator.getRightValue());
    }
    
    public static Boolean jg(Emulator emulator, Integer arg1, Integer arg2) {
        return(emulator.getLeftValue() > emulator.getRightValue());
    }
    
    public static Boolean jge(Emulator emulator, Integer arg1, Integer arg2) {
        return(emulator.getLeftValue() >= emulator.getRightValue());
    }
    
    public static Boolean jb(Emulator emulator, Integer arg1, Integer arg2) {
        return(emulator.getLeftValue() < emulator.getRightValue());
    }
    
    public static Boolean jbe(Emulator emulator, Integer arg1, Integer arg2) {
        return(emulator.getLeftValue() <= emulator.getRightValue());
    }
    
}
