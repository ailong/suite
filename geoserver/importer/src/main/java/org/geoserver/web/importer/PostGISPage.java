/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.web.importer;

import static org.geotools.data.postgis.PostgisNGDataStoreFactory.*;
import static org.geotools.jdbc.JDBCDataStoreFactory.*;
import static org.geotools.jdbc.JDBCJNDIDataStoreFactory.*;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.geoserver.catalog.NamespaceInfo;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.postgis.PostgisNGDataStoreFactory;
import org.geotools.data.postgis.PostgisNGJNDIDataStoreFactory;
import org.geotools.jdbc.JDBCDataStoreFactory;

/**
 * Connection params form for the PostGIS database
 * @author Andrea Aime - OpenGeo
 */
@SuppressWarnings("serial")
public class PostGISPage extends AbstractDBMSPage {
    
    private JNDIParamPanel jndiParamsPanel;

    private BasicDbmsParamPanel basicDbmsPanel;

    protected LinkedHashMap<String, Component> buildParamPanels() {
        LinkedHashMap<String, Component> result = new LinkedHashMap<String, Component>();

        // basic panel
        basicDbmsPanel = new BasicDbmsParamPanel("01", "localhost", 5432, true);
        result.put(CONNECTION_DEFAULT, basicDbmsPanel);

        // jndi param panels
        jndiParamsPanel = new JNDIParamPanel("02");
        result.put(CONNECTION_JNDI, jndiParamsPanel);
        
        return result;
    }
    
    protected DataStoreFactorySpi fillStoreParams(NamespaceInfo namespace,
            Map<String, Serializable> params) throws URISyntaxException {
        DataStoreFactorySpi factory;
        if (CONNECTION_JNDI.equals(connectionType)) {
            factory = new PostgisNGJNDIDataStoreFactory();

            params.put(PostgisNGJNDIDataStoreFactory.DBTYPE.key,
                    (String) PostgisNGJNDIDataStoreFactory.DBTYPE.sample);
            params.put(JNDI_REFNAME.key, jndiParamsPanel.jndiReferenceName);
        } else {
            factory = new PostgisNGDataStoreFactory();

            // basic params
            params.put(PostgisNGDataStoreFactory.DBTYPE.key,
                    (String) PostgisNGDataStoreFactory.DBTYPE.sample);
            params.put(HOST.key, basicDbmsPanel.host);
            params.put(PostgisNGDataStoreFactory.PORT.key, basicDbmsPanel.port);
            params.put(USER.key, basicDbmsPanel.username);
            params.put(PASSWD.key, basicDbmsPanel.password);
            params.put(DATABASE.key, basicDbmsPanel.database);

            // connection pool params
            params.put(MINCONN.key, basicDbmsPanel.connPool.minConnection);
            params.put(MAXCONN.key, basicDbmsPanel.connPool.maxConnection);
            params.put(FETCHSIZE.key, basicDbmsPanel.connPool.fetchSize);
            params.put(MAXWAIT.key, basicDbmsPanel.connPool.timeout);
            params.put(VALIDATECONN.key, basicDbmsPanel.connPool.validate);
            params.put(PREPARED_STATEMENTS.key, basicDbmsPanel.connPool.preparedStatements);

        }
        params.put(JDBCDataStoreFactory.SCHEMA.key, otherParamsPanel.schema);
        params.put(NAMESPACE.key, new URI(namespace.getURI()).toString());
        params.put(LOOSEBBOX.key, otherParamsPanel.looseBBox);
        params.put(PK_METADATA_TABLE.key, otherParamsPanel.pkMetadata);
        return factory;
    }

}
