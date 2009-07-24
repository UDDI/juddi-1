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


package org.uddi.custody_v3;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for discard_transferToken complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="discard_transferToken">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:uddi-org:api_v3}authInfo" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element ref="{urn:uddi-org:custody_v3}transferToken"/>
 *           &lt;element ref="{urn:uddi-org:custody_v3}keyBag"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "discard_transferToken", propOrder = {
    "authInfo",
    "transferToken",
    "keyBag"
})
public class DiscardTransferToken implements Serializable{

	@XmlTransient
	private static final long serialVersionUID = 1L;
    @XmlElement(namespace = "urn:uddi-org:api_v3")
    protected String authInfo;
    protected TransferToken transferToken;
    protected KeyBag keyBag;

    /**
     * Gets the value of the authInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthInfo() {
        return authInfo;
    }

    /**
     * Sets the value of the authInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthInfo(String value) {
        this.authInfo = value;
    }

    /**
     * Gets the value of the transferToken property.
     * 
     * @return
     *     possible object is
     *     {@link TransferToken }
     *     
     */
    public TransferToken getTransferToken() {
        return transferToken;
    }

    /**
     * Sets the value of the transferToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransferToken }
     *     
     */
    public void setTransferToken(TransferToken value) {
        this.transferToken = value;
    }

    /**
     * Gets the value of the keyBag property.
     * 
     * @return
     *     possible object is
     *     {@link KeyBag }
     *     
     */
    public KeyBag getKeyBag() {
        return keyBag;
    }

    /**
     * Sets the value of the keyBag property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyBag }
     *     
     */
    public void setKeyBag(KeyBag value) {
        this.keyBag = value;
    }

}
