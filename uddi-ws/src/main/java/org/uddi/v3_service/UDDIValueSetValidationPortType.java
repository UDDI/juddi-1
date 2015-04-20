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


package org.uddi.v3_service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.uddi.api_v3.DispositionReport;
import org.uddi.vs_v3.ValidateValues;


/**
 * This portType defines all of the UDDI value set validation operations.
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.5-b03-
 * Generated source version: 2.1
 * 
 * <p class="MsoBodyText">Whenever a keyedReference is involved in a save operation
it may be checked to see that it is valid.&nbsp; Similarly, a keyedReferenceGroup
element that is involved in a save operation may also be checked to ensure that
it is valid.&nbsp; Checking is performed for tModels that are deemed to be
"checked", as determined by the policy of the UDDI registry.</p>

<p class="MsoBodyText">UDDI provides the ability for third parties to register
value sets, and then control the validation process used by UDDI to perform
such checks. UDDI registries MAY support caching of these external value sets.&nbsp;
UDDI registries MAY also support external validation.&nbsp; Node and registry
policies determine the manner in which validation of references to external
value sets is performed.&nbsp; The APIs in this section can be used by UDDI
registries and nodes in their validation policies.</p>

<p class="MsoBodyText">Third parties that want to provide an external checking
capability may be required by the UDDI registry to implement a Web service in
the same manner that UDDI does (e.g. using SOAP for message passing using
literal encoding) that exposes a single method named validate_values.&nbsp; The
interface for validate_values is described here.</p>

<p class="MsoBodyText">In some cases a node may desire to eliminate or minimize
the number of calls to external validation Web services.&nbsp; It can do so by
caching valid values for those external value sets that allow caching of their
values.&nbsp; A node has two normative options for obtaining the set of valid
values.&nbsp; One is to periodically obtain the set of valid values from those value
set providers that implement a Web service that handles the get_allValidValues API.&nbsp; This API is described below.&nbsp; The other method of obtaining a cache of valid values is to
accumulate the valid values from successful calls to validate_values.</p>

<h3><a name="_Toc85908142"></a><a name="_Toc53709336"></a><a name="_Toc45096421"></a><a name="_Toc45095964"></a><a name="_Toc42047341"></a><a name="_Toc535332281">5.6.1
Value Set Programming Interfaces</a></h3>

<p class="MsoBodyText">The Application Programming Interfaces in this section
represent capabilities that a UDDI registry MAY use to enable validation of
references to value sets.&nbsp; Registry policy determines which external value sets
are supported and how.&nbsp; See Section <a href="#_Ref9007633 ">9.4.19</a> <i>Value
Set Policies </i>and Section <a href="#_Ref9007695 ">9.6.5</a><i>Value Sets </i>for
more information on registry support of external value sets.&nbsp; These SOAP messages all behave synchronously.</p>

<p class="MsoBodyText">The publicly accessible APIs that are used to support
external value set validation are:</p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><a href="#_validate_values"><b><span style="color:windowtext;
text-decoration:none">validate_values</span></b></a>: Used by nodes to allow
external providers of value set validation Web services to assess whether
keyedReferences or keyedReferenceGroups are valid.&nbsp; Returns a dispositionReport
structure.</p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><a href="#_get_allValidValues"><b><span style="color:windowtext;
text-decoration:none">get_allValidValues</span></b></a>: Used by nodes that
support caching of valid values from cacheable checked value sets to obtain the
set of valid values.&nbsp; Returns an empty message or a dispositionReport structure.</p>

<p class="MsoBodyText">Registry policy may require value set providers that offer
one of these Web services to publish the bindingTemplate for the service and
the tModel for the value set in a particular way so that the proper Web service
can be discovered.&nbsp; See Section <a href="#_Ref9007695 ">9.6.5</a> <i>Value sets</i>
for more information<i>.</i>&nbsp; When a value set provider offers one of these Web
services, a tModel for the checked value set SHOULD be published in any
registry the provider wishes to offer it, and a bindingTemplate SHOULD be
published for the Web service(s) the value set provider offers for the checked
value set.&nbsp; The tModel SHOULD have categorizations from the uddi-org:types
category system to indicate the type of value set (<i>categorization</i>, <i>identifier</i>,
<i>relationship</i>, <i>categorizationGroup</i>), that it is checked (<i>checked</i>),
and, if the value set provider allows validation to occur against node caches
of valid values, the <i>cacheable</i> categorization should also be provided.&nbsp; </p>

<p class="MsoBodyText">In order for a value set to be considered checked, the
tModel MUST first be categorized with the checked value from the uddi-org:types
category system. The decision to check such value sets is a registry and node
policy decision.&nbsp; </p>

<p class="MsoBodyText">If a value set tModel is categorized as checked, then in
response to attempts to publish a keyedReference which uses the checked tModel,
nodes MUST either perform the required validation, or return E_unsupported.</p>

<p class="MsoBodyText">The tModel should also have a categorization reference to
the bindingTemplate of the get_allValidValues or validate_values Web service
that the value set provider designates, using the uddi-org:validatedBy category
system.&nbsp; See Section <a href="#_Ref8977125 ">11.1.1</a> <i>UDDI Types Category
System </i>and Section <a href="# ">11.1.7</a> <i>Validated By Category System</i>
for more information.</p>

<p class="MsoBodyText">The bindingTemplate for the get_allValidValues or the
validate_values Web service SHOULD reference in its tModelInstanceDetails the
appropriate value set API tModel (Section <a href="#_Ref8979902 ">11.2.7</a> <i>Value
Set Caching API tModel </i>or Section <a href="#_Ref8979938 ">11.2.8</a> <i>Value
Set Validation API tModel</i>) as well tModels for all of the value sets the
service applies to.&nbsp; </p>
 * 
 */
