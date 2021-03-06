/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.process;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.descriptor.services.UniversalService;
import org.objectweb.proactive.core.process.filetransfer.FileTransferWorkShop;
import org.objectweb.proactive.core.util.RemoteProcessMessageLogger;


/**
 * This class contains a list of ExternalProcess processes that have to
 * be executed sequentially
 * @author The ProActive Team
 * @version 1.0, 01 Dec 2005
 * @since ProActive 3.0
 *
 */
public abstract class AbstractSequentialListProcessDecorator implements ExternalProcessDecorator {
    boolean isFirstElementService = false;

    //Array of processes
    protected List<Object> processes;

    // position of the next process to return  
    protected int currentProcessRank = 0;

    public AbstractSequentialListProcessDecorator() {
        processes = new ArrayList<Object>();
    }

    /**
     * Add a process to the processes queue
     * @param process
     */
    public void addProcessToList(ExternalProcess process) {
        this.processes.add(process);
    }

    /**
     * Add a service to the processes queue
     * @param service
     */
    public void addServiceToList(UniversalService service) {
        this.processes.add(service);
    }

    /**
     * Add a process to the processes queue at index rank
     * @param rank
     * @param process
     */
    public void addProcessToList(int rank, ExternalProcess process) {
        this.processes.add(rank, process);
    }

    /**
     * Add a service to the processes queue at index rank
     * @param rank
     * @param service
     */
    public void addServiceToList(int rank, UniversalService service) {
        this.processes.add(rank, service);
    }

    /**
     * Return the first process to be launched and increase current rank
     * @return ExternalProcess
     */
    public ExternalProcess getFirstProcess() {
        currentProcessRank++;
        return (ExternalProcess) processes.get(0);
    }

    /**
     * Return the first service to be launched and increase current rank
     * @return UniversalService
     */
    public UniversalService getFirstService() {
        currentProcessRank++;
        return (UniversalService) processes.get(0);
    }

    /**
     * Return the next process to be launched and increase current rank
     * @return ExternalProcess
     */
    public ExternalProcess getNextProcess() {
        ExternalProcess res = null;
        if (currentProcessRank < processes.size()) {
            res = (ExternalProcess) processes.get(currentProcessRank);
            currentProcessRank++;
        }
        return res;
    }

    //
    //--------------------------Implements ExternalProcessDecorator----------------------

    /**
     * @see org.objectweb.proactive.core.process.ExternalProcessDecorator#getTargetProcess()
     */
    public ExternalProcess getTargetProcess() {
        return ((ExternalProcessDecorator) processes.get(currentProcessRank)).getTargetProcess();
    }

    /**
     * @see org.objectweb.proactive.core.process.ExternalProcessDecorator#setTargetProcess(org.objectweb.proactive.core.process.ExternalProcess)
     */
    public void setTargetProcess(ExternalProcess targetProcess) {
        ((ExternalProcessDecorator) processes.get(currentProcessRank)).setTargetProcess(targetProcess);
    }

    /**
     * @see org.objectweb.proactive.core.process.ExternalProcessDecorator#getCompositionType()
     */
    public int getCompositionType() {
        return ((ExternalProcess) processes.get(currentProcessRank)).getCompositionType();
    }

    /**
     * @see org.objectweb.proactive.core.process.ExternalProcessDecorator#setCompositionType(int)
     */
    public void setCompositionType(int compositionType) {
        ((ExternalProcessDecorator) processes.get(currentProcessRank)).setCompositionType(compositionType);
    }

    public FileTransferWorkShop getFileTransferWorkShopRetrieve() {

        /* TODO Check if this is the correct place
         * implement this. Then implement it
         */
        return null;
    }

    public FileTransferWorkShop getFileTransferWorkShopDeploy() {

        /* TODO Check if this is the correct place
         * implement this. Then implement it
         */
        return null;
    }

    public void startFileTransfer() {

        /* TODO Check if this is the correct place
         * implement this. Then implement it
         */
    }

    public boolean isRequiredFileTransferDeployOnNodeCreation() {

        /* TODO Check if this is the correct place
         * implement this. Then implement it
         */
        return false;
    }

    /**
     * @see org.objectweb.proactive.core.process.ExternalProcess#closeStream()
     */
    public void closeStream() {
        ((ExternalProcess) processes.get(currentProcessRank)).closeStream();
    }

    /**
     * @see org.objectweb.proactive.core.process.ExternalProcess#getInputMessageLogger()
     */
    public RemoteProcessMessageLogger getInputMessageLogger() {
        return ((ExternalProcess) processes.get(currentProcessRank)).getInputMessageLogger();
    }

