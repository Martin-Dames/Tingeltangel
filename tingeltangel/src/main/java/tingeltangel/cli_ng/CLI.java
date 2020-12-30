/*
 * Copyright 2016 martin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import tingeltangel.core.scripting.SyntaxError;

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
        new ImportWimmelbuch(),
        new ShowBooks(),
        new FindNiceMid(),
        new SaveBook(),
        new GenerateBook(),
        new ExportMp3s(),
        new GenerateCodes(),
        new GenerateCode(),
        new Exit(),
        new Help(),
        new DeleteBook(),
        new SearchForNewBooksInRepository(),
        new UpdateRepositoryBooks(),
        new CleanupRepository(),
        new ShowBooksInRepository(),
        new TtsGetVoices(),
        new TtsGetVoice(),
        new TtsSetVoice(),
        new TtsGetVariants(),
        new TtsGetVariant(),
        new TtsSetVariant(),
        new GenerateCodesPage(),
        new CodeRawToTing(),
        new CodeTingToRaw(),
        new SetEntryMp3(),
        new SetEntryMp3Hint(),
        new SetEntryScript(),
        new SetEntrySubScript(),
        new ClearEntryScript(),
        new ClearEntrySubScript(),
        new AppendEntryScript(),
        new AppendEntrySubScript(),
        new SetEntryTts(),
        new DeleteEntry(),
        new ExportMp3(),
        new GetEntryType(),
        new GetEntryMp3Name(),
        new GetEntryMp3Length(),
        new GetEntryMp3Hint(),
        new GetEntryTts(),
        new GetEntryScript(),
        /*
        new SetCover(),
        new GetCover(),
        */
        new Deploy(),
        new GetRegister(),
        new SetRegister(),
        /*
        new GetRegisterHint(),
        new SetRegisterHint(),
        */
        new GetMid(),
        new ChangeMid(),
        new GetMeta(),
        new SetMeta(),
        new Play(),
        new Execute(),
        /*
        new StickUpdate(),
        new StickStatus(),
        new StickBooks(),
        new StickDeleteBook(),
        */
        new StickDebug(),
        new StickActivateBook(),
        new SetCodeConfig()
    };
    
    private final static HashMap<String, CliCmd> CMDS = new HashMap<String, CliCmd>();
    private static Book book = new Book(15000);
    
    static Book getBook() {
        return(book);
    }
    
    public static void setBook(Book b) {
        book = b;
    }
    
    static void showCommands() {
        for(int i = 0; i < COMMANDS.length; i++) {
            System.out.println(COMMANDS[i].getDescription());
        }
    }
    
    static boolean bookOpened() {
        return(book.getID() != 15000);
    }
    
    public static void init() {
        for(int i = 0; i < COMMANDS.length; i++) {
            CliCmd cmd = COMMANDS[i];
            CMDS.put(cmd.getName(), cmd);
        }
    }
    
    public static void exec(String _cmd) {
        _cmd = _cmd.trim();
        if((!_cmd.isEmpty()) && !_cmd.startsWith("//")) {

            int p = _cmd.indexOf(" ");
            String argstr = "";
            String cmd;
            if(p < 0) {
                cmd = _cmd.toLowerCase();
            } else {
                cmd = _cmd.substring(0, p).toLowerCase();
                argstr = _cmd.substring(p + 1).trim();
            }

            String[] args = new String[0];
            try{
                if(!argstr.isEmpty()) {
                    args = argssplit(argstr);
                }


                CliCmd cliCmd = CMDS.get(cmd);
                if(cliCmd == null) {
                    System.err.println("unbekannter Befehl");
                } else {
                    cliCmd.execute(args);
                }
            } catch(SyntaxError e) {
                System.err.println(e.getMessage());
            }
        }
    }
    
    public static void run(String cmd) {
        
        cmd = cmd.trim();
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        out.println("Tingeltangel CLI " + Tingeltangel.MAIN_FRAME_VERSION);
        out.flush();
        
        init();
        
        if(cmd.isEmpty()) {

            String row;

            System.out.print(">");

            try {

                while((row = in.readLine()) != null) {
                    exec(row);
                    System.out.print(">");
                }
            } catch(IOException e) {
                throw new Error(e);
            }
        } else {
            exec(cmd);
        }
    }
    
    private static String[] argssplit(String x) throws SyntaxError {
        
        LinkedList<String> args = new LinkedList<String>();
        
        while(!x.isEmpty()) {
            if(x.startsWith("\"")) {
                x = x.substring(1);
                int p = x.indexOf("\"");
                if(p == -1) {
                    throw new SyntaxError("missing \"");
                }
                args.add(x.substring(0, p));
                x = x.substring(p + 1).trim();
            } else {
                int p = x.indexOf(" ");
                if(p == -1) {
                    args.add(x);
                    x = "";
                } else {
                    args.add(x.substring(0, p));
                    x = x.substring(p).trim();
                }
            }
        }
        return(args.toArray(new String[0]));
    }
    
}
