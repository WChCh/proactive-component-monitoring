/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.config;

import java.net.Socket;

import org.apache.log4j.Logger;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.filetransfer.FileTransferService;
import org.objectweb.proactive.core.util.OperatingSystem;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * All ProActive Properties
 *
 * All Java properties supported by ProActive are declared here. Each property
 * has a short Javadoc
 *
 * Provided methods must be used in place of System.(get|set)property()
 * or the ProActiveConfiguration class.
 *
 */
@PublicAPI
public enum PAProperties {

    /* ------------------------------------
     * Java Properties
     */

    /**
     * Java security policy file location
     */
    JAVA_SECURITY_POLICY("java.security.policy", PAPropertiesType.STRING, true),

    /**
     * If IPv6 is available on the operating system the default preference is to prefer an IPv4-mapped address over an IPv6 address
     */
    PREFER_IPV6_ADDRESSES("java.net.preferIPv6Addresses", PAPropertiesType.BOOLEAN, true),

    /**
     * If IPv6 is available on the operating system the underlying native socket will be an IPv6 socket. This allows Java(tm) applications to connect too, and accept connections from, both IPv4 and IPv6 hosts.
     */
    PREFER_IPV4_STACK("java.net.preferIPv4Stack", PAPropertiesType.BOOLEAN, true),

    /**
     * Indicate the Fractal provider class, to the ProActive implementation of
     * Fractal/GCM set it to org.objectweb.proactive.core.component.Fractive
     */
    FRACTAL_PROVIDER("fractal.provider", PAPropertiesType.STRING, true),

    JAVA_SECURITY_AUTH_LOGIN_CONFIG("java.security.auth.login.config", PAPropertiesType.STRING, true),

    JAVAX_XML_TRANSFORM_TRANSFORMERFACTORY("javax.xml.transform.TransformerFactory", PAPropertiesType.STRING,
            true),

    /* ------------------------------------
     *  PROACTIVE
     */

    /**
     * ProActive Configuration file location
     *
     * If set ProActive will load the configuration file at the given location.
     */
    PA_CONFIGURATION_FILE("proactive.configuration", PAPropertiesType.STRING),

    /**
     * Indicates where ProActive is installed
     *
     * Can be useful to write generic deployment descriptor by using a JavaPropertyVariable
     * to avoid hard coded path.
     *
     * Used in unit and functional tests
     */
    PA_HOME("proactive.home", PAPropertiesType.STRING),

    /**
     * Indicates what is the operating system of the local machine
     *
     * It is not redundant with "os.name" since the only valid values those from <code>OperatingSystem</code>.
     * Often users are only interested to know if the computer is running unix or windows.
     *
     * @see OperatingSystem
     */
    PA_OS("proactive.os", PAPropertiesType.STRING, true),

    /**
     * Log4j configuration file location
     *
     * If set the specified log4j configuration file is used. Otherwise the default one,
     * Embedded in the ProActive jar is used.
     */
    LOG4J("log4j.configuration", PAPropertiesType.STRING),

    /**
     * URI of the remote log collector
     * 
     */
    PA_LOG4J_COLLECTOR("proactive.log4j.collector", PAPropertiesType.STRING),

    /**
     * Qualified name of the flushing provider to use
     */
    PA_LOG4J_APPENDER_PROVIDER("proactive.log4j.appender.provider", PAPropertiesType.STRING),

    /**
     * Specifies the name of the ProActive Runtime
     *
     * By default a random name is assigned to a ProActive Runtime. This property allows
     * to choose the name of the Runtime to be able to perform lookups.
     *
     * <strong>The name must start with PA_JVM</strong>
     */
    PA_RUNTIME_NAME("proactive.runtime.name", PAPropertiesType.STRING),

    /**
     * this property should be used when one wants to start only a runtime without an additional main class
     */
    PA_RUNTIME_STAYALIVE("proactive.runtime.stayalive", PAPropertiesType.BOOLEAN),

    /**
     * Terminates the Runtime when the Runtime becomes empty
     *
     * If true, when all bodies have been terminated the ProActive Runtime will exit
     */
    PA_EXIT_ON_EMPTY("proactive.exit_on_empty", PAPropertiesType.BOOLEAN),

