/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.uima.adapter.jms.activemq;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.springframework.jms.support.destination.DestinationResolver;

public class TempDestinationResolver implements DestinationResolver {
  private UimaDefaultMessageListenerContainer listener;

  private Destination destination = null;

  private Object mutex = new Object();

  private String serviceName = "";
  
  private String endpoint = "";
  
  public TempDestinationResolver() {
  }
  public TempDestinationResolver(String name, String endpoint) {
	  serviceName = name;
	  this.endpoint = endpoint;
  }
  /**
   * This method is called by the Spring listener code. It creates a single temp queue for all
   * listener instances. If the Spring listener is configured with more than one concurrentConsumer,
   * this method will be called more than once. The temp queue is created only once and cached on
   * the first call. Subsequent requests receive the same queue.
   */
  public Destination resolveDestinationName(Session session, String destinationName,
          boolean pubSubDomain) throws JMSException {
	  System.out.println("************ resolveDestinationName() Controller:"+serviceName+" Endpoint:"+endpoint+"************************");
	  try {
		  synchronized (mutex) {
		    //  if (destination == null) {
			  if ( listener.getDestination() == null || ((ActiveMQDestination)listener.getDestination()).isTemporary() ) {
		        destination = session.createTemporaryQueue();
		        if (listener != null) {
		          listener.setDestination(destination);
		        }
		      }
		  }
	  } catch( Exception e) {
		  e.printStackTrace();
		  throw e;
	  }
    return destination;
  }

  public void setListener(UimaDefaultMessageListenerContainer aListener) {
    listener = aListener;
  }

  /**
   * called from Spring during initialization
   * 
   * @param aFactory
   */
  public void setConnectionFactory(ActiveMQConnectionFactory aFactory) {
  }
}
