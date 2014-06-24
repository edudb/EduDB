/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package DBStructure;

import statistics.Schema;

import java.util.HashMap;

/**
 * Created by mohamed on 4/11/14.
 */
public class DataManager {
    private static HashMap<String, DBTable > tables;
    private static boolean initialized;

    public static void addTable(DBTable table){
        init();// TODO init
        tables.put(table.getTableName(), table);
    }

    public static void size(){
        System.out.println((tables == null )? "null": tables.size());
    }

    public static DBTable getTable(String tableName){
        init();
        if( Schema.chekTableExists(tableName) ){
            Object o = tables.get(tableName);
            if(o != null){
                return (DBTable) o;
            }else{//
                DBTable table = new DBTable(tableName);
                tables.put(tableName, table);
                return table;
            }
        }else{//table doesn't exist
            System.out.println("table " + tableName + " is not in schema");
            return null;
        }
    }

    private static void init() {
        if(!initialized){
            tables = new HashMap<>();
        }
        initialized = true;
    }

}
