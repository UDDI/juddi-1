/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.registry;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This servlet is ONLY used to initialize the jUDDI webapp on
 * startup and cleanup the jUDDI webapp on shutdown.
 * 
 * @author Steve Viens (sviens@apache.org)
 */
public class RegistryServlet extends HttpServlet
{
  // xml config file name.
  private static final String PROPERTY_FILE = "/WEB-INF/juddi.properties";

  // private reference to the webapp's logger.
  private static Log log = LogFactory.getLog(RegistryServlet.class);
  
  // registry singleton instance
  private static RegistryEngine registry = null;

  /**
   * Create the shared instance of jUDDI's Registry class
   * and call it's "init()" method to initialize all core 
   * components.
   */
  public void init(ServletConfig config) 
    throws ServletException
  {
    super.init(config);    

    Properties props = new Properties();

    try
    {      
      log.info("jUDDI Starting: Loading resources and initializing subsystems.");
      
      InputStream is = 
      	getServletContext().getResourceAsStream(PROPERTY_FILE);
    	
      if (is != null)
      {
        log.info("Resources loaded from: "+PROPERTY_FILE);

        // Load jUDDI configuration from the 
        // juddi.properties file found in the 
        // WEB-INF directory.

        props.load(is);
      }
      else
      {
        log.warn("Could not locate jUDDI properties '" + PROPERTY_FILE + 
        		"'. Using defaults.");

        // A juddi.properties file doesn't exist
        // yet so create create a new Properties 
      	// instance using default property values.
        
        props.put(RegistryEngine.PROPNAME_OPERATOR_NAME,
                  RegistryEngine.DEFAULT_OPERATOR_NAME);
        
        props.put(RegistryEngine.PROPNAME_OPERATOR_URL,
                  RegistryEngine.DEFAULT_OPERATOR_URL);
        
        props.put(RegistryEngine.PROPNAME_ADMIN_EMAIL_ADDRESS,
                  RegistryEngine.DEFAULT_ADMIN_EMAIL_ADDRESS);
        
        props.put(RegistryEngine.PROPNAME_DATASOURCE_NAME,
                  RegistryEngine.DEFAULT_DATASOURCE_NAME);
              
        props.put(RegistryEngine.PROPNAME_AUTH_CLASS_NAME,
                  RegistryEngine.DEFAULT_AUTH_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_CRYPTOR_CLASS_NAME,
                  RegistryEngine.DEFAULT_CRYPTOR_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_DATASTORE_CLASS_NAME,
                  RegistryEngine.DEFAULT_DATASTORE_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_MONITOR_CLASS_NAME,
                  RegistryEngine.DEFAULT_MONITOR_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_UUIDGEN_CLASS_NAME,
                  RegistryEngine.DEFAULT_UUIDGEN_CLASS_NAME);
        
        props.put(RegistryEngine.PROPNAME_VALIDATOR_CLASS_NAME,
                  RegistryEngine.DEFAULT_VALIDATOR_CLASS_NAME);  

        props.put(RegistryEngine.PROPNAME_MAX_NAME_ELEMENTS,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_NAME_ELEMENTS));
        
        props.put(RegistryEngine.PROPNAME_MAX_NAME_LENGTH,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_NAME_LENGTH));

        props.put(RegistryEngine.PROPNAME_MAX_MESSAGE_SIZE,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_MESSAGE_SIZE));        

        props.put(RegistryEngine.PROPNAME_MAX_BUSINESS_ENTITIES_PER_USER,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_BUSINESS_ENTITIES_PER_USER));
        
        props.put(RegistryEngine.PROPNAME_MAX_BUSINESS_SERVICES_PER_BUSINESS,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_BUSINESS_SERVICES_PER_BUSINESS));
        
        props.put(RegistryEngine.PROPNAME_MAX_BINDING_TEMPLATES_PER_SERVICE,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_BINDING_TEMPLATES_PER_SERVICE));
        
        props.put(RegistryEngine.PROPNAME_MAX_TMODELS_PER_USER,
                  Integer.toString(RegistryEngine.DEFAULT_MAX_TMODELS_PER_USER));
        
        props.put(RegistryEngine.PROPNAME_MAX_ROWS_LIMIT,           
                  Integer.toString(RegistryEngine.DEFAULT_MAX_ROWS_LIMIT));
      }
    }
    catch(IOException ioex) {
      log.error(ioex.getMessage(),ioex);
    }

    log.info("Initializing jUDDI subsystems.");
    
    registry = new RegistryEngine(props);
    registry.init();
  }

  /**
   * Grab the shared instance of jUDDI's Registry class and
   * call it's "dispose()" method to notify all sub-components
   * to stop any background threads and release any external
   * resources they may have aquired.
   */
  public void destroy()
  {
    super.destroy();
    
    log.info("jUDDI Stopping: Cleaning up existing resources.");

    RegistryEngine registry = RegistryServlet.getRegistry();
    if (registry != null)
      registry.dispose();
  }

  /**
   *
   */
  public static RegistryEngine getRegistry()
  {
    return registry;
  }
}