@WebService(name = "UDDI_ValueSetValidation_PortType", targetNamespace = "urn:uddi-org:v3_service")
@XmlSeeAlso({
    org.uddi.custody_v3.ObjectFactory.class,
    org.uddi.repl_v3.ObjectFactory.class,
    org.uddi.subr_v3.ObjectFactory.class,
    org.uddi.api_v3.ObjectFactory.class,
    org.uddi.vscache_v3.ObjectFactory.class,
    org.uddi.vs_v3.ObjectFactory.class,
    org.uddi.sub_v3.ObjectFactory.class,
    org.w3._2000._09.xmldsig_.ObjectFactory.class,
    org.uddi.policy_v3.ObjectFactory.class,
    org.uddi.policy_v3_instanceparms.ObjectFactory.class
})
public interface UDDIValueSetValidationPortType extends Remote{


    /**
     * 
     * <p class="MsoBodyText">A UDDI node that supports external validation sends the
validate_values API to the appropriate external Web service, of which there is
exactly one, whenever a publisher saves data that uses a keyedReference or
keyedReferenceGroup whose use is regulated by the external party who controls
that Web service. For purposes of discussion, the identifier, category, and
relationship type systems that the keyedReference elements refer to are called
checked value sets. The category group systems that the keyedReferenceGroup
elements refer to are similarly called checked category group systems.&nbsp; </p>

<p class="MsoBodyText">The normal use for checked value sets is to verify that
specific values (checking the keyValue attribute of values supplied) exist
within the value set.&nbsp; For certain value sets the value set provider may
further restrict the use of a value based on a contextual evaluation of the
passed data.&nbsp; The provider may do enable this contextual checking by offering a
validation Web service.</p>

<p class="MsoBodyText">Validation algorithms for checked category group systems
similarly verify that the contents of the keyedReferenceGroup elements form a
valid set according to the validation algorithm for the checked category group
system.&nbsp; Frequently such validation ensures that the value sets identified in
contained keyedReferences are allowed to participate in the category group
system.</p>
* 
     * @param body
     * The UDDI node that is calling validate_values MUST pass one or more businessEntity elements, one or more businessService elements, one or more bindingTemplate elements, one or more tModel elements, or one or more publisherAssertion elements as the sole argument to this Web service.  The one or more elements passed represents the outermost UDDI data structure(s) being passed within a save_business, save_service, save_binding, save_tModel, add_publisherAssertion, or set_publisherAssertions API call.  Multiple elements of the same type may be passed together if multiples are included in the same save invocation.

The optional authInfo argument is an element that contains an authentication token.  An authentication token is obtained using the get_authToken API call or through some other means external to this specification.  Providers of validate_values Web services that serve multiple registries and providers that restrict who can use their service may require authInfo for this API. 
* 
* <p class="MsoBodyText">The called Web service for a checked value set performs
validation on all of the keyedReferences or keyedReferenceGroups that are
associated with the value sets the Web service is authorized to check.&nbsp; This
can involve merely checking that the <i>keyValue</i> values supplied are good for
the given value set (as signified by the embedded keyedReference tModelKey
values). Other types of validation as desired may be performed, including
context sensitive checks that utilize the information passed in the entity
being saved.</p>

<p class="MsoBodyText">The entity being saved may contain multiple references to
values from the value set(s) that the validation Web service is authorized to
validate.&nbsp; When the entity being saved is a businessEntity, contained
businessService and bindingTemplate entities may themselves reference values
from the authorized value sets as well.&nbsp; All references to values that are
associated with the value set(s) that the validation Web service is authorized
to check MUST be validated without regard to their placement in the entity
being saved.</p>

<p class="MsoBodyText">If the external value set and the node both support
caching of valid values, the node may not invoke validate_values if it already
knows that the referenced values are valid, through checking its cache.</p>

<p class="MsoBodyText">A checked category group system is treated in the same manner
as a checked value set.&nbsp; The tModelKey associated with the keyedReferenceGroup
identifies the checked category group system. A node may be able to validate a
reference to a cacheable checked category group system without calling
validate_values if it can determine using its cache that the tModelKey
attributes from the keyedReference elements contained in the
keyedReferenceGroup are allowed for the category group system.</p>
     * @return
     *     returns org.uddi.api_v3.DispositionReport
     * If all values referenced in the entity being saved are valid from the value set(s) or category group system(s) that the validation Web service is authorized to validate, the proper response is an empty message.  
     * @throws DispositionReportFaultMessage, RemoteException
     * <p class="MsoBodyText">If any error is found, or the called Web service needs to
signal that the information being saved is not valid based on the validation
algorithm chosen by the external Web service provider, then the Web service
MUST raise a SOAP Fault as specified in Section <a href="#_Ref8979985 ">4.8</a>
<i>Success and Error Reporting.</i></p>

<p class="MsoBodyText">When an error is signaled in this fashion, the UDDI node MUST
reject the pending change and return to the original caller the same SOAP fault data returned by the validation Web service.&nbsp; The error codes indicate one of the
following reasons, and the error text clearly indicates the keyedReference or
keyedReferenceGroup data that is being rejected and the reason it is being
rejected.</p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b>E_invalidValue</b>: One or more of the keyValues in the
keyedReference or keyedReferences in the keyedReferenceGroup supplied failed
validation.&nbsp; Only the first error encountered need be reported.</p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b>E_valueNotAllowed</b>: The values may be valid, but are not
allowed contextually.</p>
     */
    @WebMethod(operationName = "validate_values", action = "validate_values")
    @WebResult(name = "dispositionReport", targetNamespace = "urn:uddi-org:api_v3", partName = "body")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public DispositionReport validateValues(
        @WebParam(name = "validate_values", targetNamespace = "urn:uddi-org:vs_v3", partName = "body")
        ValidateValues body)
        throws DispositionReportFaultMessage, RemoteException
    ;

}
