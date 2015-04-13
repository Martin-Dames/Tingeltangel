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
package tingeltangel;


import org.yaml.snakeyaml.Yaml;
import tiptoi_reveng.lexer.Lexer;
import tiptoi_reveng.lexer.LexerException;
import tiptoi_reveng.node.Start;
import tiptoi_reveng.parser.Parser;
import tiptoi_reveng.parser.ParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

public class ReadYamlFile {

    public void read(InputStream in) throws ParserException, IOException, LexerException {
        Yaml yaml = new Yaml();
        Map data = (Map) yaml.load(in);

        Map scripts = (Map) data.get("scripts");

        for (Object oid : scripts.keySet()) {
            @SuppressWarnings("unchecked")
            List<String> commands = (List<String>) scripts.get(oid);
            for (String command : commands) {
                PushbackReader reader = new PushbackReader(new StringReader(command));

                Lexer lexer = new Lexer(reader);
                Parser parser = new Parser(lexer);
                try {
                    Start start = parser.parse();
                } catch (ParserException pe) {
                    System.err.println("Could not parse command " + command);
                    pe.printStackTrace();
                    throw pe;
                }
            }
        }
    }
}
