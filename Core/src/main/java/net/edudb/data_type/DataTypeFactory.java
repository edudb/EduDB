/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.data_type;

import net.edudb.exception.InvalidTypeValueException;

/**
 * A factory that creates data types supported by EduDB.
 *
 * @author Ahmed Abdul Badie
 */
public class DataTypeFactory {

    /**
     * Creates an instance of a supported data type as an object.
     *
     * @param typeName The name of the type.
     * @param value    The value of the type.
     * @return The created data type.
     */
    public DataType makeType(String typeName, String value) throws InvalidTypeValueException {
        String val = value.replace("'", "");
        switch (typeName.toLowerCase()) {
            case "bool":
            case "boolean":

                return new BooleanType(BooleanType.parseBoolean(val));

            case "decimal":
                try {
                    return new DecimalType(Double.parseDouble(val));
                } catch (Exception e) {
                    throw new InvalidTypeValueException("Value '" + value + "' is not a decimal");
                }
            case "integer":
                try {
                    return new IntegerType(Integer.parseInt(val));
                } catch (Exception e) {
                    throw new InvalidTypeValueException("Value '" + value + "' is not an integer");
                }
            case "timestamp":
            case "datetime":
                return new TimestampType(val);

            case "varchar":
                return new VarCharType(val);
            default:
                return null;
        }

    }

}
