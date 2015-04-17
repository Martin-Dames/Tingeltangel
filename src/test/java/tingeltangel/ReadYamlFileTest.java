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

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;
import tiptoi_reveng.lexer.Lexer;
import tiptoi_reveng.lexer.LexerException;
import tiptoi_reveng.node.Start;
import tiptoi_reveng.parser.Parser;
import tiptoi_reveng.parser.ParserException;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

public class ReadYamlFileTest {

    private ReadYamlFile reader = new ReadYamlFile();

    @Test
    public void testParser() throws ParserException, IOException, LexerException {
        reader.read(getClass().getResourceAsStream("/tip-toi-reveng/example.yaml"));
    }

    @Test
    public void testParser2() throws ParserException, IOException, LexerException {
        reader.read(getClass().getResourceAsStream("/tip-toi-reveng/vokabeltrainer.yaml"));

    }

    @Test
    public void testParser4() throws ParserException, IOException, LexerException {
        reader.read(getClass().getResourceAsStream("/tip-toi-reveng/WWW_Bauernhof.yaml"));

    }
    @Test
    public void testParser5() throws ParserException, IOException, LexerException {
        reader.read(getClass().getResourceAsStream("/tip-toi-reveng/WWW_Weltatlas.yaml"));

    }

}
