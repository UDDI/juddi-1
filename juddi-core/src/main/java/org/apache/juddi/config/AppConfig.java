/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
 *
 */
package org.apache.juddi.config;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXBException;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.juddi.keygen.KeyGenerator;
import org.apache.juddi.model.KeyGeneratorKey;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.FindBusinessByCategoryQuery;
import org.apache.juddi.query.PersistenceManager;
import org.apache.juddi.query.util.FindQualifiers;
import org.apache.log4j.Logger;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.ObjectFactory;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * Handles the application level configuration for jUDDI. By default it first
 * looks at system properties
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class AppConfig 
{
	private final static String JUDDI_PROPERTIES = "juddi.properties";
	private Logger log = Logger.getLogger(AppConfig.class);
	private Configuration config;
	private static AppConfig instance=null;
	
	/**
	 * Constructor (note Singleton pattern).
	 * @throws ConfigurationException
	 */
	private AppConfig() throws ConfigurationException 
	{
		loadConfiguration();
	}
	/**
	 * Does the actual work of reading the configuration from System
	 * Properties and/or juddi.properties file. When the juddi.properties
	 * file is updated the file will be reloaded. By default the reloadDelay is
	 * set to 1 second to prevent excessive date stamp checking.
	 */
	private void loadConfiguration() throws ConfigurationException
	{
		//Properties from system properties
		CompositeConfiguration compositeConfig = new CompositeConfiguration();
		compositeConfig.addConfiguration(new SystemConfiguration());
		//Properties from file
		PropertiesConfiguration propConfig = new PropertiesConfiguration(JUDDI_PROPERTIES);
		// Properties from the persistence layer 
		MapConfiguration persistentConfig = new MapConfiguration(getPersistentConfiguration());
		
		long refreshDelay = propConfig.getLong(Property.JUDDI_CONFIGURATION_RELOAD_DELAY, 1000l);
		log.debug("Setting refreshDelay to " + refreshDelay);
		FileChangedReloadingStrategy fileChangedReloadingStrategy = new FileChangedReloadingStrategy();
		fileChangedReloadingStrategy.setRefreshDelay(refreshDelay);
		propConfig.setReloadingStrategy(fileChangedReloadingStrategy);
		compositeConfig.addConfiguration(propConfig);
		compositeConfig.addConfiguration(persistentConfig);
		//Making the new configuration globally accessible.
		config = compositeConfig;
	}

	/*
	 * This method will build any "persisted" properties. Persisted properties are those that are stored in the database.  These values
	 * should be stored when the application is installed.  If they don't exist, then an error should occur.
	 */
	private Properties getPersistentConfiguration() throws ConfigurationException {
		Properties result = new Properties();
		
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		if (!Install.alreadyInstalled(em)) {
			log.info("The 'root' publisher was not found, loading...");
			try {
				Install.install();
			} catch (JAXBException e) {
				throw new ConfigurationException(e);
			} catch (DispositionReportFaultMessage e) {
				throw new ConfigurationException(e);
			} catch (IOException e) {
				throw new ConfigurationException(e);
			}

		}
		tx.commit();
		tx.begin();
		UddiEntityPublisher  rootPublisher = em.find(UddiEntityPublisher.class, Constants.ROOT_PUBLISHER);
		Set<KeyGeneratorKey> rootKeyGenList = rootPublisher.getKeyGeneratorKeys();
		if (rootKeyGenList == null || rootKeyGenList.size() == 0)
			throw new ConfigurationException("The 'root' publisher key generator was not found.  Please make sure that the application is properly installed.");
		
		String rootKeyGen = rootKeyGenList.iterator().next().getKeygenTModelKey();
		//rootKeyGen = rootKeyGen.substring((KeyGenerator.UDDI_SCHEME + KeyGenerator.PARTITION_SEPARATOR).length());
		rootKeyGen = rootKeyGen.substring(0, rootKeyGen.length() - (KeyGenerator.PARTITION_SEPARATOR + KeyGenerator.KEYGENERATOR_SUFFIX).length());
		log.debug("root partition:  " + rootKeyGen);

		result.setProperty(Property.JUDDI_ROOT_PARTITION, rootKeyGen);
		
		// The node Id is defined as the business key of the business entity categorized as a node.  This entity is saved as part of the install.
		// Only one business entity should be categorized as a node.
		String nodeId = "";
		CategoryBag categoryBag = new CategoryBag();
		KeyedReference keyedRef = new KeyedReference();
		keyedRef.setTModelKey(Constants.NODE_CATEGORY_TMODEL);
		keyedRef.setKeyValue(Constants.NODE_KEYVALUE);
		categoryBag.getContent().add(new ObjectFactory().createKeyedReference(keyedRef));
		List<?> keyList = FindBusinessByCategoryQuery.select(em, new FindQualifiers(), categoryBag, null);
		if (keyList != null && keyList.size() > 1)
			throw new ConfigurationException("Only one business entity can be categorized as the node.");
		
		if (keyList != null && keyList.size() > 0) {
			nodeId = (String)keyList.get(0);
		}
		else
			throw new ConfigurationException("A node business entity was not found.  Please make sure that the application is properly installed.");
		result.setProperty(Property.JUDDI_NODE_ID, nodeId);
		
		tx.commit();
		em.close();
		
		return result;
	}
	
	
	/**
	 * Obtains the reference to the Singleton instance.
	 * 
	 * @return the APplicationConfuration Singleton Instance.
	 * @throws ConfigurationException
	 */
	public static AppConfig getInstance() throws ConfigurationException 
	{
		if (instance==null) {
			instance = new AppConfig();
		}
		return instance;
	}
	/**
	 * Hook to receive configuration reload events from an external application.
	 * 
	 * @throws ConfigurationException
	 */
	public static void reloadConfig() throws ConfigurationException
	{
		getInstance().loadConfiguration();
	}
	/**
	 * The object from which property values can be obtained.
	 * @return the commons Configuration interface
	 * @throws ConfigurationException 
	 */
	public static Configuration getConfiguration() throws ConfigurationException
	{
		return getInstance().config;
	}
}