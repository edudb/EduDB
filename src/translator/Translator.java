/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package translator;

import adipe.translate.Queries;
import adipe.translate.TranslationException;
import net.edudb.condition.Condition;
import net.edudb.condition.ConditionFactory;
import net.edudb.condition.NullCondition;
import net.edudb.data_type.DataType;
import net.edudb.data_type.DataTypeFactory;
import net.edudb.engine.Utility;
import net.edudb.exception.InvalidTypeValueException;
import net.edudb.master.MasterWriter;
import net.edudb.metadata_buffer.MetadataBuffer;
import net.edudb.response.Response;
import ra.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.regex.Matcher;

public class Translator {

    public String translate(String statement) {
        HashMap<String, ArrayList<String>> schema = new HashMap<>();
        Hashtable<String, Hashtable<String, DataType>> tables = MetadataBuffer.getInstance().getTables();

        for (Hashtable<String, DataType> table: tables.values()) {
            ArrayList<String> columnNames = new ArrayList<>();

            String metadata = table.get("metadata").toString();
            String[] metadataArray = metadata.split(" ");

            for ( int i = 0; i < metadataArray.length; i+=2)
                columnNames.add(metadataArray[i]);

            schema.put(table.get("name").toString(), columnNames);
        }

        try {
            Term term = Queries.getRaOf(adipe.translate.ra.Schema.create(schema), statement);
            return term.toString();
        } catch (RuntimeException | TranslationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String regex = "\\AFilter\\((.+)\\,\"(.+)\"\\)\\z";

    public ArrayList<Condition> getDistributionCondition(String metadata, String distributionColumn, String ra) {
        Matcher matcher = Utility.getMatcher(ra, regex);
        if (matcher.matches()) {

            String[] metadataArray = metadata.split(" ");
            String[] columnTypes = new String[metadataArray.length/2];

            int distributionColumnIndex = -1;
            for (int i = 0; i < metadataArray.length; i+=2) {
                if (metadataArray[i].equals(distributionColumn))
                    distributionColumnIndex = i / 2 + 1;
                columnTypes[i / 2] = metadataArray[i + 1];
            }

            return getDistributionCondition(columnTypes, distributionColumnIndex, matcher.group(2));
        }
        return null;
    }

    /**
     * Matches string of the format: #{number}={number}. e.g. #1=2
     */
    private String genericExpr = "(?:\\#\\d+\\<?\\>?\\=?#?.+)";
    /**
     * Matches string of the format: AND({anything}) or OR({anything})
     */
    private String binaryExpr = "(?:(?:AND|OR)\\(.*\\))";
    /**
     * Matches string of the format: #{number}={number} or (AND({anything}) or
     * OR({anything}))
     */
    private String argument = "(?:" + genericExpr + "|" + binaryExpr + ")";
    /**
     * Matches and captures string of the format: #{number}={number}. e.g. #1=2
     */
    private String capturedConstantExpr = "\\#(\\d+)(\\<?\\>?\\=?)(.+)";
    /**
     * Matches and captures string of the format: #{number}={number}. e.g. #1=2
     */
    private String capturedColumnExpr = "\\#(\\d+)(\\<?\\>?\\=?)#(\\d+)";
    /**
     * Matches string of the format: AND(genericExpr or binaryExpr, genericExpr
     * or binaryExpr) or OR(genericExpr or binaryExpr, genericExpr or
     * binaryExpr)
     *
     * e.g. AND(OR(#1=3,#4=1),#2=44)
     */
    private String capturedBinaryExpr = "(AND|OR)\\(" + "(" + argument + ")" + "," + "(" + argument + ")" + "\\)";

    private ArrayList<Condition> getDistributionCondition(String[] columnTypes, int distributionIndex, String ra) {
        System.out.println("Current ra: " + ra);
        Matcher constantExpression = Utility.getMatcher(ra, capturedConstantExpr);
        Matcher columnExpression = Utility.getMatcher(ra, capturedColumnExpr);
        Matcher binaryExpression = Utility.getMatcher(ra, capturedBinaryExpr);

        if (constantExpression.matches()) {
            ArrayList<Condition> result = new ArrayList<>();
            result.add(getConstantCondition(columnTypes, distributionIndex, constantExpression));
            return result;
        }
        return null;
    }

    private Condition getConstantCondition(String[] columnTypes, int distributionIndex, Matcher matcher) {
        int columnOrder = Integer.parseInt(matcher.group(1));
        String columnType = columnTypes[columnOrder- 1];

        String stringData = matcher.group(3).replace("\\\"", "");

        DataTypeFactory dataTypeFactory = new DataTypeFactory();
        DataType data;

        try {
            data = dataTypeFactory.makeType(columnType, stringData);
        } catch (InvalidTypeValueException e) {
            MasterWriter.getInstance().write(new Response(e.getMessage()));
            return null;
        }

        if (distributionIndex != columnOrder)
            return new NullCondition();

        String operator = matcher.group(2);

        ConditionFactory conditionFactory = new ConditionFactory();

        return conditionFactory.makeCondition(operator, data);
    }
}
