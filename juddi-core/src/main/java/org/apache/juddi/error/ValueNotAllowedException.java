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

package org.apache.juddi.error;

/**
 *   E_valueNotAllowed: (20210) Signifies that a value did not pass validation because of contextual issues.  The value may be valid in some contexts, but 
 *   not in the context used.  The error text MAY contain information about the contextual problem.
 * 
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
public class ValueNotAllowedException extends RegistryException {

	private static final long serialVersionUID = 1L;

	public ValueNotAllowedException(ErrorMessage message) {
		super(message, UDDIErrorHelper.buildDispositionReport(UDDIErrorHelper.E_VALUE_NOT_ALLOWED));
	}
}
