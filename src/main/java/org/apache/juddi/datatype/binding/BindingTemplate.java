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
package org.apache.juddi.datatype.binding;

import java.util.Vector;

import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;

/**
 * Technical web service description. Contains "information relevant for
 * application programs that need to connect to and then communicate with a
 * remote Web Service. This information includes the address to make contact
 * with a Web Service, as well as support for option information that can be
 * used to describe both hosted services and services that require additional
 * values to be discovered prior to invoking a service" - technical
 * whitepaper
 *
 * "Technical information about a service entry point and construction
 * specs" - XML Structure Reference
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class BindingTemplate implements RegistryObject
{
  // unique key for the bindingTemplate.
  String bindingKey;

  // the unique key of the enclosing service
  // is required if the business service is not specified
  String serviceKey;

  // Optional repeating element. This is zero or more language-qualified
  // text descriptions of the technical service entry point.
  Vector descVector;

  // One of accessPoint or hostingRedirector is required. accessPoint may be an
  // email, a URL, or even a phone nbr. No assumptions can be made about this
  // field without understanding the specifics of the service.
  AccessPoint accessPoint;

  // hostingRedirector is required if accessPoint is not supplied. This field
  // is a redirected reference to a DIFFERENT bindingTemplate. If you query a
  // bindingTemplate and find a hostingRedirector value, you should retrieve
  // that bindingTemplate and use it in place of the first one (the one
  // containing the hostingRedirector).
  HostingRedirector hostingRedirector;

  // the "technical fingerprint". Contains a set of 0..n references to
  // tModelInstanceInfo instances that are all completely supported by this web
  // service.
  TModelInstanceDetails tModelInstanceDetails;

  // UDDI v3.0 The ability to classify bindingTemplates with
  // categoryBags now allows metadata to be attributed directly to
  // the technical details of a Web service, enabling more granular
  // searches to be performed on the specific technical metadata
  // for a given service.
  CategoryBag categoryBag;

  /**
   * Constructs a new empty BindingTemplate.
   */
  public BindingTemplate()
  {
  }

  /**
   * Sets the bindingkey of this bindingtemplate to the given key.
   *
   * @param key The new bindingkey of this bindingtemplate.
   */
  public void setBindingKey(String key)
  {
    this.bindingKey = key;
  }

  /**
   * Returns the bindingkey of this binding template.
   * @return The bindingkey of this binding template.
   */
  public String getBindingKey()
  {
    return this.bindingKey;
  }

  /**
   * Sets the servicekey of this bindingtemplate.
   *
   * @param key The new service key.
   */
  public void setServiceKey(String key)
  {
    this.serviceKey = key;
  }

  /**
   * Returns the servicekey of this bindingtemplate. This method can return null
   * if the business service for this binding template has been set and if the
   * service key for this binding template has not been set.
   *
   * @return The servicekey of this bindingtemplate.
   */
  public String getServiceKey()
  {
    return this.serviceKey;
  }

  /**
   * Adds the given description. If there was already a description with the
   * same language-code as the new description, an exception will be thrown.
   *
   * @param desc The description to add.
   */
  public void addDescription(Description desc)
  {
    if (this.descVector == null)
      this.descVector = new Vector();
    this.descVector.add(desc);
  }

  /**
   * Sets the description list to the current one. Ignores any object in the
   * collection that is not an "instanceof" the Description class.
   *
   * @param descs Descriptions of Description objects to set
   */
  public void setDescriptionVector(Vector descs)
  {
    this.descVector = descs;
  }

  /**
   * Returns the descriptions.
   *
   * @return the descriptions. If the aren't any descriptions, an empty
   *  enumeration is returned.
   */
  public Vector getDescriptionVector()
  {
    return this.descVector;
  }

  /**
   * Sets the AccessPoint of this BindingTemplate. If the new AccessPoint is
   * not null and if this BindingTemplate also contains a HostingRedirector,
   * that HostingRedirector is set to null.
   *
   * @param point The new AccessPoint of this BindingTemplate.
   */
  public void setAccessPoint(AccessPoint point)
  {
    this.accessPoint = point;
  }

  /**
   * Returns the accesspoint of this binding template.
   *
   * @return The accesspoint of this binding template, or null if the hosting
   *  redirector of this binding template has been set.
   */
  public AccessPoint getAccessPoint()
  {
    return this.accessPoint;
  }

  /**
   * Sets the hosting redirector of this BindingTemplate. If the new
   * HostingRedirector is not null and if this BindingTemplate also contains
   * an AccessPoint, that AccessPoint is set to null.
   *
   * @param director The new HostingRedirector of this BindingTemplate.
   */
  public void setHostingRedirector(HostingRedirector director)
  {
    this.hostingRedirector = director;
  }

  /**
   * Returns the hosting redirector of this binding template.
   *
   * @return The hosting redirector of this BindingTemplate, or null if the
   *  AccessPoint of this BindingTemplate has been set.
   */
  public HostingRedirector getHostingRedirector()
  {
    return this.hostingRedirector;
  }


  /**
   * Returns the tModelInstanceDetails of this binding template.
   *
   * @return The tModelInstanceDetails of this binding template. If this binding
   * template doesn't contain any tModelInstanceDetails, an empty enumeration is returned.
   */
  public TModelInstanceDetails getTModelInstanceDetails()
  {
    return this.tModelInstanceDetails;
  }

  /**
   * Sets the tModelInstanceDetails of this binding template.
   */
  public void setTModelInstanceDetails(TModelInstanceDetails details)
  {
    this.tModelInstanceDetails = details;
  }

  /**
   * Add a category to the categorybag of this binding template.
   * @param ref The category to add.
   */
  public void addCategory(KeyedReference ref)
  {
    // just return if the KeyedReference parameter is null (nothing to add)
    if (ref == null)
      return;

    // make sure the CategoryBag has been initialized
    if (this.categoryBag == null)
      this.categoryBag = new CategoryBag();

    this.categoryBag.addKeyedReference(ref);
  }

  /**
   * Returns the categorybag of this binding template. If this binding
   * template doesn't contain any categories, an empty enumeration is
   * returned.
   *
   * @return The categorybag of this binding template.
   */
  public CategoryBag getCategoryBag()
  {
    return this.categoryBag;
  }

  /**
   * Set the categorybag of this binding template to the given one.
   *
   * @param bag The new categorybag.
   */
  public void setCategoryBag(CategoryBag bag)
  {
    this.categoryBag = bag;
  }
}