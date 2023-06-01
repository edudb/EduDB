/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.jdbc;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class EdudbDriver implements Driver {
    private static final String USER_PROPERTY = "username";
    private static final String PASSWORD_PROPERTY = "password";

    static {
        try {
            DriverManager.registerDriver(new EdudbDriver());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register driver", e);
        }
    }


    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!acceptsURL(url)) {
            return null;
        }

        ConnectionUrlInfo urlInfo = ConnectionUrlInfo.parse(url);
        String server = urlInfo.server();
        String workspace = urlInfo.workspace();
        String username = info.getProperty(USER_PROPERTY);
        String password = info.getProperty(PASSWORD_PROPERTY);

        return new EdudbConnection(server, workspace, username, password);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return ConnectionUrlInfo.isUrlSupported(url);
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
