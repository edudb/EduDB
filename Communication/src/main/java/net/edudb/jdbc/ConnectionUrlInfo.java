/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.jdbc;

import hu.webarticum.regexbee.Bee;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectionUrlInfo {

    public static final String URL_PREFIX = "jdbc:edudb://";
    private static final String SERVER_GROUPNAME = "server";
    private static final String WORKSPACE_GROUPNAME = "workspace";
    private static final Pattern URL_PATTERN = Bee
            .then(Bee.BEGIN)
            .then(Bee.fixed(URL_PREFIX))
            .then(Bee.checked("[^:/]+").as(SERVER_GROUPNAME))
            .then(Bee.fixedChar(':')
                    .then(Bee.checked("[^:/\\?]+").as(WORKSPACE_GROUPNAME)).optional())
            .then(Bee.END)
            .toPattern();


    private final String server;
    private final String workspace;

    public static boolean isUrlSupported(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        return matcher.find();
    }


    private ConnectionUrlInfo(String server, String workspace) {
        this.server = server;
        this.workspace = workspace;
    }


    public static ConnectionUrlInfo parse(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid connection url");
        }

        String server = matcher.group(SERVER_GROUPNAME);
        String workspace = matcher.group(WORKSPACE_GROUPNAME);

        return new ConnectionUrlInfo(server, workspace);
    }

    public String server() {
        return server;
    }

    public String workspace() {
        return workspace;
    }

    @Override
    public String toString() {
        return "ConnectionUrlInfo{" +
                "server='" + server + '\'' +
                "workspace='" + workspace + '\'' +
                '}';
    }
}
