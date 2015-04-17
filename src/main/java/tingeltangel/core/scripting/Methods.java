
package tingeltangel.core.scripting;


public class Methods {

    
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
        emulator.setRegister(arg1, emulator.getRegister(arg1) + arg2);
    }
    
    public static void addR(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setRegister(arg1, emulator.getRegister(arg1) + emulator.getRegister(arg2));
    }
    
    public static void subV(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setRegister(arg1, emulator.getRegister(arg1) - arg2);
    }
    
    public static void subR(Emulator emulator, Integer arg1, Integer arg2) {
        emulator.setRegister(arg1, emulator.getRegister(arg1) - emulator.getRegister(arg2));
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
