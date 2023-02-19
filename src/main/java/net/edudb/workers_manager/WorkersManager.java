/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/


package net.edudb.workers_manager;

import net.edudb.master.MasterWriter;
import net.edudb.meta_manager.MetaDAO;
import net.edudb.meta_manager.MetaManager;
import net.edudb.metadata_buffer.MetadataBuffer;
import net.edudb.response.Response;
import net.edudb.worker_manager.WorkerManager;

import java.util.Hashtable;

/**
 * A singleton that handles the connections to all the workers
 *
 * @author Fady Sameh
 */
public class WorkersManager {

    private static final WorkersManager instance = new WorkersManager();

    private final Hashtable<String, WorkerManager> workers = new Hashtable<String, WorkerManager>();

    private WorkersManager() {}

    public Hashtable<String, WorkerManager> getWorkers() { return workers; }

    public static WorkersManager getInstance() { return instance; }

    public void connect(String host, Integer port) {
        String workerIdentifier = host + ":" + port;

        if (workers.get(workerIdentifier) == null
            || !workers.get(workerIdentifier).isConnected()) {
            WorkerManager newWorkerManager =  new WorkerManager(port, host);
            new Thread(newWorkerManager).start();
        }
        else  {
            MasterWriter.getInstance().write(new Response("Worker " + host + ":" + port + " is already connected"));
        }
    }

    public void registerWorker(WorkerManager workerManager) {
        String workerIdentifier = workerManager.getHost() + ":" + workerManager.getPort();
        workers.put(workerIdentifier, workerManager);

        MasterWriter.getInstance().write(new Response("Worker at " + workerIdentifier
                + " successfully connected"));

        if (MetadataBuffer.getInstance().getWorkers().get(workerIdentifier) == null) {
            System.out.println("worker " + workerIdentifier + " should be registered");
            MetaDAO metaDAO = MetaManager.getInstance();
            metaDAO.writeWorker(workerManager.getHost(), workerManager.getPort());
        }

    }

    public void removeWorker(WorkerManager workerManager) {
        String workerIdentifier = workerManager.getHost() + ":" + workerManager.getPort();
        workers.remove(workerIdentifier);
    }

    public void closeDatabase() {
        for(WorkerManager worker: workers.values()) {
            worker.closeDatabase();
        }

        workers.clear();
        System.out.println("workers cleared in manager: " + workers.isEmpty());
    }
}