    /**
     * Boolean to activate automatic continuations for this runtime.
     */
    PA_FUTURE_AC("proactive.future.ac", PAPropertiesType.BOOLEAN),

    /**
     * Period of the future monitoring ping, in milliseconds
     * 
     * If set to 0, then future monitoring is disabled
     */
    PA_FUTUREMONITORING_TTM("proactive.futuremonitoring.ttm", PAPropertiesType.INTEGER),

    /**
     * Include client side calls in stack traces
     */
    PA_STACKTRACE("proactive.stack_trace", PAPropertiesType.BOOLEAN),

    /**
     * Activates the legacy SAX ProActive Descriptor parser
     *
     * To check if the new JAXP parser introduced regressions.
     */
    PA_LEGACY_PARSER("proactive.legacy.parser", PAPropertiesType.BOOLEAN),

    /* ------------------------------------
     *  NETWORK
     */

    /**
     * ProActive Communication protocol
     *
     * Suppported values are: rmi, rmissh, ibis, http
     */
    PA_COMMUNICATION_PROTOCOL("proactive.communication.protocol", PAPropertiesType.STRING),

    /**
     * ProActive Runtime Hostname (or IP Address)
     *
     * This option can be used to set manually the Runtime IP Address. Can be
     * useful when the Java networking stack return a bad IP address (example: multihomed machines).
     * 
     */
    PA_HOSTNAME("proactive.hostname", PAPropertiesType.STRING),

    /**
     * Toggle DNS resolution
     *
     * When true IP addresses are used instead of FQDNs. Can be useful with misconfigured DNS servers
     * or strange /etc/resolv.conf files. FQDNs passed by user or 3rd party tools are resolved and converted
     * into IP addresses
     *
     */
    PA_NET_USE_IP_ADDRESS("proactive.useIPaddress", PAPropertiesType.BOOLEAN),

    /** Enable or disable IPv6 
     * 
     */
    PA_NET_DISABLE_IPv6("proactive.net.disableIPv6", PAPropertiesType.BOOLEAN),

    /**
     * Toggle loopback IP address usage
     *
     * When true loopback IP address usage is avoided. Since Remote adapters contain only one
     * endpoint the right IP address must be used. This property must be set to true if a loopback
     * address is returned by the Java INET stack.
     *
     * If only a loopback address exists, it is used.
     */
    PA_NET_NOLOOPBACK("proactive.net.nolocal", PAPropertiesType.BOOLEAN),

    /**
     * Toggle Private IP address usage
     *
     * When true private IP address usage is avoided. Since Remote adapters contain only one
     * endpoint the right IP address must be used. This property must be set to true if a private
     * address is returned by the Java INET stack and this private IP is not reachable by other hosts.
     */
    PA_NET_NOPRIVATE("proactive.net.noprivate", PAPropertiesType.BOOLEAN),

    /**
     * Select the network interface
     */
    PA_NET_INTERFACE("proactive.net.interface", PAPropertiesType.STRING),

    /** Select the netmask to use (xxx.xxx.xxx.xxx/xx)
     * 
     * Does not work with IPv6 addresses
     */
    PA_NET_NETMASK("proactive.net.netmask", PAPropertiesType.STRING),

    /**
     * RMI/SSH black voodoo
     *
     * Can be used to fix broken networks (multihomed, broken DNS etc.). You probably want
     * to post on the public ProActive mailing list before using this property.
     */
    PA_NET_SECONDARYNAMES("proactive.net.secondaryNames", PAPropertiesType.STRING), SCHEMA_VALIDATION(
            "schema.validation", PAPropertiesType.BOOLEAN, true),

    /** The address of the router to use. Must be set if message routing is enabled 
     * 
     * Can be FQDN or an IP address
     */
    PA_NET_ROUTER_ADDRESS("proactive.net.router.address", PAPropertiesType.STRING),

    /** The port of the router to use. Must be set if message routing is enabled
     * 
     */
    PA_NET_ROUTER_PORT("proactive.net.router.port", PAPropertiesType.INTEGER),

    /** The Socket Factory to use by the message routing protocol
     *
     */
    PA_PAMR_SOCKET_FACTORY("proactive.communication.pamr.socketfactory", PAPropertiesType.STRING),

