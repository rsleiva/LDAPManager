import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

public class LDAPManager {

    public static void main(String[] args) {
        String URL_LDAP="";
        String USUARIO_LDAP="";
        String PASSWORD_LDAP="";
        String USER_SEARCH_FILTER="";
        String USER_SEARCH_BASE="";
        String GROUP_SEARCH_BASE="";

        int s=3;
        
        switch (s) {
            case 1:
                //PRO GELEC
                URL_LDAP = "ldap://192.168.145.50:389/DC=pro,DC=edenor";
                USUARIO_LDAP = "CN=SVC_consulta_ot,OU=Cuentas de Servicio,DC=pro,DC=edenor";
                PASSWORD_LDAP = "Edenor2010";
                USER_SEARCH_FILTER = "(&(cn={0})(|(memberOf=CN=APP_GELEC_SUPERVISOR,OU=GELEC,OU=FIM-SG,OU=Grupos,DC=pro,DC=edenor)(memberOf=CN=APP_GELEC_CONSULTA,OU=GELEC,OU=FIM-SG,OU=Grupos,DC=pro,DC=edenor)(memberOf=CN=APP_GELEC_ADMINISTRADOR,OU=GELEC,OU=FIM-SG,OU=Grupos,DC=pro,DC=edenor)))";
                USER_SEARCH_BASE = "ou=Edificios";
                GROUP_SEARCH_BASE = "ou=GELEC,OU=FIM-SG,OU=Grupos";
                break;
            case 2:
                //QA PORTAL RECLAMOS
                URL_LDAP = "ldap://192.168.146.214:389/DC=qa,DC=edenor";
                USUARIO_LDAP = "CN=SVC_consulta_ot,OU=Cuentas de Servicio,DC=qa,DC=edenor";
                PASSWORD_LDAP = "edenor2020";
                USER_SEARCH_FILTER = "(&(cn={0})(|(memberOf=CN=APP_PortalENRE_Reclamos_Operador,OU=PortalENRE_Reclamos,OU=Aplicaciones,OU=Grupos,DC=qa,DC=edenor)(memberOf=CN=APP_PortalENRE_Reclamos_Consulta,OU=PortalENRE_Reclamos,OU=Aplicaciones,OU=Grupos,DC=qa,DC=edenor)))";
                USER_SEARCH_BASE = "ou=Edificios";
                GROUP_SEARCH_BASE = "OU=PortalENRE_Reclamos,OU=Aplicaciones,OU=Grupos";                
                break;
            case 3:
                //PRO PORTAL RECLAMOS
                URL_LDAP = "ldap://192.168.145.50:389/DC=pro,DC=edenor";
                USUARIO_LDAP = "CN=SVC_consulta_ot,OU=Cuentas de Servicio,DC=pro,DC=edenor";
                PASSWORD_LDAP = "Edenor2010";
                USER_SEARCH_FILTER = "(&(cn={0})(|(memberOf=CN=APP_PortalENRE_Reclamos_Operador,OU=PortalENRE_Reclamos,OU=Aplicaciones,OU=Grupos,DC=pro,DC=edenor)(memberOf=CN=APP_PortalENRE_Reclamos_Consulta,OU=PortalENRE_Reclamos,OU=Aplicaciones,OU=Grupos,DC=pro,DC=edenor)))";
                USER_SEARCH_BASE = "ou=Edificios";
                GROUP_SEARCH_BASE = "OU=PortalENRE_Reclamos,OU=Aplicaciones,OU=Grupos";                
                break;
            case 4:
                // PRO PORTALAT
                URL_LDAP = "ldap://pro.edenor:389/DC=pro,DC=edenor";
                USUARIO_LDAP = "CN=SVC_consulta_ot,OU=Cuentas de Servicio,DC=pro,DC=edenor";
                PASSWORD_LDAP = "Edenor2010";
                USER_SEARCH_FILTER = "(&(cn={0})(|(memberOf=CN=APP_GELEC_SUPERVISOR,OU=GELEC,OU=FIM-SG,OU=Grupos,DC=pro,DC=edenor)(memberOf=CN=APP_GELEC_CONSULTA,OU=GELEC,OU=FIM-SG,OU=Grupos,DC=pro,DC=edenor)(memberOf=CN=APP_GELEC_ADMINISTRADOR,OU=GELEC,OU=FIM-SG,OU=Grupos,DC=pro,DC=edenor)))";
                USER_SEARCH_BASE = "ou=Edificios";
                GROUP_SEARCH_BASE = "ou=GELEC,OU=FIM-SG,OU=Grupos";
                break;
        }
        
        // Nombre de usuario a verificar
        String usernameToCheck = "rsleiva";

        // Propiedades de conexión LDAP
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, URL_LDAP);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, USUARIO_LDAP);
        env.put(Context.SECURITY_CREDENTIALS, PASSWORD_LDAP);

        try {
            // Establecer la conexión LDAP
            DirContext ctx = new InitialLdapContext(env, null);

            // Realizar búsqueda del usuario
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String filter = USER_SEARCH_FILTER.replace("{0}", usernameToCheck);
            NamingEnumeration<SearchResult> results = ctx.search(USER_SEARCH_BASE, filter, searchControls);

            // Verificar si se encontró el usuario y sus roles
            if (results.hasMore()) {
                // El usuario fue encontrado, verificar sus roles
                SearchResult result = results.next();
                String dn = result.getNameInNamespace();
                System.out.println("El usuario " + usernameToCheck + " fue encontrado en LDAP con DN: " + dn);
                
                // Verificar los roles del usuario
                SearchControls groupSearchControls = new SearchControls();
                groupSearchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                NamingEnumeration<SearchResult> groupResults = ctx.search(GROUP_SEARCH_BASE, "member=" + dn, groupSearchControls);

                boolean hasRole = false;
                while (groupResults.hasMore()) {
                    SearchResult groupResult = groupResults.next();
                    String groupName = groupResult.getNameInNamespace();
                    System.out.println("El usuario " + usernameToCheck + " tiene el rol " + groupName);
                }
            } else {
                // El usuario no fue encontrado
                System.out.println("El usuario " + usernameToCheck + " no fue encontrado en LDAP o no tiene permisos.");
            }

            // Cerrar conexión LDAP
            ctx.close();
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("No se pudo establecer conexion con el LDAP.");
        }
    }
}
