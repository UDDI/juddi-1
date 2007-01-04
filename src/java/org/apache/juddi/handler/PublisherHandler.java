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
package org.apache.juddi.handler;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.util.Config;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of Publisher objects.
 * Returns Publisher."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class PublisherHandler extends AbstractHandler
{
  public static final String TAG_NAME = "publisher";

  protected PublisherHandler(HandlerMaker maker)
  {
  }

  public RegistryObject unmarshal(Element element)
  {
    Publisher obj = new Publisher();

    // Attributes (required)
    obj.setPublisherID(element.getAttribute("publisherID"));
    obj.setName(element.getAttribute("publisherName"));

    String admin = element.getAttribute("admin");
    if ((admin != null) && (admin.length() > 0))
      obj.setAdminValue(admin);
    else
      obj.setAdmin(false);

    String enabled = element.getAttribute("enabled");
    if ((enabled != null) && (enabled.length() > 0))
      obj.setEnabledValue(enabled);
    else
      obj.setAdmin(false);

    String emailAddress = element.getAttribute("emailAddress");
    if ((emailAddress != null) && (emailAddress.length() > 0))
      obj.setEmailAddress(emailAddress);
    
    // maxBusinesses
    try {
    	String attrValue = element.getAttribute("maxBusinesses");
    	if ((attrValue != null) && (attrValue.length() > 0))
     		obj.setMaxBusinesses(Integer.parseInt(attrValue));
    	else 
    		obj.setMaxBusinesses(Config.getMaxBusinessesPerPublisher());
    }
    catch (Exception ex) {
    	obj.setMaxBusinesses(
    			Config.getMaxBusinessesPerPublisher());
    }
    
    // maxServicesPerBusiness
    try {
    	String attrValue = element.getAttribute("maxServicesPerBusiness");
    	if ((attrValue != null) && (attrValue.length() > 0))
    		obj.setMaxServicesPerBusiness(Integer.parseInt(attrValue));
    	else 
    		obj.setMaxServicesPerBusiness(Config.getMaxServicesPerBusiness());
    }
    catch (Exception ex) {
    	obj.setMaxServicesPerBusiness(
    			Config.getMaxServicesPerBusiness());
    }

    // maxBindingsPerService
    try {
    	String attrValue = element.getAttribute("maxBindingsPerService");
    	if ((attrValue != null) && (attrValue.length() > 0))
    		obj.setMaxBindingsPerService(Integer.parseInt(attrValue));
    	else 
    		obj.setMaxBindingsPerService(Config.getMaxBindingsPerService());
    }
    catch (Exception ex) {
    	obj.setMaxBindingsPerService(
    			Config.getMaxBindingsPerService());
    }

    // maxTModels
    try {
    	String attrValue = element.getAttribute("maxTModels");
    	if ((attrValue != null) && (attrValue.length() > 0))
    		obj.setMaxTModels(Integer.parseInt(attrValue));
    	else 
    		obj.setMaxTModels(Config.getMaxTModelsPerPublisher());
    }
    catch (Exception ex) {
    	obj.setMaxTModels(
    			Config.getMaxTModelsPerPublisher());
    }
    
    // Text Node Value
    // {none}

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    Publisher publisher = (Publisher)object;
    String generic = getGeneric(null);
    String namespace = getUDDINamespace(generic);
    Element element = parent.getOwnerDocument().createElementNS(namespace,TAG_NAME);

    // Attributes (required)
    String publisherID = publisher.getPublisherID();
    if ((publisherID != null) && (publisherID.length() > 0))
      element.setAttribute("publisherID",publisherID);
    else
      element.setAttribute("publisherID","");

    String publisherName = publisher.getName();
    if ((publisherName != null) && (publisherName.length() > 0))
      element.setAttribute("publisherName",publisherName);
    else
      element.setAttribute("publisherName","");

    element.setAttribute("admin",String.valueOf(publisher.isAdmin()));
    element.setAttribute("enabled",String.valueOf(publisher.isEnabled()));

    String emailAddress = publisher.getEmailAddress();
    if ((emailAddress != null) && (emailAddress.length() > 0))
      element.setAttribute("emailAddress",emailAddress);

    element.setAttribute("maxBusinessEntities",String.valueOf(publisher.getMaxBusinesses()));
    element.setAttribute("maxServicesPerBusiness",String.valueOf(publisher.getMaxServicesPerBusiness()));
    element.setAttribute("maxBindingsPerService",String.valueOf(publisher.getMaxBindingsPerService()));
    element.setAttribute("maxTModels",String.valueOf(publisher.getMaxTModels()));

    // Text Node Value
    // {none}

    // Child Elements
    // {none}

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(PublisherHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;

    Publisher publisher = new Publisher();
    publisher.setPublisherID("bcrosby");
    publisher.setName("Bing Crosby");
    publisher.setEmailAddress("bcrosby@juddi.org");
    publisher.setAdmin(true);
    publisher.setEnabled(true);
    publisher.setMaxBusinesses(5);
    publisher.setMaxServicesPerBusiness(10);
    publisher.setMaxBindingsPerService(25);
    publisher.setMaxTModels(10);

    System.out.println();

    RegistryObject regObject = publisher;
    handler.marshal(regObject,parent);
    child = (Element)parent.getFirstChild();
    parent.removeChild(child);
    XMLUtils.writeXML(child,System.out);

    System.out.println();

    regObject = handler.unmarshal(child);
    handler.marshal(regObject,parent);
    child = (Element)parent.getFirstChild();
    parent.removeChild(child);
    XMLUtils.writeXML(child,System.out);
  }
}