    /** SSL cipher suites used for RMISSL communications.
     * List of cipher suites used for RMISSL, separated by commas.
     * default is SSL_DH_anon_WITH_RC4_128_MD5. This cipher suite is used only
     * to have encrypted communications, without authentication, and works with default
     * JVM's keyStore/TrustStore
     *
     * Many others can be used. for implementing a certificate authentication...
     * see http://java.sun.com/javase/6/docs/technotes/guides/security/jsse/JSSERefGuide.html
     *
     * */
    PA_SSL_CIPHER_SUITES("proactive.ssl.cipher.suites", PAPropertiesType.STRING),

    /* ------------------------------------
     *  RMI
     */

    /**
     * Assigns a TCP port to RMI
     *
     * this property identifies the default port used by the RMI communication protocol
     */
    PA_RMI_PORT("proactive.rmi.port", PAPropertiesType.INTEGER), JAVA_RMI_SERVER_CODEBASE(
            "java.rmi.server.codebase", PAPropertiesType.STRING, true),

    /**
     * Sockets used by the RMI remote object factory connect to the remote server 
     * with a specified timeout value. A timeout of zero is interpreted as an infinite timeout. 
     * The connection will then block until established or an error occurs. 
     */
    PA_RMI_CONNECT_TIMEOUT("proactive.rmi.connect_timeout", PAPropertiesType.INTEGER),

    PA_CODEBASE("proactive.codebase", PAPropertiesType.STRING, true),

    PA_CLASSLOADING_USEHTTP("proactive.classloading.useHTTP", PAPropertiesType.BOOLEAN, false),
    /* ------------------------------------
     *  HTTP
     */

    /**
     * Assigns a TCP port to XML-HTTP
     *
     * this property identifies the default port for the xml-http protocol
     */
    PA_XMLHTTP_PORT("proactive.http.port", PAPropertiesType.INTEGER),

    /** 
     * Define a Connector to be used by Jetty
     * 
     * By default a SelectChannelConnector is used. It is well suited to handle a lot
     * of mainly idle clients workload (like coarse grained master worker). If you have a
     * few very busy client better performances can be achieved by using a SocketConnect
     * 
     * You can use a SocketConnect, a BlockingChannelConnector or a SelectChannelConnector
     * You CANNOT use a SSL connector. 
     * Click 
     * <a  href="http://docs.codehaus.org/display/JETTY/Architecture">here</a> for more
     * information on the Jetty architecture. 
     */
    PA_HTTP_JETTY_CONNECTOR("proactive.http.jetty.connector", PAPropertiesType.STRING),

    /**
     * Jetty configuration file
     * 
     * Jetty can be configured by providing a 
     * <a  href="http://docs.codehaus.org/display/JETTY/jetty.xml">jetty.xml</a>
     * file. Click 
     * <a  href="http://docs.codehaus.org/display/JETTY/Syntax+Reference">here </a>
     * for the Jetty syntax reference.
     */
    PA_HTTP_JETTY_XML("proactive.http.jetty.xml", PAPropertiesType.STRING),

    /**
     * Sockets used by the HTTP remote object factory connect to the remote server 
     * with a specified timeout value. A timeout of zero is interpreted as an infinite timeout. 
     * The connection will then block until established or an error occurs. 
     */
    PA_HTTP_CONNECT_TIMEOUT("proactive.http.connect_timeout", PAPropertiesType.INTEGER),

    /* ------------------------------------
     *  COMPONENTS
     */

    /** Timeout in seconds for parallel creation of components */
    PA_COMPONENT_CREATION_TIMEOUT("components.creation.timeout", PAPropertiesType.INTEGER),

    /** If 'true', the component framework should optimize communication between component using shortcut mechanism */
    PA_COMPONENT_USE_SHORTCUTS("proactive.components.use_shortcuts", PAPropertiesType.BOOLEAN),

    /* ------------------------------------
     *  MIGRATION
     */

    /** The class or interface of the location server to be looked up */
    PA_LOCATION_SERVER("proactive.locationserver", PAPropertiesType.STRING),

    /** The bind name of a location server, used during lookup */
    PA_LOCATION_SERVER_RMI("proactive.locationserver.rmi", PAPropertiesType.STRING),

