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
package org.apache.juddi.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.IdentifierBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.error.RegistryException; 

/**
 * This is a simple implementation of jUDDI's Validation interface.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class DefaultValidator implements Validator
{
  // private reference to the jUDDI logger
  private static Log log = LogFactory.getLog(DefaultValidator.class);

  /**
   *
   */
  public DefaultValidator()
  {
  }

  /**
   *
   */
  public boolean validate(CategoryBag bag)
    throws RegistryException
  {
    return true;
  }

  /**
   *
   */
  public boolean validate(IdentifierBag bag)
    throws RegistryException
  {
    return true;
  }

  /**
   *
   */
  public boolean validate(KeyedReference ref)
    throws RegistryException
  {
    return true;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
    throws Exception
  {
    Validator validation = new DefaultValidator();
  }
}