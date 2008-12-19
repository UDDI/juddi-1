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
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.juddi.api.datatype.Publisher;
import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.apache.juddi.api.impl.UDDISecurityImpl;
import org.apache.juddi.error.ErrorMessage;
import org.apache.juddi.error.FatalErrorException;
import org.apache.juddi.keygen.KeyGenerator;
import org.apache.juddi.mapping.MappingApiToModel;
import org.apache.juddi.model.KeyGeneratorKey;
import org.apache.juddi.model.KeyGeneratorKeyId;
import org.apache.juddi.model.UddiEntityPublisher;
import org.apache.juddi.query.PersistenceManager;
import org.apache.log4j.Logger;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class Install {

	public static final String FILE_ROOT_BUSINESSENTITY = "root_BusinessEntity.xml";
	public static final String FILE_ROOT_PUBLISHER = "root_Publisher.xml";
	public static final String FILE_ROOT_TMODELKEYGEN = "root_tModelKeyGen.xml";
	public static final String FILE_UDDI_PUBLISHER = "UDDI_Publisher.xml";
	public static final String FILE_UDDI_TMODELS = "UDDI_tModels.xml";
	
	public static final String FILE_PERSISTENCE = "persistence.xml";
	public static final String JUDDI_INSTALL_DATA_DIR = "juddi_install_data/";
	public static Logger log = Logger.getLogger(Install.class);
	
	public static void install() throws JAXBException, DispositionReportFaultMessage {
		
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
	
			if (alreadyInstalled(em))
				throw new FatalErrorException(new ErrorMessage("errors.install.AlreadyInstalled"));
			
			UddiEntityPublisher rootPublisher = installPublisher(em, JUDDI_INSTALL_DATA_DIR + FILE_ROOT_PUBLISHER);
			UddiEntityPublisher uddiPublisher = installPublisher(em, JUDDI_INSTALL_DATA_DIR + FILE_UDDI_PUBLISHER);
			
			installPublisherKeyGen(em, JUDDI_INSTALL_DATA_DIR + FILE_ROOT_TMODELKEYGEN, rootPublisher);
			
			installUDDITModels(em, JUDDI_INSTALL_DATA_DIR + FILE_UDDI_TMODELS, uddiPublisher);
			tx.commit();
			//TODO why does this need it's own tx?
			tx.begin();
			installRootBusinessEntity(em, JUDDI_INSTALL_DATA_DIR + FILE_ROOT_BUSINESSENTITY, rootPublisher);
			tx.commit();
		} catch (Exception e) {
			log .error(e.getMessage(),e);
			tx.rollback();
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}

		
		
	}

	public static void uninstall() {
		// Close the open emf, open a new one with Persistence.create...(String, Map) and overwrite the property that handles the table 
		// generation. The persistence.xml file will have to be read in to determine which property
		// to overwrite.  The property will be specific to the provider.  
		// Hibernate:  <property name="hibernate.hbm2ddl.auto" value="update"/> ->use "create-drop" or just "drop"?
		// OpenJPA: openjpa.jdbc.SynchronizeMappings=buildSchema(SchemaAction='add,deleteTableContents')
		// etc...(find more)
		// Then close this emf.  Question:  is the original emf reusable or will closing it cause problems?
		
	}
	
	public static boolean alreadyInstalled() {
		EntityManager em = PersistenceManager.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		boolean result = alreadyInstalled(em);
		
		tx.commit();
		em.close();
		
		return result;
	}
	
	public static org.uddi.api_v3.RegisteredInfo getRootRegisteredInfo() throws DispositionReportFaultMessage {
		UDDIPublicationImpl publish = new UDDIPublicationImpl();
		UDDISecurityImpl security = new UDDISecurityImpl();

		// TODO:  What if user configures a different authenticator?  Passing no credentials will not work.
		org.uddi.api_v3.GetAuthToken gat = new org.uddi.api_v3.GetAuthToken();
		gat.setUserID(Constants.ROOT_PUBLISHER);
		org.uddi.api_v3.AuthToken authToken = security.getAuthToken(gat);
		
		org.uddi.api_v3.GetRegisteredInfo gri = new org.uddi.api_v3.GetRegisteredInfo();
		gri.setAuthInfo(authToken.getAuthInfo());

		return publish.getRegisteredInfo(gri);
	}
	
	private static boolean alreadyInstalled(EntityManager em) {
		
		UddiEntityPublisher publisher = em.find(UddiEntityPublisher.class, Constants.ROOT_PUBLISHER);
		if (publisher != null)
			return true;

		publisher = em.find(UddiEntityPublisher.class, Constants.UDDI_PUBLISHER);
		if (publisher != null)
			return true;
		
		return false;
	}
	
	private static void installRootBusinessEntity(EntityManager em, String resource, UddiEntityPublisher publisher) 
	throws JAXBException, DispositionReportFaultMessage, IOException {
		UDDIPublicationImpl publish = new UDDIPublicationImpl();
		UDDISecurityImpl security = new UDDISecurityImpl();

		// TODO:  What if user configures a different authenticator?  Passing no credentials will not work.
		org.uddi.api_v3.GetAuthToken gat = new org.uddi.api_v3.GetAuthToken();
		gat.setUserID(publisher.getAuthorizedName());
		org.uddi.api_v3.AuthToken authToken = security.getAuthToken(gat);
		
		org.uddi.api_v3.SaveBusiness sb = new org.uddi.api_v3.SaveBusiness();

		org.uddi.api_v3.BusinessEntity apiBusinessEntity = (org.uddi.api_v3.BusinessEntity)buildEntityFromDoc(resource, "org.uddi.api_v3");
		sb.getBusinessEntity().add(apiBusinessEntity);
		sb.setAuthInfo(authToken.getAuthInfo());
		
		publish.saveBusiness(sb);
	}
	
	private static void installUDDITModels(EntityManager em, String resource, UddiEntityPublisher publisher) 
		throws JAXBException, DispositionReportFaultMessage, IOException {
		SaveTModel apiSaveTModel = (SaveTModel)buildEntityFromDoc(resource, "org.uddi.api_v3");
		installTModels(em, apiSaveTModel.getTModel(), publisher);
		
	}
	
	private static UddiEntityPublisher installPublisher(EntityManager em, String resource) 
		throws JAXBException, DispositionReportFaultMessage, IOException {
		Publisher apiPub = (Publisher)buildEntityFromDoc(resource, "org.apache.juddi.api.datatype");
		org.apache.juddi.model.Publisher modelPub = new org.apache.juddi.model.Publisher();
		MappingApiToModel.mapPublisher(apiPub, modelPub);
		em.persist(modelPub);
		return modelPub;
	}
	
	private static void installTModels(EntityManager em, List<org.uddi.api_v3.TModel> apiTModelList, UddiEntityPublisher publisher) throws DispositionReportFaultMessage {
		if (apiTModelList != null) {
			for (org.uddi.api_v3.TModel apiTModel : apiTModelList) {
				String tModelKey = apiTModel.getTModelKey();

				if (tModelKey.toUpperCase().endsWith(KeyGenerator.KEYGENERATOR_SUFFIX.toUpperCase())) {
					installPublisherKeyGen(em, apiTModel, publisher);
				}
				else {
					org.apache.juddi.model.Tmodel modelTModel = new org.apache.juddi.model.Tmodel();
					MappingApiToModel.mapTModel(apiTModel, modelTModel);
					modelTModel.setPublisher(publisher);
					
					em.persist(modelTModel);
				}
				
			}
		}
		
	}

	private static void installPublisherKeyGen(EntityManager em, String resource, UddiEntityPublisher publisher) 
		throws JAXBException, DispositionReportFaultMessage, IOException {
		TModel apiTModel = (TModel)buildEntityFromDoc(resource, "org.uddi.api_v3");
		installPublisherKeyGen(em, apiTModel, publisher);
	}

	private static void installPublisherKeyGen(EntityManager em, TModel apiTModel, UddiEntityPublisher publisher) throws DispositionReportFaultMessage {

		org.apache.juddi.model.Tmodel modelTModel = new org.apache.juddi.model.Tmodel();
		MappingApiToModel.mapTModel(apiTModel, modelTModel);
		modelTModel.setPublisher(publisher);
		
		em.persist(modelTModel);
		
		int id = 0;
		Set<KeyGeneratorKey> keyGenList = publisher.getKeyGeneratorKeys();
		if (keyGenList != null)
			id = keyGenList.size();
		
		KeyGeneratorKey keyGenKey = new KeyGeneratorKey();
		keyGenKey.setId(new KeyGeneratorKeyId(publisher.getAuthorizedName(), id));
		keyGenKey.setPublisher(publisher);
		keyGenKey.setKeygenTModelKey(modelTModel.getEntityKey());
		publisher.getKeyGeneratorKeys().add(keyGenKey);
		
	}
	
	private static Object buildEntityFromDoc(String resource, String thePackage) throws JAXBException, IOException {
		InputStream resourceStream =Thread.currentThread().getContextClassLoader().getResource(resource).openStream();

		JAXBContext jc = JAXBContext.newInstance(thePackage);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Object obj = ((JAXBElement<?>)unmarshaller.unmarshal(resourceStream)).getValue();
		return obj;
	}

}
