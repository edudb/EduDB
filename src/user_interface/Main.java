/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package user_interface;

import DBStructure.DataManager;
import adipe.translate.TranslationException;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws TranslationException {
//        File sqlFile = new File("C:\\Users\\Azza\\Desktop\\workspace\\edudb\\SQLFile.txt");

        try {
            ConsoleReader console = new ConsoleReader();
            console.setPrompt("edudb2:) ");
            String line;
            Parser parser = new Parser();
            while ((line = console.readLine()) != null) {
                if(line.equals("exit")){
                    System.exit(0);
                }else if(line.equals("clear")){
                    console.clearScreen();
                }else if( line.equals("commit") ){
                 }else{
                    parser.parseSQL(line);
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                TerminalFactory.get().restore();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


}