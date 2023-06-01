/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.meta_manager;

import net.edudb.structure.Record;

import java.util.ArrayList;

/**
 * An interface, that implements the Repository design pattern, that
 * is used to interact with the database storing the cluster's meta
 * data.
 *
 * @author Fady Sameh
 *
 */
public interface MetaDAO {

    /**
     *
     * @param tableName
     * The name of the table you want to get the records from
     *
     * @return
     * ArrayList of records from the table
     */
    ArrayList<Record> getAll(String tableName);

    void writeTable(String tableName, String tableMetadata);

    void editTable(String tableName, String distributionMethod, String distributionColumn, int shardNumber);

    void deleteTable(String tableName);

    void writeShard(String host, int port, String table, int id, String minValue, String maxValue);

    void deleteShards(String tableName);

    void writeWorker(String host, int port);
}