    /** The lifetime (in seconds) of a forwarder left after a migration when using the mixed location scheme */
    PA_MIXEDLOCATION_TTL("proactive.mixedlocation.ttl", PAPropertiesType.INTEGER),

    /** If set to true, a forwarder will send an update to a location server when reaching
     * the end of its lifetime */
    PA_MIXEDLOCATION_UPDATINGFORWARDER("proactive.mixedlocation.updatingForwarder", PAPropertiesType.BOOLEAN),

    /** The maximum number of migration allowed before an object must send its new location to a location server */
    PA_MIXEDLOCATION_MAXMIGRATIONNB("proactive.mixedlocation.maxMigrationNb", PAPropertiesType.INTEGER),

    /** The maximum time (in seconds) an object can spend on a site before updating its location to a location server */
    PA_MIXEDLOCATION_MAXTIMEONSITE("proactive.mixedlocation.maxTimeOnSite", PAPropertiesType.INTEGER),

    /* ------------------------------------
     *  RMISSH
     */

    /** this property identifies the location of RMISSH key directory */
    PA_RMISSH_KEY_DIR("proactive.communication.rmissh.key_directory", PAPropertiesType.STRING),

    /** this property identifies that when using SSH tunneling, a normal connection should be tried before tunneling */
    PA_RMISSH_TRY_NORMAL_FIRST("proactive.communication.rmissh.try_normal_first", PAPropertiesType.BOOLEAN),

    /** this property identifies the SSH garbage collector period
     *
     * If set to 0, tunnels and connections are not garbage collected
     */
    PA_RMISSH_GC_PERIOD("proactive.communication.rmissh.gc_period", PAPropertiesType.INTEGER),

    /** this property identifies the maximum idle time before a SSH tunnel or a connection is garbage collected */
    PA_RMISSH_GC_IDLETIME("proactive.communication.rmissh.gc_idletime", PAPropertiesType.INTEGER),

    /** this property identifies the know hosts file location when using ssh tunneling
     *  if undefined, the default value is user.home property concatenated to SSH_TUNNELING_DEFAULT_KNOW_HOSTS
     */
    PA_RMISSH_KNOWN_HOSTS("proactive.communication.rmissh.known_hosts", PAPropertiesType.STRING),

    /** Sock connect timeout, in ms
     *
     * The timeout to be used when a SSH Tunnel is opened. 0 is interpreted
     * as an infinite timeout. This timeout is also used for plain socket when try_normal_first is set to true
     *
     * @see Socket
     */
    PA_RMISSH_CONNECT_TIMEOUT("proactive.communication.rmissh.connect_timeout", PAPropertiesType.INTEGER),

    // Not documented, temporary workaround until 4.3.0
    PA_RMISSH_REMOTE_USERNAME("proactive.communication.rmissh.username", PAPropertiesType.STRING),

    // Not documented, temporary workaround until 4.3.0
    PA_RMISSH_REMOTE_PORT("proactive.communication.rmissh.port", PAPropertiesType.INTEGER),

    /* ------------------------------------
     *  PAMR 
     */

    /**
     * Sockets used by the PAMR remote object factory connect to the remote server 
     * with a specified timeout value. A timeout of zero is interpreted as an infinite timeout. 
     * The connection will then block until established or an error occurs. 
     */
    PA_PAMR_CONNECT_TIMEOUT("proactive.communication.pamr.connect_timeout", PAPropertiesType.INTEGER),

    /* ------------------------------------
     *  PAMR over SSH
     */

    /** this property identifies the location of RMISSH key directory */
    PA_PAMRSSH_KEY_DIR("proactive.communication.pamrssh.key_directory", PAPropertiesType.STRING),

    /** this property identifies the PAMR over SSH garbage collector period
     *
     * If set to 0, tunnels and connections are not garbage collected
     */
    PA_PAMRSSH_GC_PERIOD("proactive.communication.pamrssh.gc_period", PAPropertiesType.INTEGER),

    /** this property identifies the maximum idle time before a SSH tunnel or a connection is garbage collected */
    PA_PAMRSSH_GC_IDLETIME("proactive.communication.pamrssh.gc_idletime", PAPropertiesType.INTEGER),

