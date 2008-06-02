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
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * ErrInfoHandler
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class ErrInfoHandler extends AbstractHandler
{
  public static final String TAG_NAME = "errInfo";

  protected ErrInfoHandler(HandlerMaker maker)
  {
  }

  public RegistryObject unmarshal(Element element)
  {
    ErrInfo obj = new ErrInfo();

    // Attributes
    obj.setErrCode(element.getAttribute("errCode"));

    // Text Node Value
    obj.setErrMsg(XMLUtils.getText(element));

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    ErrInfo errInfo = (ErrInfo)object;
    String generic = getGeneric(null);
    String namespace = getUDDINamespace(generic);
    Element element = parent.getOwnerDocument().createElementNS(namespace,TAG_NAME);

    String errCode = errInfo.getErrCode();
    if (errCode != null)
      element.setAttribute("errCode",errCode);

    String lineValue = errInfo.getErrMsg();
    if (lineValue != null)
      element.appendChild(parent.getOwnerDocument().createTextNode(lineValue));

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(ErrInfoHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;

    ErrInfo errInfo = new ErrInfo();
    errInfo.setErrCode("E_accountLimitExceeded");
    errInfo.setErrMsg("Authentication token information has timed out.");

    System.out.println();

    RegistryObject regObject = errInfo;
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

    System.out.println();
  }
}