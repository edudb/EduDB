package net.edudb.master.executor;

import net.edudb.meta_manager.MetaManager;

/**
 * This class is just for testing the interface implemented to interact
 * with the meta/worker databases
 *
 * @author Fady Sameh
 */
public class ForwardToMeta implements MasterExecutorChain{

    private MasterExecutorChain nextElement;
    @Override
    public void setNextElementInChain(MasterExecutorChain chainElement) {
        this.nextElement = chainElement;
    }

    @Override
    public void execute(String string) {
        MetaManager metaDAO = MetaManager.getInstance();
        String s = metaDAO.forwardCommand(string);
    }
}
