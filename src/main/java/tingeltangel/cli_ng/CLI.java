/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tingeltangel.cli_ng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import tingeltangel.Tingeltangel;
import tingeltangel.core.Book;

/**
 *
 * @author martin
 */
public class CLI {
    
    private final static CliCmd[] COMMANDS = {
        new NewBook(),
        new LoadBook(),
        new BookInfo(),
        new ImportFromRepository(),
        new ImportManual(),
        new GenerateBooklet(),
        new ShowBooks(),
        new FindNiceMid(),
        new SaveBook(),
        new GenerateBook(),
        new ExportMp3s(),
        new GenerateCodes(),
        new Exit(),
        new Help(),
        /*
        new DeleteBook(),
        new SearchForNewBooksInRepository(),
        new UpdateRepositoryBookList(),
        new ShowBooksInRepository(),
        new CleanupRepository(),
        new TtsGetLanguages(),
        new TtsSetLanguage(),
        new TtsGetVariants(),
        new TtsSetVariants(),
        new CodePageRaw(),
        new CodePageTing(),
        new CodeGetRaw(),
        new CodeGetTing(),
        new CodeRawToTing(),
        new CodeTingToRaw(),
        new SetEntryMp3(),
        new SetEntryMp3Hint(),
        new SetEntryScript(),
        new SetEntrySubScript(),
        new SetEntryTts(),
        new DeleteEntry(),
        new ExportMp3(),
        new GetEntryType(),
        new GetEntryMp3Name(),
        new GetEntryMp3Length(),
        new GetEntryMp3Hint(),
        new GetEntryScript(),
        new GetEntryTts(),
        new SetCover(),
        new GetCover(),
        new Deploy(),
        new GetRegister(),
        new SetRegister(),
        new GetRegisterHint(),
        new SetRegisterHint(),
        new GetMid(),
        new ChangeMid(),
        
        new GetMeta(), // name, publisher, author, version, url, magic, date
        new SetMeta(), // name, publisher, author, version, url, magic, date
        
        new Play(),
        
        new StickUpdate(),
        new StickStatus(),
        new StickBooks(),
        new StickDeleteBook(),
        */
        new StickDebug(),
        /*
        new StickActivateBook() */
    };
    
    private final static HashMap<String, CliCmd> cmds = new HashMap<String, CliCmd>();
    private final static Book book = new Book(15000);
    
    static Book getBook() {
        return(book);
    }
    
    static void showCommands() {
        for(int i = 0; i < COMMANDS.length; i++) {
            System.out.println(COMMANDS[i].getDescription());
        }
    }
    
    static boolean bookOpened() {
        return(book.getID() == 15000);
    }
    
    public static void run() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        out.println("Tingeltangel CLI " + Tingeltangel.MAIN_FRAME_VERSION);
        out.flush();
        
        for(int i = 0; i < COMMANDS.length; i++) {
            CliCmd cmd = COMMANDS[i];
            cmds.put(cmd.getName(), cmd);
        }
        
        
        
        String row;
        
        System.out.print(">");
        
        try {
        
            while((row = in.readLine()) != null) {
                row = row.trim();
                if((!row.isEmpty()) && !row.startsWith("//")) {

                    int p = row.indexOf(" ");
                    String argstr = "";
                    String cmd;
                    if(p < 0) {
                        cmd = row.toLowerCase();
                    } else {
                        cmd = row.substring(0, p).toLowerCase();
                        argstr = row.substring(p + 1).trim();
                    }

                    String[] args = new String[0];
                    if(!argstr.isEmpty()) {
                        args = argstr.split(" ");
                    }

                    LinkedList<String> argList = new LinkedList<String>();
                    for(int i = 0; i < args.length; i++) {
                        String arg = args[i].trim();
                        if(!arg.isEmpty()) {
                            argList.add(arg);
                        }
                    }
                    args = argList.toArray(new String[0]);

                    CliCmd cliCmd = cmds.get(cmd);
                    if(cliCmd == null) {
                        System.err.println("unbekanntes kommando");
                    } else {
                        cliCmd.execute(args);
                    }
                }
                System.out.print(">");
            }
        } catch(IOException e) {
            throw new Error(e);
        }
    }
    
}