    /** this property identifies the know hosts file location when using ssh tunneling
     *  if undefined, the default value is user.home property concatenated to SSH_TUNNELING_DEFAULT_KNOW_HOSTS
     */
    PA_PAMRSSH_KNOWN_HOSTS("proactive.communication.pamrssh.known_hosts", PAPropertiesType.STRING),

    /** Sock connect timeout, in ms
     *
     * The timeout to be used when a SSH Tunnel is opened. 0 is interpreted
     * as an infinite timeout. This timeout is also used for plain socket when try_normal_first is set to true
     *
     * @see Socket
     */
    PA_PAMRSSH_CONNECT_TIMEOUT("proactive.communication.pamrssh.connect_timeout", PAPropertiesType.INTEGER),

    // Not documented, temporary workaround until 4.3.0
    PA_PAMRSSH_REMOTE_USERNAME("proactive.communication.pamrssh.username", PAPropertiesType.STRING),

    // Not documented, temporary workaround until 4.3.0
    PA_PAMRSSH_REMOTE_PORT("proactive.communication.pamrssh.port", PAPropertiesType.INTEGER),

    /* ------------------------------------
     *  SECURITY
     */

    /** this property indicates if a RMISecurityManager has to be instanciated*/
    PA_SECURITYMANAGER("proactive.securitymanager", PAPropertiesType.BOOLEAN),

    /** this property indicates the location of the runtime' security manager configuration file */
    PA_RUNTIME_SECURITY("proactive.runtime.security", PAPropertiesType.STRING),

    /** this property indicates the url of the security domain the runtime depends on */
    PA_RUNTIME_DOMAIN_URL("proactive.runtime.domain.url", PAPropertiesType.STRING),

    /* ------------------------------------
     *  TIMIT
     */

    /** this property indicates the list (comma separated) of the TimIt counters to activate */
    PA_TIMIT_ACTIVATION("proactive.timit.activation", PAPropertiesType.STRING),

    /* ------------------------------------
     *  MASTER/WORKER
     */

    /**
     * Master/Worker ping period in milliseconds
     *
     * The ping period is the default interval at which workers receive a ping message
     * (to check if they're alive). Default to ten seconds
     */
    PA_MASTERWORKER_PINGPERIOD("proactive.masterworker.pingperiod", PAPropertiesType.INTEGER),
    /**
     * Master/Worker compress tasks
     *
     * Parameter which decides wether tasks should be compressed when they are saved inside the task repository
     * compressing increases CPU usage on the master side, but decrease memory usage, default to false
     */
    PA_MASTERWORKER_COMPRESSTASKS("proactive.masterworker.compresstasks", PAPropertiesType.BOOLEAN),

    /* ------------------------------------
     *  DISTRIBUTED GARBAGE COLLECTOR
     */

    /** Enable the distributed garbage collector */
    PA_DGC("proactive.dgc", PAPropertiesType.BOOLEAN),

    /**
     * TimeToAlone
     * After this delay, we suppose we got a message from all our referencers.
     */
    PA_DGC_TTA("proactive.dgc.tta", PAPropertiesType.INTEGER),

    /**
     * TimeToBroadcast
     * Time is always in milliseconds. It is fundamental for this value
     * to be the same in all JVM of the distributed system, so think twice
     * before changing it.
     */
    PA_DGC_TTB("proactive.dgc.ttb", PAPropertiesType.INTEGER),

    /* ------------------------------------
     *  DISTRIBUTED DEBUGGER
     */

    /** Enable the distributed debugger */
    PA_DEBUG("proactive.debug", PAPropertiesType.BOOLEAN),

    /* ------------------------------------
     *  MESSAGE TAGGING 
     */
    /** Set the max period for LocalMemoryTag lease time */
    PA_MAX_MEMORY_TAG_LEASE("proactive.tagmemory.lease.max", PAPropertiesType.INTEGER),

    /** Set the Period of the running thread for tag memory leasing check */
    PA_MEMORY_TAG_LEASE_PERIOD("proactive.tagmemory.lease.period", PAPropertiesType.INTEGER),

    /** Enable or disable the Distributed Service ID Tag */
    PA_TAG_DSF("proactive.tag.dsf", PAPropertiesType.BOOLEAN),

    /* ------------------------------------
     *  FILE TRANSFER
     */

