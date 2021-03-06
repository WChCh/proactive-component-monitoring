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
package org.objectweb.proactive.examples.jmx.remote.management.client.entities;

import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

import javax.management.ObjectName;

import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.examples.jmx.remote.management.events.EntitiesEventListener;
import org.objectweb.proactive.examples.jmx.remote.management.events.EntitiesEventManager;
import org.objectweb.proactive.examples.jmx.remote.management.exceptions.GroupAlreadyExistsException;
import org.objectweb.proactive.examples.jmx.remote.management.status.Status;
import org.objectweb.proactive.examples.jmx.remote.management.utils.Constants;


/**
 * This entities represents a Remote Gateway Group.
 *
 * @author The ProActive Team
 *
 */
public class RemoteGroup extends ManageableEntity implements Serializable, EntitiesEventListener,
        Transactionnable {

    /**
     *
     */
    private ManageableEntity entities;
    private Group<ManageableEntity> gEntities;
    private String name;
    private String description = "";
    private RemoteGroup parent;
    private int totalGateways;
    private int nbConnected;
    private Vector<RemoteGateway> gateways = new Vector<RemoteGateway>();
    public Vector<RemoteGroup> groups = new Vector<RemoteGroup>();

    public RemoteGroup(String name) throws GroupAlreadyExistsException {
        this(name, null);
    }

    public RemoteGroup(String name, RemoteGroup parent) throws GroupAlreadyExistsException {
        this.name = name;
        this.parent = parent;
        if ((parent != null) && this.parent.hasGroup(name)) {
            throw new GroupAlreadyExistsException();
        }
        try {
            this.entities = (ManageableEntity) PAGroup.newGroup(ManageableEntity.class.getName());
            this.gEntities = PAGroup.getGroup(this.entities);
        } catch (ClassNotReifiableException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() throws IOException {
        ((RemoteEntity) this.entities).connect();
    }

    @Override
    public synchronized void addEntity(ManageableEntity entity) {
        try {
            this.gEntities.add(entity);
            if (entity instanceof RemoteGroup) {
                groups.addElement((RemoteGroup) entity);
                this.totalGateways += ((RemoteGroup) entity).getTotalGateways();
            } else if (entity instanceof RemoteGateway) {
                gateways.addElement((RemoteGateway) entity);
                this.totalGateways++;
                EntitiesEventManager.getInstance().newEvent(this,
                        EntitiesEventManager.GATEWAY_ADDED_IN_A_GROUP);
            }
            addName(entity.getName());
            EntitiesEventManager.getInstance().subscribe(this, entity);
            EntitiesEventManager.getInstance().newEvent(this, EntitiesEventManager.ENTITY_ADDED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object[] getChildren() {
        return this.gEntities.toArray();
    }

    @Override
    public ManageableEntity getParent() {
        return this.parent;
    }

    @Override
    public boolean hasChildren() {
        return this.gEntities.size() > 0;
    }

    @Override
    public void remove() {
        this.entities.remove();
        this.gEntities.clear();

        this.parent.remove(this);
        this.parent.removeEntity(this);
        EntitiesEventManager.getInstance().newEvent(this, EntitiesEventManager.GROUP_REMOVED);
    }

    @Override
    public void removeEntity(ManageableEntity entity) {
        if (entity instanceof RemoteGroup) {
            this.groups.removeElement(entity);
            this.totalGateways -= ((RemoteGroup) entity).getTotalGateways();
            this.nbConnected -= ((RemoteGroup) entity).getNbConnected();
        } else if (entity instanceof RemoteGateway) {
            this.gateways.removeElement(entity);
            this.totalGateways--;
            this.nbConnected--;
        }
        this.gEntities.remove(entity);
    }

    @Override
    public Status executeCommand(String command) {
        return this.entities.executeCommand(command);
    }

    public Status installBundle(String location) throws IOException {
        return this.entities.executeCommand("install " + location);
    }

    @Override
    public void refresh() {
        this.entities.refresh();
    }

    @Override
    public String toString() {
        return this.name + "[  " + this.nbConnected + " / " + this.totalGateways + " ] ";
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) throws GroupAlreadyExistsException {
        if ((this.parent != null) && this.parent.hasGroup(name)) {
            throw new GroupAlreadyExistsException();
        }
        this.parent.changeName(this, name);
        this.name = name;

        EntitiesEventManager.getInstance().newEvent(this, EntitiesEventManager.GROUP_UPDATED);
    }

    public ManageableEntity getEntities() {
        return entities;
    }

    public Group<ManageableEntity> getGEntities() {
        return gEntities;
    }

    public boolean isRoot() {
        return (this.name.equals(Constants.ROOT)) && (this.parent == null);
    }

    public boolean hasGroup(String name) {
        return contains(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Status cancelTransaction() {
        return ((Transactionnable) this.entities).cancelTransaction();
    }

    @Override
    public Status commitTransaction() {
        return ((Transactionnable) this.entities).commitTransaction();
    }

    @Override
    public Status openTransaction() {
        try {
            return this.entities.openTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ProActiveConnection getConnection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ObjectName getObjectName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getUrl() {
        // TODO Auto-generated method stub
        return null;
    }

    public void handleEntityEvent(Object source, String message) {
        if (message.equals(EntitiesEventManager.GATEWAY_CONNECTED)) {
            this.nbConnected++;
            EntitiesEventManager.getInstance().newEvent(this,
                    EntitiesEventManager.GATEWAYS_IN_GROUP_CONNECTED);
        } else if (message.equals(EntitiesEventManager.GATEWAYS_IN_GROUP_CONNECTED)) {
            updateConnectedGateways(((RemoteGroup) source).getNbConnected());
        } else if (message.equals(EntitiesEventManager.GATEWAY_ADDED_IN_A_GROUP)) {
            updateTotalGateways(((RemoteGroup) source).getTotalGateways());
        }
    }

    public int getNbConnected() {
        return this.nbConnected;
    }

    public int getTotalGateways() {
        return this.totalGateways;
    }

    private void updateTotalGateways(int toAdd) {
        this.totalGateways += toAdd;
        EntitiesEventManager.getInstance().newEvent(this, EntitiesEventManager.GATEWAY_ADDED_IN_A_GROUP);
    }

    private void updateConnectedGateways(int toAdd) {
        this.nbConnected += toAdd;
        EntitiesEventManager.getInstance().newEvent(this, EntitiesEventManager.GATEWAYS_IN_GROUP_CONNECTED);
    }

    public boolean noneConnected() {
        return this.nbConnected == 0;
    }

    public boolean allConnected() {
        return this.totalGateways == this.nbConnected;
    }
}
