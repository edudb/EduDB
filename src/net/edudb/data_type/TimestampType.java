/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.data_type;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import net.edudb.engine.Utility;
import net.edudb.exception.InvalidTypeValueException;

/**
 * 
 * @author Ahmed Abdul Badie
 *
 */
public class TimestampType extends DataType implements Serializable {
	private Date timestamp;

	private static final long serialVersionUID = 8410489057933198854L;

	public TimestampType(Date dateTime) {
		this.timestamp = dateTime;
	}

	/**
	 * Parses a date passed as a string and returns its value as a date.
	 * 
	 * @param string
	 *            The data to parse.
	 * @return The parsed date iff the value is valid.
	 * @throws InvalidTypeValueException
	 */
	public static Date parseDate(String string) throws InvalidTypeValueException {
		/**
		 * Matches strings of the form:<br><br>
		 * <b>YYYY-MM-DD HH:MM:SS</b>
		 */
		Matcher matcher = Utility.getMatcher(string, "\\A\\d{4}\\-\\d{2}\\-\\d{2}\\s\\d{2}\\:\\d{2}\\:\\d{2}\\z");
		if (matcher.matches()) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				return dateFormat.parse(string);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		throw new InvalidTypeValueException("The value must be of the format YYYY-MM-DD HH:MM:SS");
	}

	@Override
	public double diff(DataType dataType) {
		return -1;
	}

	@Override
	public int compareTo(DataType dataType) {
		TimestampType type = (TimestampType) dataType;
		return timestamp.compareTo(type.timestamp);
	}

	@Override
	public String toString() {
		return timestamp.toString();
	}

}