    /**
     * The maximum number of {@link FileTransferService} objects that can be spawned
     * on a Node to handle file transfer requests in parallel.
     */
    PA_FILETRANSFER_MAX_SERVICES("proactive.filetransfer.services_number", PAPropertiesType.INTEGER),

    /**
     * When sending a file, the maximum number of file blocks (parts) that can
     * be sent asynchronously before blocking for their arrival.
     */
    PA_FILETRANSFER_MAX_SIMULTANEOUS_BLOCKS("proactive.filetransfer.blocks_number", PAPropertiesType.INTEGER),

    /**
     * The size, in [KB], of file blocks (parts) used to send files.
     */
    PA_FILETRANSFER_MAX_BLOCK_SIZE("proactive.filetransfer.blocks_size_kb", PAPropertiesType.INTEGER),

    /**
     * The size, in [KB], of the buffers to use when reading and writing a file.
     */
    PA_FILETRANSFER_MAX_BUFFER_SIZE("proactive.filetransfer.buffer_size_kb", PAPropertiesType.INTEGER),

    // -------------- DATA SPACES

    /**
     * This property indicates an access URL to the scratch data space. If scratch is going to be
     * used on host, this property and/or {@link #PA_DATASPACES_SCRATCH_PATH} should be set.
     */
    PA_DATASPACES_SCRATCH_URL("proactive.dataspaces.scratch_url", PAPropertiesType.STRING),

    /**
     * This property indicates a location of the scratch data space. If scratch is going to be used
     * on host, this property and/or {@link #PA_DATASPACES_SCRATCH_URL} should be set.
     */
    PA_DATASPACES_SCRATCH_PATH("proactive.dataspaces.scratch_path", PAPropertiesType.STRING),

    // -------------- VFS PROVIDER

    /**
     * This property indicates how often an auto closing mechanism is started to collect and close
     * all unused streams open trough file system server interface.
     */
    PA_VFSPROVIDER_SERVER_STREAM_AUTOCLOSE_CHECKING_INTERVAL_MILLIS(
            "proactive.vfsprovider.server.stream_autoclose_checking_millis", PAPropertiesType.INTEGER),

    /**
     * This property indicates a period after that a stream is perceived as unused and therefore can
     * be closed by auto closing mechanism.
     */
    PA_VFSPROVIDER_SERVER_STREAM_OPEN_MAXIMUM_PERIOD_MILLIS(
            "proactive.vfsprovider.server.stream_open_maximum_period_millis", PAPropertiesType.INTEGER),

    // -------------- Misc

    /**
     * Indicates if a Runtime is running a functional test
     *
     * <strong>Internal use</strong>
     * This property is set to true by the functional test framework. JVM to be killed
     * after a functional test are found by using this property
     */
    PA_TEST("proactive.test", PAPropertiesType.BOOLEAN),

    /** Duration of each performance test in ms */
    PA_TEST_PERF_DURATION("proactive.test.perf.duration", PAPropertiesType.INTEGER),

    /**
     * Functional test timeout in ms
     *
     * If 0 no timeout.
     */
    PA_TEST_TIMEOUT("proactive.test.timeout", PAPropertiesType.INTEGER),

    /**
     * TODO vlegrand Describe this property
     */
    CATALINA_BASE("catalina.base", PAPropertiesType.STRING, true),

    /**
     * if true, any reference on the reified object within an outgoing request or reply is
     * replaced by a reference on the active object. This feature can be used when activating 
     * an object whose source code cannot be modified to replace the code that return <code>this</code>
     * by the reference on the active object using <code>PAActiveObject.getStubOnThis()</code>
     */
    PA_IMPLICITGETSTUBONTHIS("proactive.implicitgetstubonthis", PAPropertiesType.BOOLEAN),

    /**
     * on unix system, define the shell that the GCM deployment invokes when creating new runtimes.
     */
    PA_GCMD_UNIX_SHELL("proactive.gcmd.unix.shell", PAPropertiesType.STRING),

    /**
     * Web services framework
     *
     * Suppported values are: axis2, cxf
     */
    PA_WEBSERVICES_FRAMEWORK("proactive.webservices.framework", PAPropertiesType.STRING),

