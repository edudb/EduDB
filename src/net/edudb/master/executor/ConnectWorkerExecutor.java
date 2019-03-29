package net.edudb.master.executor;

import net.edudb.engine.Utility;
import net.edudb.workers_manager.WorkersManager;

import java.util.regex.Matcher;

public class ConnectWorkerExecutor implements MasterExecutorChain {

    private MasterExecutorChain nextElement;
    private String regex = "connect\\s+worker";

    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public void execute(String s) {
        Matcher matcher = Utility.getMatcher(s, regex);
        if (matcher.matches()) {
            WorkersManager.getInstance().connect("localhost", 9999);
        } else {
            nextElement.execute(s);
        }
    }
}
