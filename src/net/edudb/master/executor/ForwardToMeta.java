package net.edudb.master.executor;

import net.edudb.master.MasterWriter;
import net.edudb.meta_manager.MetaManager;
import net.edudb.response.Response;

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
        Response response = metaDAO.forwardCommand(string);

        MasterWriter.getInstance().write(response);

    }
}
