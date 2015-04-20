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
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import org.uddi.vscache_v3.ValidValue;


/**
 * This portType defines all of the UDDI value set caching operations.
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
* 
* <p class="MsoBodyText">The Application Programming Interfaces in this section
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
 */
@WebService(name = "UDDI_ValueSetCaching_PortType", targetNamespace = "urn:uddi-org:v3_service")
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
public interface UDDIValueSetCachingPortType extends Remote {


    /**
     * <p class="MsoBodyText">A UDDI node that supports external value sets MAY invoke a get_allValidValues Web service offered by a value set provider that has granted
permission to that registry to cache the valid values for that value set.&nbsp; The
external value set provider MAY offer the get_allValidValues Web service and
the UDDI node MAY use it.&nbsp; The normal use is to return a full set of valid
values for the identified value set.&nbsp; If the value set provider determines
there are too many values to return in one chunk, the set of valid values may
be returned in chunks.</p>

<p class="MsoBodyText">Registry policy may require the value set provider that
offers a get_allValidValues Web service to republish its value set tModel when
the cache should be re-acquired by participating nodes.&nbsp; See Section <a href="#_Ref9007695 ">9.6.5</a> <i>Value Sets</i> for more information.</p>

<p class="MsoBodyText">get_allValidValues can similarly be used to obtain the set
of tModelKeys for value sets that can participate in a cached category group
system.</p>
* The called Web service returns the set of valid values in a validValuesList on success.  This structure lists every valid value associated with the value set or category group system that is described by the tModelKey provided.  In the event too many values exist to be returned in a single response (i.e., the message size exceeds the maximum number of bytes allowed by the UDDI registry), or the value set provider wants to supply the values in multiple packets, then the validValueList includes the chunkToken element and the API can be re-issued to get the remaining valid values.
* 
* <h5 style="margin-left:0in;text-indent:0in">Chunking of valid values</h5>

<p class="MsoBodyText">If the value set provider determines that there are too
many values to be returned in a single group, then the provider SHOULD provide
a chunkToken with the results.&nbsp; The chunkToken is a string based token which is
used by the value set provider to maintain the state of the set of values for a
particular caller, when these results are chunked across multiple responses.&nbsp;
Providers should establish their own policies for determining the content and
format of the chunkToken. The chunkToken returned with a particular value set
result set SHOULD be used to retrieve subsequent results.&nbsp; If no more results
are pending, the value of the chunkToken will be "0" or the
chunkToken will be absent.&nbsp; </p>

<p class="MsoBodyText">A chunkToken is intended as a short-term aid in obtaining
contiguous results across multiple API calls and is therefore likely to remain
valid for only a short time.&nbsp; Value set providers may establish policies on how
long a chunkToken remains valid.</p>
     * @param tModelKey          tModelKey:  A required uddiKey value that identifies the specific instance of the tModel which describes the value set or category group system for which a Web service to get all valid values has been provided.  It uniquely identifies the category, identifier, or category group system for which valid values are being requested.
     * @param authInfo ·         authInfo: An optional element that contains an authentication token.  Authentication tokens are obtained using the get_authToken API call or through some other means external to this specification.  Providers of get_allValidValues Web services that serve multiple registries and providers that restrict who can use their service may require authInfo for this API. 
     * @param validValue RETURN TYPE <p class="MsoBodyText">A validValuesList structure is returned containing the set
of valid values for the external category or identifier system.&nbsp; The list MUST
contain a chunkToken if the Web service provider wishes to provide the data in
packets.&nbsp; The validValuesList has the form: </p>

<p class="MsoBodyText"><img src="http://uddi.org/pubs/uddi-v3.0.2-20041019_files/image106.gif" border="0" height="121" width="393"></p>

<p class="MsoBodyText">And its contained validValue element has the form:</p>

<p class="MsoBodyText"><img src="http://uddi.org/pubs/uddi-v3.0.2-20041019_files/image107.gif" border="0" height="71" width="347"></p>
     * @param chunkToken ·         chunkToken:  Optional element used to retrieve subsequent groups of data when the first invocation of this API indicates more data is available.  This occurs when a chunkToken is returned whose value is not "0" in the validValuesList structure described in the next section.  To retrieve the next chunk of data, the chunkToken returned should be used as an argument to the next invocation of this API.
     * @throws DispositionReportFaultMessage
     * <p class="MsoBodyText">If any error occurs in processing this API, a dispositionReport structure MUST be returned to the caller in a SOAP Fault.&nbsp; See Section <a href="#_Ref8980008 ">4.8</a> <i>Success and Error Reporting.&nbsp; </i>The following
error information is relevant: </p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b>E_invalidKeyPassed</b>: Signifies that the tModelKey passed
did not match with the uddiKey of any known tModels.&nbsp; The details on the
invalid key SHOULD be included in the dispositionReport element.</p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b>E_noValuesAvailable</b>: Signifies that no values could be
returned. </p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b>E_unsupported</b>: Signifies that the Web service does not
support this API.</p>

<p class="MsoBodyText" style="margin-left:1.0in;text-indent:-.25in"><span style="font-family:Symbol">·<span style="font:7.0pt &quot;Times New Roman&quot;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
</span></span><b>E_invalidValue</b>: Signifies that the chunkToken value
supplied is either invalid or has expired.</p>
     */
    @WebMethod(operationName = "get_allValidValues", action = "get_allValidValues")
    @RequestWrapper(localName = "get_allValidValues", targetNamespace = "urn:uddi-org:vscache_v3", className = "org.uddi.vscache_v3.GetAllValidValues")
    @ResponseWrapper(localName = "validValuesList", targetNamespace = "urn:uddi-org:vscache_v3", className = "org.uddi.vscache_v3.ValidValuesList")
    public void getAllValidValues(
        @WebParam(name = "authInfo", targetNamespace = "urn:uddi-org:api_v3")
        String authInfo,
        @WebParam(name = "tModelKey", targetNamespace = "urn:uddi-org:api_v3")
        String tModelKey,
        @WebParam(name = "chunkToken", targetNamespace = "urn:uddi-org:vscache_v3", mode = WebParam.Mode.INOUT)
        Holder<String> chunkToken,
        @WebParam(name = "validValue", targetNamespace = "urn:uddi-org:vscache_v3", mode = WebParam.Mode.OUT)
        Holder<List<ValidValue>> validValue)
        throws DispositionReportFaultMessage, RemoteException
    ;

}
