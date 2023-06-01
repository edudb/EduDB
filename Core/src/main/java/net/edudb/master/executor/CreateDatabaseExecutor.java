/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.master.executor;

import net.edudb.engine.Utility;
import net.edudb.meta_manager.MetaManager;
import java.util.regex.Matcher;

/**
 * This executor is responsible for creating a database
 *
 * @author Fady Sameh
 */
public class CreateDatabaseExecutor implements MasterExecutorChain {

    private MasterExecutorChain nextElement;

    private final String regex = "\\A(?:(?i)create)\\s+(?:(?i)database)\\s+(\\D\\w*)\\s*;?\\z";

    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public void execute(String string) {
        if (string.toLowerCase().startsWith("create database")) {
            Matcher matcher = Utility.getMatcher(string, regex);
            if (matcher.matches()) {
                MetaManager.getInstance().createDatabase(matcher.group(1));

                //return;
            }
        }
        else
            nextElement.execute(string);
    }
}