    /**
     * if true, write the bytecode of the generated stub on the disk
     * 
     */
    PA_MOP_WRITESTUBONDISK("proactive.mop.writestubondisk", PAPropertiesType.BOOLEAN),

    /**
      * Specifies the location where to write the classes generated 
      * using the mop
      */
    PA_MOP_GENERATEDCLASSES_DIR("proactive.mop.generatedclassesdir", PAPropertiesType.STRING);

    static final Logger logger = ProActiveLogger.getLogger(Loggers.CONFIGURATION);
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private String key;
    private PAPropertiesType type;
    private boolean isSystemProperty;

    PAProperties(String str, PAPropertiesType type) {
        this(str, type, false);
    }

    PAProperties(String str, PAPropertiesType type, boolean isSystemProperty) {
        this.key = str;
        this.type = type;
        this.isSystemProperty = isSystemProperty;
    }

    /**
     * Returns the key associated to this property
     * @return the key associated to this property
     */
    public String getKey() {
        return key;
    }

    public PAPropertiesType getType() {
        return type;
    }

    /**
     * Returns the value of this property
     * @return the value of this property
     */
    public String getValue() {
        return ProActiveConfiguration.getInstance().getProperty(key);
    }

    public int getValueAsInt() {
        if (type != PAPropertiesType.INTEGER) {
            RuntimeException e = new IllegalArgumentException(key +
                " is not an integer property. getValueAsInt cannot be called on this property");
            logger.error(e);
            throw e;
        }

        return Integer.parseInt(getValue());
    }

    public static void main(String[] args) {
        System.out.println(Integer.parseInt(null));
    }

    /**
     * Set the value of this property
     * @param value new value of the property
     */
    public void setValue(String value) {
        ProActiveConfiguration.getInstance().setProperty(key, value);
    }

    public void setValue(Integer i) {
        ProActiveConfiguration.getInstance().setProperty(key, i.toString());
    }

    public void setValue(Boolean bool) {
        ProActiveConfiguration.getInstance().setProperty(key, bool.toString());
    }

    @Override
    public String toString() {
        return key + "=" + getValue();
    }

    /**
     * Indicates if the property is set.
     * @return true if and only if the property has been set.
     */
    public boolean isSet() {
        return ProActiveConfiguration.getInstance().getProperty(key) != null;
    }

    /**
     * Indicates if this property is true.
     *
     * This method can only be called with boolean property. Otherwise an {@link IllegalArgumentException}
     * is thrown.
     *
     * If the value is illegal for a boolean property, then false is returned and a warning is
     * printed.
     *
     * @return true if the property is set to true.
     */
    public boolean isTrue() {
        if (type != PAPropertiesType.BOOLEAN) {
            RuntimeException e = new IllegalArgumentException(key +
                " is not a boolean property. isTrue cannot be called on this property");
            logger.error(e);
            throw e;
        }

        if (!isSet()) {
            return false;
        }

        String val = getValue();
        if (TRUE.equals(val)) {
            return true;
        }

        if (FALSE.equals(val)) {
            return false;
        }

        logger.warn(key + " is a boolean property but its value is nor " + TRUE + " nor " + FALSE + " " +
            "(" + val + "). ");
        return false;
    }

    /**
     * Returns the string to be passed on the command line
     *
     * The property surrounded by '-D' and '='
     *
     * @return the string to be passed on the command line
     */
    public String getCmdLine() {
        return "-D" + key + '=';
    }

    public boolean isBoolean() {
        return type == PAPropertiesType.BOOLEAN;
    }

    public boolean isSystemProperty() {
        return isSystemProperty;
    }

    public boolean isValid(String value) {
        switch (type) {
            case BOOLEAN:
                if (TRUE.equals(value) || FALSE.equals(value)) {
                    return true;
                }
                return false;
            case INTEGER:
                try {
                    Integer.parseInt(value);
                    return true;
                } catch (NumberFormatException e) {
                }
                return false;
            case STRING:
                return true;
            default:
                return false;
        }
    }

    static public PAProperties getProperty(String key) {
        for (PAProperties prop : PAProperties.values()) {
            if (prop.getKey().equals(key)) {
                return prop;
            }
        }
        return null;
    }

    public enum PAPropertiesType {
        STRING, INTEGER, BOOLEAN;
    }
}
