 /*
 * Copyright (C) 2010 Google Code.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.resting.component.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.CharArrayBuffer;

import com.google.resting.Resting;
import com.google.resting.component.EncodingTypes;
import com.google.resting.util.IOUtils;
/**
 * Wrapper object for REST response. This entity encapsulates the HTTP status code, the HTTP response body as a String and 
 * the HTTP headers of the REST response. {@link Resting} APIs like {@link Resting.get()}, {@link Resting.put()}, 
 * {@link Resting.post()} and {@link Resting.delete()} return an instance of this object. 
 * 
 * @author sujata.de
 * @since resting 0.1
 *
 */

public class ServiceResponse {
	
	private int statusCode = 500;
	private Header[] responseHeaders=null;
	private ContentData contentData=null;

	public ServiceResponse(HttpResponse response, EncodingTypes charset) {
		assert response!=null:"HttpResponse should not be null";
		InputStream inputStream=null;	
		try {
			if (response !=null){ 
				this.statusCode = response.getStatusLine().getStatusCode();
				this.responseHeaders=response.getAllHeaders();
				inputStream=response.getEntity().getContent();
				this.contentData=IOUtils.writeToContentData(inputStream, charset);
			}//if(response)
			else
			{
				throw new NullPointerException("HTTP response is null. Please check availability of endpoint service.");
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			IOUtils.closeQuietly(inputStream);
		}//try
		
	}//ServiceResponse
	
	/**
	 * This class encapsulates the content of the http response in bytes. Enables
	 * the builder pattern for creating an instance of ServiceResponse object.
	 * 
	 */
	public static class ContentData {
		private byte[] byteContent=null;
		private int contentLength=0;
		private EncodingTypes charset;
		private String stringContent=null;

		public ContentData(byte[] responseInBytes, EncodingTypes charset){
			this.byteContent=responseInBytes;
			this.contentLength=responseInBytes.length;
			this.charset=charset;
		}		
		
		public int getContentLength(){
			return contentLength;
		}//getContentLength
		
		public byte[] getContentInBytes(){
			return byteContent;
		}
		
		public String getContentInString(){
			if(stringContent==null)
				stringContent=IOUtils.writeToString(byteContent, charset);
			return stringContent;
		}
		
	
		@Override
		public String toString(){
			return getContentInString();	
		}//toString

	}//ContentData
	
	/**
	 * Get status code of http response
	 * 
	 * @return http status code
	 */
	public int getStatusCode() {
		return statusCode;
	}//getStatusCode
	
	/**
	 * Get the string representation of the HTTP response content. If the content encoding type is either binary (with or without printable characters) or 
	 * not among the other types defined in {@link com.google.resting.component.EncodingTypes}, then the string may be null. In that case, 
	 * the content can be retrieved as a byte array using {@link getResponseInBytes()}.
	 * 
	 * @return HTTP response content as a string 
	 */

	public String getResponseString() {
		String responseInString=contentData.getContentInString();
		assert responseInString!=null:"Response may be a binary stream. Can be retrieved using the getResponseInBytes() method.";
		return responseInString;
	}//getResponseInString
	
	/**
	 * Get the response headers of the HTTP response.
	 * 
	 * @return Array of response headers, in name-value pair in {@link org.apache.http.Header} objects.
	 */
	public Header[] getResponseHeaders(){
		assert responseHeaders.length>0:"Response headers can not be null.";
		return responseHeaders;
	}//getResponseHeaders
	
	/**
	 * Get the HTTP response content as a byte array.
	 * 
	 * @return HTTP response content as a byte array
	 */
	public byte[] getResponseInBytes(){
		byte[] responseInBytes=contentData.getContentInBytes();
		assert responseInBytes!=null:"Response can not be null";
		return responseInBytes;
	}//getResponseInBytes
	
	/**
	 * Returns the content length of the HTTP response
	 * 
	 * @return Content length
	 */
	public int getResponseLength(){
		return contentData.getContentLength();
	}//getResponseLengths
	
	@Override
	public String toString(){
		int length=150;
		length=contentData.getContentLength()+length;
		 for(Header header:responseHeaders){
			 length+=header.getName().length()+header.getValue().length()+3;
		 }
		CharArrayBuffer buffer= new CharArrayBuffer(length);
		buffer.append("\nServiceResponse\n---------------\nHTTP Status: ");
		buffer.append(statusCode);
		buffer.append("\nHeaders: \n");
		 for(Header header:responseHeaders){
			 buffer.append(header.getName());
			 buffer.append(" : ");
			 buffer.append(header.getValue());
			 buffer.append("\n");
		 }		
		
		buffer.append("Response body: \n");
		buffer.append(contentData);
		buffer.append("\n----------------\n");

		return buffer.toString();
	}//toString

}//ServiceResponse
