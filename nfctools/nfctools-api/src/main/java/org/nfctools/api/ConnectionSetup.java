/**
 * Copyright 2011-2012 Adrian Stabiszewski, as@nfctools.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nfctools.api;

public class ConnectionSetup {

	public byte[] mifareParams;
	public byte[] felicaParams;
	public byte[] nfcId3t;
	public byte[] generalBytes;

	public ConnectionSetup(byte[] mifareParams, byte[] felicaParams, byte[] nfcId3t, byte[] generalBytes) {
		this.mifareParams = mifareParams;
		this.felicaParams = felicaParams;
		this.nfcId3t = nfcId3t;
		this.generalBytes = generalBytes;
	}
}
