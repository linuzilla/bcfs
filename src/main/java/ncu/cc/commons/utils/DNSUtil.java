package ncu.cc.commons.utils;

import com.sun.jndi.dns.DnsContextFactory;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public class DNSUtil {
    public static final String NAMING_FACTORY_INITIAL = "java.naming.factory.initial";
    public static final Class<?> DNS_CONTEXT_FACTORY_CLASS = DnsContextFactory.class;
    public static final String MX_RECORD = "MX";
    public static final String A_RECORD = "A";
    private static Hashtable<String, String> env = new Hashtable<>();

    static {
        env.put(NAMING_FACTORY_INITIAL, DNS_CONTEXT_FACTORY_CLASS.getName());
    }

    public static boolean validEmailDomain(String hostName) throws NamingException {
        DirContext ictx = new InitialDirContext(env);

        Attributes attrs = ictx.getAttributes(hostName, new String[]{MX_RECORD});
        Attribute attr = attrs.get(MX_RECORD);

        if ((attr == null) || (attr.size() == 0)) {
            attrs = ictx.getAttributes(hostName, new String[]{A_RECORD});
            attr = attrs.get(A_RECORD);
            if (attr == null) {
                StackTraceUtil.print1("No match for name '" + hostName + "'");
                return false;
            } else {
                StackTraceUtil.print1("A record found: " + attr.toString());
                return true;
            }
        } else {
            StackTraceUtil.print1("MX record found: " + attr.toString());
            return true;
        }
    }
}
