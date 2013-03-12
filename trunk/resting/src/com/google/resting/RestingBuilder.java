 /*
 * Copyright (C) 2013 Google Code.
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
package com.google.resting;

import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.google.resting.component.Alias;
import com.google.resting.component.EncodingTypes;
import com.google.resting.component.RequestParams;
import com.google.resting.component.Verb;
import com.google.resting.component.impl.json.JSONAlias;
import com.google.resting.helper.RestingHelper;
import com.google.resting.transform.TransformationType;

/**
  * <p>Use this builder to make a REST invocation and create a list of entities when you need to set configuration
 * options other than the default. For {@link Resting} with default configuration, it is simpler to
 * use {@code Resting.<method>}. {@code RestingBuilder} is best used by creating it, and then invoking its
 * various configuration methods, and finally calling build.</p>
 * 
 * <p>The following is an example shows how to use the {@code RestingBuilder}:
 * <pre>
 * List<Product> entities = new RestingBuilder("http://http://local.myapis.com/HellosService/V1/productInput",Product.class)
 *     .setPort(8080)
 *     .setVerb(Verb.POST)
 *     .setTransformationType(TransformationType.XML)
 *     .setConnectionTimeout(3000)
 *     .build();
 * </pre></p>
 *
 * <p>NOTE: the order of invocation of configuration methods does not matter.</p>
 * 
 * @author sujata.de
 * @since resting 0.7
 *
 * @param <T> Type of the target entities
 */

public final class RestingBuilder<T> {
	private String uri;
	private int port;
	private Verb verb;
	private EncodingTypes encoding;
	private TransformationType transformationType;
	private Class<T> targetType;
	private List<Header> additionalHeaders;
	private RequestParams requestParams;
	private HttpParams httpParams;
	private Alias alias;


	 /**
	   * Creates a RestingBuilder instance that can be used to build a Resting request with various configuration
	   * settings. RestingBuilder follows the builder pattern, and it is typically used by first
	   * invoking various configuration methods to set desired options, and finally calling
	   * {@link #build()}.
	   */	
	public RestingBuilder(String uri, Class<T> targetType){
		this.targetType=targetType;
		this.uri=uri;
		this.port=80;
		this.verb=Verb.GET;
		this.encoding=EncodingTypes.UTF8;
		this.transformationType=TransformationType.JSON;
		this.targetType=null;
		this.additionalHeaders=null;
		this.requestParams=null;
		this.httpParams=new BasicHttpParams();
		this.alias=null;
	}//RestingBuilder
	  /**
	   * Invokes REST service and creates a {@link List} of target entities based on the current configuration. This method is free of
	   * side-effects to this {@code RestingBuilder} instance and hence can be called multiple times.
	   *
	   * @return a list of target configured with the options currently set in this builder
	   */	
	public List<T> build(){
		return RestingHelper.executeAndTransform(uri, port, requestParams, verb, transformationType, targetType, alias, encoding, additionalHeaders, httpParams);
	}//build
	  
	/**
	   * Invokes REST service and creates a {@link Map} of target entities based on the current configuration. This method is free of
	   * side-effects to this {@code RestingBuilder} instance and hence can be called multiple times.
	   *
	   * @return a map of target configured with the options currently set in this builder
	*/	
	public Map<String, List> build(Map<String, Class> aliasTypeMap){
		if(transformationType == TransformationType.JSON){
			if(aliasTypeMap!=null){
				JSONAlias aliases=new JSONAlias(aliasTypeMap);
				return RestingHelper.executeAndTransform(uri, port, requestParams, verb, transformationType, aliases, encoding, additionalHeaders, httpParams);
			}//if aliasTypeMap
		}//if JSON 
		return null;
	}
	/**
	 * Sets the connection timeout. Default value is 0, indicating infinite timeout.
	 * 
	 * @param connectionTimeout
	 * @return a reference to this {@code RestingBuilder} object to fulfill the "Builder" pattern
	 */
	public RestingBuilder setConnectionTimeout(int connectionTimeout){
		HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
		return this;
	}//setConnectionTimeout
	
	/**
	 * Sets the socket timeout. Default value is 0, indicating infinite timeout.
	 * 
	 * @param socketTimeout
	 * @return a reference to this {@code RestingBuilder} object to fulfill the "Builder" pattern
	 */	
	public RestingBuilder setSocketTimeout(int socketTimeout){
		HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
		return this;
	}//setSocketTimeout

	/**
	 * Sets the port. Default value is 80.
	 * 
	 * @param port
	 * @return a reference to this {@code RestingBuilder} object to fulfill the "Builder" pattern
	 */	
	public RestingBuilder setPort(int port) {
		this.port = port;
		return this;
	}//setPort

	/**
	 * Sets the Verb. Default value is GET.
	 * 
	 * @param verb
	 * @return a reference to this {@code RestingBuilder} object to fulfill the "Builder" pattern
	 */
	public RestingBuilder setVerb(Verb verb) {
		this.verb = verb;
		return this;
	}//setVerb

	/**
	 * Sets the encoding tyoe. Default value is UTF-8.
	 * 
	 * @param connectionTimeout
	 * @return a reference to this {@code RestingBuilder} object to fulfill the "Builder" pattern
	 */
	public RestingBuilder setEncodingType(EncodingTypes encodingType) {
		this.encoding = encodingType;
		return this;
	}//setEncodingType

	/**
	 * Sets the transformation type. Default value is JSON.
	 * 
	 * @param transformationType
	 * @return a reference to this {@code RestingBuilder} object to fulfill the "Builder" pattern
	 */
	public RestingBuilder setTransformationType(TransformationType transformationType) {
		this.transformationType = transformationType;
		return this;
	}//setTransformationType

	/**
	 * Sets the additional headers. Default value is null.
	 * 
	 * @param additionalHeaders
	 * @return a reference to this {@code RestingBuilder} object to fulfill the "Builder" pattern
	 */	
	public RestingBuilder setAdditionalHeaders(List<Header> additionalHeaders) {
		this.additionalHeaders = additionalHeaders;
		return this;
	}//setAdditionalHeaders

	/**
	 * Sets the request params. Default value is null.
	 * 
	 * @param requestParams
	 * @return a reference to this {@code RestingBuilder} object to fulfill the "Builder" pattern
	 */
	public RestingBuilder setRequestParams(RequestParams requestParams) {
		this.requestParams = requestParams;
		return this;
	}//setRequestParams
}//RestingBuilder
