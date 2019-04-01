/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.master.executor;

import net.edudb.engine.Utility;
import net.edudb.master.MasterWriter;
import net.edudb.meta_manager.MetaManager;
import net.edudb.response.Response;

import java.util.regex.Matcher;

/**
 * Handles the initialization of meta data tables in the meta data
 * server.
 *
 * @author FadySameh
 *
 */
public class InitializeMetaDataExecutor implements MasterExecutorChain {

    private MasterExecutorChain nextElement;
    private String regex = "init\\s+metadata";

    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    public void execute(String string) {
//        Matcher matcher = Utility.getMatcher(string, regex);
//        if (matcher.matches()) {
//            try {
//               // MetaManager.getInstance().initializeTables();
//                MasterWriter.getInstance().write(new Response("Meta data created successfully"));
//            } catch (InterruptedException e) {
//
//            }
//        } else {
//            nextElement.execute(string);
//        }
    }
}