    /**
     * @see org.objectweb.proactive.core.process.ExternalProcess#getErrorMessageLogger()
     */
    public RemoteProcessMessageLogger getErrorMessageLogger() {
        return ((ExternalProcess) processes.get(currentProcessRank)).getErrorMessageLogger();
    }

    /**
     * @see org.objectweb.proactive.core.process.ExternalProcess#getOutputMessageSink()
     */
    public MessageSink getOutputMessageSink() {
        return ((ExternalProcess) processes.get(currentProcessRank)).getOutputMessageSink();
    }

    /**
     * @see org.objectweb.proactive.core.process.ExternalProcess#setInputMessageLogger(org.objectweb.proactive.core.util.RemoteProcessMessageLogger)
     */
    public void setInputMessageLogger(RemoteProcessMessageLogger inputMessageLogger) {
        for (int i = 0; i < processes.size(); i++) {
            ((ExternalProcess) processes.get(i)).setInputMessageLogger(inputMessageLogger);
        }
    }

    /**
     * @see org.objectweb.proactive.core.process.ExternalProcess#setErrorMessageLogger(org.objectweb.proactive.core.util.RemoteProcessMessageLogger)
     */
    public void setErrorMessageLogger(RemoteProcessMessageLogger errorMessageLogger) {
        for (int i = 0; i < processes.size(); i++) {
            ((ExternalProcess) processes.get(i)).setErrorMessageLogger(errorMessageLogger);
        }
    }

    /**
     * @see org.objectweb.proactive.core.process.ExternalProcess#setOutputMessageSink(org.objectweb.proactive.core.process.MessageSink)
     */
    public void setOutputMessageSink(MessageSink outputMessageSink) {
        for (int i = 0; i < processes.size(); i++) {
            ((ExternalProcess) processes.get(i)).setOutputMessageSink(outputMessageSink);
        }
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#getEnvironment()
     */
    public String[] getEnvironment() {
        return ((ExternalProcess) processes.get(currentProcessRank)).getEnvironment();
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#setEnvironment(java.lang.String[])
     */
    public void setEnvironment(String[] environment) {
        ((ExternalProcess) processes.get(currentProcessRank)).setEnvironment(environment);
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#getUsername()
     */
    public String getUsername() {
        return ((ExternalProcess) processes.get(currentProcessRank)).getUsername();
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#setUsername(java.lang.String)
     */
    public void setUsername(String username) {
        ((ExternalProcess) processes.get(currentProcessRank)).setUsername(username);
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#getCommand()
     */
    public String getCommand() {
        return null;
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#getProcessId()
     */
    public String getProcessId() {
        return "ps";
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#getNodeNumber()
     */
    public int getNodeNumber() {
        return ((ExternalProcessDecorator) processes.get(currentProcessRank)).getNodeNumber();
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#getFinalProcess()
     */
    public UniversalProcess getFinalProcess() {
        return ((ExternalProcessDecorator) processes.get(currentProcessRank)).getFinalProcess();
    }

    public List<Object> getListProcess() {
        return this.processes;
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#stopProcess()
     */
    public void stopProcess() {
        ((ExternalProcess) processes.get(currentProcessRank)).stopProcess();
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#waitFor()
     */
    public int waitFor() throws InterruptedException {
        int status = 0;
        status = ((ExternalProcess) processes.get(currentProcessRank)).waitFor();
        return status;
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#isStarted()
     */
    public boolean isStarted() {
        boolean started = true;
        started = ((ExternalProcess) processes.get(currentProcessRank)).isStarted();
        return started;
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#isFinished()
     */
    public boolean isFinished() {
        boolean finished = true;
        finished = ((ExternalProcess) processes.get(currentProcessRank)).isFinished();
        return finished;
    }

    public boolean isSequential() {
        return true;
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#setCommandPath(java.lang.String)
     */
    public void setCommandPath(String path) {
        ((ExternalProcess) processes.get(currentProcessRank)).setCommandPath(path);
    }

    /**
     * @see org.objectweb.proactive.core.process.UniversalProcess#getCommandPath()
     */
    public String getCommandPath() {
        return ((ExternalProcess) processes.get(currentProcessRank)).getCommandPath();
    }

    //
    //-----------------protected methods----------------------------------------
    //
    protected abstract ExternalProcess createProcess();

    public int exitValue() throws IllegalThreadStateException {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setStarted(boolean isStarted) {
        // TODO Auto-generated method stub
    }

    public void setFinished(boolean isStarted) {
        // TODO Auto-generated method stub
    }

    public boolean isFirstElementIsService() {
        return isFirstElementService;
    }

    public void setFirstElementIsService(boolean isFirstElementIsService) {
        this.isFirstElementService = isFirstElementIsService;
    }
}
