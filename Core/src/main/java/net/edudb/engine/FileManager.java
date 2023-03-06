/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.engine;

import net.edudb.block.*;
import net.edudb.page.Page;
import net.edudb.structure.table.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileManager {
    private static final FileManager instance = new FileManager();

    private FileManager() {
    }

    public static FileManager getInstance() {
        return instance;
    }

    public static ArrayList<String> readSchema() {
        return readFile(getSchemaPath());
    }

    public static String getSchemaPath() {
        return Utility.appendToPath(Config.databasePath(), "schema.txt");
    }

    /**
     * Reads all lines from a file and returns them as a list of strings.
     *
     * @param filePath the path to the file to read
     * @return a list of strings containing all lines from the file, or an empty list if an error occurs
     * @author Ahmed Nasser Gaafar
     */
    public static ArrayList<String> readFile(String filePath) {
        ArrayList<String> lines = new ArrayList<>();
        try {
            lines = (ArrayList<String>) Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return lines;
    }

    /**
     * Writes data to a file.
     *
     * @param filePath the path to the file to write to
     * @param data     the data to write to the file
     * @param append   true to append the data to the end of the file, false to overwrite the file
     * @author Ahmed Nasser Gaafar
     */
    public static void writeFile(String filePath, String data, boolean append) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a page from disk using a block reader.
     *
     * @param pageName The name of the page to read.
     * @return The read page.
     */
    public Page readPage(String pageName) {
        BlockAbstractFactory blockFactory = new BlockReaderFactory();
        BlockReader blockReader = blockFactory.getReader(Config.blockType());

        try {
            return blockReader.read(pageName);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Writes a page to disk using a block writer.
     *
     * @param page The page to write.
     */
    public void writePage(Page page) {
        BlockAbstractFactory blockFactory = new BlockWriterFactory();
        BlockWriter blockWriter = blockFactory.getWriter(Config.blockType());

        try {
            blockWriter.write(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads a table from disk using a table reader.
     *
     * @param tableName The name of the table to read.
     * @return The read table.
     */
    public Table readTable(String tableName) {
        TableAbstractFactory tableFactory = new TableReaderFactory();
        TableReader tableReader = tableFactory.getReader(Config.tableType());

        try {
            return tableReader.read(tableName);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Writes a table to disk using a table writer.
     *
     * @param table The table to write.
     */
    public void writeTable(Table table) {
        TableAbstractFactory tableFactory = new TableWriterFactory();
        TableWriter blockWriter = tableFactory.getWriter(Config.tableType());

        try {
            blockWriter.write(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
