/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package net.edudb.statistics;

import net.edudb.file_utility.FileManager;
import net.edudb.server.ServerWriter;
import net.edudb.structure.DBColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by mohamed on 4/1/14.
 */
public class Schema {

    private static HashMap<String, ArrayList<String>> schema;
    private static boolean initalized;

    public static void initSchema(){
        if(initalized){
            return;
        }
        schema = new HashMap<>();
        setSchema();
        initalized = true;
    }

    public static boolean chekTableExists(String tableName){
        initSchema();
        return schema.get(tableName) != null;
    }

    private static void setSchema(){
        ArrayList<String> lines = FileManager.readFile(FileManager.getSchema());
        for(String line : lines){
            putTable(line);
        }
    }

    // get column list of table
    public static ArrayList<DBColumn> getColumns(String tableName){
        initSchema();
        ArrayList<DBColumn> columns = new ArrayList<>();
        int count = schema.get(tableName).size();
        for (int i=1; i<= count; i++){
            DBColumn column = new DBColumn(i, tableName);
            columns.add(column);
        }
        return columns;
    }

    // add table to schema object
    private static void putTable(String line) {
        String[] tokens = line.split(" ");
        String TableName = tokens[0];
        ArrayList<String> columns = new ArrayList<String>();
        for(int i=1; i<tokens.length; i+=2){
            String columnName = tokens[i];
            columns.add(columnName);
        }
        schema.put(TableName, columns);
    }

    // add table to schema file
    public static void addTable(String line){
        initSchema();
        putTable(line);
        line += System.lineSeparator();
        ServerWriter.getInstance().writeln("Schema (addTable):" + "new table");
        FileManager.addToFile(FileManager.getSchema(), line);
    }

    public static HashMap<String, ArrayList<String>> getSchema() {
        initSchema();
        return schema;
    }

    public static int getCount(String tableName){
        return schema.get(tableName).size();
    }

    public static ArrayList<String> getColumnNames(String tableName) {
        initSchema();
        ArrayList<String> columnNames = new ArrayList<>();
        int count = schema.get(tableName).size();
        for (int i=0; i< count; i++){
            columnNames.add(schema.get(tableName).get(i));
        }
        return columnNames;
    }

    public static int getColumnNumber(String name, String tableName) {
        return getColumnNames(tableName).indexOf(name);
    }

    public static Set<String> getTableNames() {
        return schema.keySet();
    }
}
