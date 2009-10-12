/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.tck;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.Registry;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.transport.InVMTransport;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.tck.TckBindingTemplate;
import org.apache.juddi.v3.tck.TckBusiness;
import org.apache.juddi.v3.tck.TckBusinessService;
import org.apache.juddi.v3.tck.TckPublisher;
import org.apache.juddi.v3.tck.TckSecurity;
import org.apache.juddi.v3.tck.TckTModel;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.Loader;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
public class UDDI_050_BindingTemplateIntegrationTest 
{
   
    private static Logger logger                          = Logger.getLogger(UDDI_050_BindingTemplateIntegrationTest.class);
	
	private static TckTModel tckTModel                    = null;
	private static TckBusiness tckBusiness                = null;
	private static TckBusinessService tckBusinessService  = null;
	private static TckBindingTemplate tckBindingTemplate  = null;
	
	
	private static String authInfoJoe                 = null;
	
	@BeforeClass
	public static void setup() throws ConfigurationException {
		String clazz = UDDIClerkManager.getClientConfig().getNodes().get("default").getProxyTransport();
		if (InVMTransport.class.getName().equals(clazz)) {
			Registry.start();
		}
		logger.debug("Getting auth tokens..");
		try {
	         Class<?> transportClass = Loader.loadClass(clazz);
	         if (transportClass!=null) {
	        	 Transport transport = (Transport) transportClass.getConstructor(String.class).newInstance("default");
	        	 
	        	 UDDISecurityPortType security = transport.getUDDISecurityService();
	        	 authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.JOE_PUBLISHER_ID,  TckPublisher.JOE_PUBLISHER_CRED);
	        	 Assert.assertNotNull(authInfoJoe);
	        	 
	        	 UDDIPublicationPortType publication = transport.getUDDIPublishService();
	        	 UDDIInquiryPortType inquiry = transport.getUDDIInquiryService();
	        	 
	        	 tckTModel  = new TckTModel(publication, inquiry);
	        	 tckBusiness = new TckBusiness(publication, inquiry);
	        	 tckBusinessService = new TckBusinessService(publication, inquiry);
	        	 tckBindingTemplate = new TckBindingTemplate(publication, inquiry);
	         } else {
	        	 Assert.fail();
	         }
	     } catch (Exception e) {
	    	 logger.error(e.getMessage(), e);
			 Assert.fail("Could not obtain authInfo token.");
	     } 
	}
	
	@AfterClass
	public static void stopRegistry() throws ConfigurationException {
		String clazz = UDDIClerkManager.getClientConfig().getNodes().get("default").getProxyTransport();
		if (InVMTransport.class.getName().equals(clazz)) {
			Registry.stop();
		}
	}
	
	@Test
	public void joepublisher() {
		try {
			tckTModel.saveJoePublisherTmodel(authInfoJoe);
			tckBusiness.saveJoePublisherBusiness(authInfoJoe);
			tckBusinessService.saveJoePublisherService(authInfoJoe);
			tckBindingTemplate.saveJoePublisherBinding(authInfoJoe);
			tckBindingTemplate.deleteJoePublisherBinding(authInfoJoe);
		} finally {
			tckBusinessService.deleteJoePublisherService(authInfoJoe);
			tckBusiness.deleteJoePublisherBusiness(authInfoJoe);
			tckTModel.deleteJoePublisherTmodel(authInfoJoe);
		}
	}
}