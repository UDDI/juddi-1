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
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import org.uddi.custody_v3.DiscardTransferToken;
import org.uddi.custody_v3.KeyBag;
import org.uddi.custody_v3.TransferEntities;


/**
 * This portType defines all of the UDDI custody transfer operations.
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.5-b03-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "UDDI_CustodyTransfer_PortType", targetNamespace = "urn:uddi-org:custody_v3_portType")
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
public interface UDDICustodyTransferPortType extends Remote{


    /**
     * 
     * @param body
     * @throws DispositionReportFaultMessage, RemoteException
     */
    @WebMethod(operationName = "discard_transferToken", action = "discard_transferToken")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public void discardTransferToken(
        @WebParam(name = "discard_transferToken", targetNamespace = "urn:uddi-org:custody_v3", partName = "body")
        DiscardTransferToken body)
        throws DispositionReportFaultMessage, RemoteException
    ;

    /**
     * 
     * @param nodeID
     * @param opaqueToken
     * @param keyBag
     * @param expirationTime
     * @param authInfo
     * @throws DispositionReportFaultMessage, RemoteException
     */
    @WebMethod(operationName = "get_transferToken", action = "get_transferToken")
    @RequestWrapper(localName = "get_transferToken", targetNamespace = "urn:uddi-org:custody_v3", className = "org.uddi.custody_v3.GetTransferToken")
    @ResponseWrapper(localName = "transferToken", targetNamespace = "urn:uddi-org:custody_v3", className = "org.uddi.custody_v3.TransferToken")
    public void getTransferToken(
        @WebParam(name = "authInfo", targetNamespace = "urn:uddi-org:api_v3")
        String authInfo,
        @WebParam(name = "keyBag", targetNamespace = "urn:uddi-org:custody_v3")
        KeyBag keyBag,
        @WebParam(name = "nodeID", targetNamespace = "urn:uddi-org:api_v3", mode = WebParam.Mode.OUT)
        Holder<String> nodeID,
        @WebParam(name = "expirationTime", targetNamespace = "urn:uddi-org:custody_v3", mode = WebParam.Mode.OUT)
        Holder<XMLGregorianCalendar> expirationTime,
        @WebParam(name = "opaqueToken", targetNamespace = "urn:uddi-org:custody_v3", mode = WebParam.Mode.OUT)
        Holder<byte[]> opaqueToken)
        throws DispositionReportFaultMessage, RemoteException
    ;

    /**
     * 
     * @param body
     * @throws DispositionReportFaultMessage, RemoteException
     */
    @WebMethod(operationName = "transfer_entities", action = "transfer_entities")
    @SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
    public void transferEntities(
        @WebParam(name = "transfer_entities", targetNamespace = "urn:uddi-org:custody_v3", partName = "body")
        TransferEntities body)
        throws DispositionReportFaultMessage, RemoteException
    ;

}
