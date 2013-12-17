package org.apache.juddi.v3.tck;

/*
 * Copyright 2001-2009 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import javax.xml.ws.Endpoint;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

import java.net.InetAddress;
import java.util.List;
import java.util.Locale;
import javax.xml.ws.BindingProvider;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.pop3.POP3Client;
import org.apache.commons.net.pop3.POP3MessageInfo;
import org.apache.commons.net.pop3.POP3SClient;
import org.apache.juddi.v3.client.UDDIConstants;
import static org.apache.juddi.v3.tck.TckBusiness.MARY_BUSINESS_XML;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uddi.api_v3.FindQualifiers;
import org.uddi.api_v3.FindService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceList;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;

/**
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UDDI_090_SubscriptionListenerExternalTest {

        public UDDI_090_SubscriptionListenerExternalTest() {
                serialize = false;
                if (System.getProperty("debug") != null
                        && System.getProperty("debug").equalsIgnoreCase("true")) {
                        serialize = true;
                }
        }
        private static Log logger = LogFactory.getLog(UDDI_090_SubscriptionListenerExternalTest.class);
        private static UDDISubscriptionPortType subscriptionMary = null;
        private static UDDIInquiryPortType inquiryMary = null;
        private static TckTModel tckTModelMary = null;
        private static TckBusiness tckBusinessMary = null;
        private static TckBusinessService tckBusinessServiceMary = null;
        private static TckSubscriptionListener tckSubscriptionListenerMary = null;
        private static String hostname = null;
        private static UDDISubscriptionPortType subscriptionJoe = null;
        private static UDDIInquiryPortType inquiryJoe = null;
        private static TckTModel tckTModelJoe = null;
        private static TckBusiness tckBusinessJoe = null;
        private static TckBusinessService tckBusinessServiceJoe = null;
        private static TckSubscriptionListener tckSubscriptionListenerJoe = null;
        private static Endpoint endPoint;
        private static String authInfoJoe = null;
        private static String authInfoMary = null;
        private static UDDIClient manager;
        private static String email = null;
        private static Integer httpPort = 80;
        private static boolean serialize = false;

        @AfterClass
        public static void stopManager() throws ConfigurationException {
                manager.stop();
                //shutting down the TCK SubscriptionListener
                endPoint.stop();
                endPoint = null;
        }

        @BeforeClass
        public static void startManager() throws ConfigurationException {
                try {
                        httpPort = 9600 + new Random().nextInt(99);

                        hostname = TckPublisher.getProperties().getProperty("bindaddress");
                        if (hostname==null)
                                hostname=InetAddress.getLocalHost().getHostName();

                        //bring up the TCK SubscriptionListener
                        String httpEndpoint = "http://" + hostname + ":" + httpPort + "/tcksubscriptionlistener";
                        System.out.println("Bringing up SubscriptionListener endpoint at " + httpEndpoint);
                        endPoint = Endpoint.publish(httpEndpoint, new UDDISubscriptionListenerImpl());

                        manager = new UDDIClient();
                        manager.start();

                        logger.debug("Getting auth tokens..");


                        Transport transport = manager.getTransport();
                        UDDISecurityPortType security = transport.getUDDISecurityService();
                        authInfoJoe = TckSecurity.getAuthToken(security, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        authInfoMary = TckSecurity.getAuthToken(security, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        Assert.assertNotNull(authInfoJoe);



                        UDDIPublicationPortType publication = transport.getUDDIPublishService();
                        inquiryJoe = transport.getUDDIInquiryService();
                        subscriptionJoe = transport.getUDDISubscriptionService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) inquiryJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                                TckSecurity.setCredentials((BindingProvider) subscriptionJoe, TckPublisher.getJoePublisherId(), TckPublisher.getJoePassword());
                        }


                        tckTModelJoe = new TckTModel(publication, inquiryJoe);
                        tckBusinessJoe = new TckBusiness(publication, inquiryJoe);
                        tckBusinessServiceJoe = new TckBusinessService(publication, inquiryJoe);
                        tckSubscriptionListenerJoe = new TckSubscriptionListener(subscriptionJoe, publication);




                        publication = transport.getUDDIPublishService();
                        inquiryMary = transport.getUDDIInquiryService();
                        subscriptionMary = transport.getUDDISubscriptionService();
                        if (!TckPublisher.isUDDIAuthMode()) {
                                TckSecurity.setCredentials((BindingProvider) publication, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                                TckSecurity.setCredentials((BindingProvider) inquiryJoe, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                                TckSecurity.setCredentials((BindingProvider) subscriptionJoe, TckPublisher.getMaryPublisherId(), TckPublisher.getMaryPassword());
                        }


                        tckTModelMary = new TckTModel(publication, inquiryMary);
                        tckBusinessMary = new TckBusiness(publication, inquiryMary);
                        tckBusinessServiceMary = new TckBusinessService(publication, inquiryMary);
                        tckSubscriptionListenerMary = new TckSubscriptionListener(subscriptionMary, publication);

                        email = TckPublisher.getProperties().getProperty("mail.to");

                } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        Assert.fail("Could not obtain authInfo token.");
                }
        }

        public static void removeAllExistingSubscriptions(String authinfo, UDDISubscriptionPortType sub) {
                List<Subscription> subscriptions;
                try {
                        subscriptions = sub.getSubscriptions(authinfo);

                        DeleteSubscription ds = new DeleteSubscription();
                        ds.setAuthInfo(authinfo);
                        for (int i = 0; i < subscriptions.size(); i++) {
                                ds.getSubscriptionKey().add(subscriptions.get(i).getSubscriptionKey());
                        }
                        if (!subscriptions.isEmpty()) {
                                logger.info("Purging " + subscriptions.size() + " old subscriptions");
                                sub.deleteSubscription(ds);
                        }
                } catch (Exception ex) {
                        logger.warn("error clearing subscriptions", ex);
                }
        }

        @Test
        public void joePublisherUpdateService_HTTP_FIND_SERVICE() {
                logger.info("joePublisherUpdateService_HTTP_FIND_SERVICE");
                try {
                        removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        //Saving the binding template that will be called by the server for a subscription event
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the HTTP Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, "uddi_data/subscriptionnotifier/listenerService.xml", httpPort, hostname);
                        //Saving the HTTP Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, "uddi_data/subscriptionnotifier/subscription1.xml");
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Updating Service ********** ");
                        tckBusinessServiceJoe.updateJoePublisherService(authInfoJoe, "foo");

                        //waiting up to 100 seconds for the listener to notice the change.
                        for (int i = 0; i < 200; i++) {
                                Thread.sleep(500);
                                System.out.print(".");
                                if (UDDISubscriptionListenerImpl.notificationCount > 0) {
                                        logger.info("Received HTTP Notification");
                                        break;
                                }
                        }
                        if (UDDISubscriptionListenerImpl.notificationCount == 0) {
                                Assert.fail("No HttpNotification was sent");
                        }
                        if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("<name xml:lang=\"en\">Service One</name>")) {
                                Assert.fail("Notification does not contain the correct service");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, "uddi:uddi.joepublisher.com:subscriptionone");
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        public static final void printMessageInfo(BufferedReader reader, int id) throws IOException {
                String from = "";
                String subject = "";
                String line;
                while ((line = reader.readLine()) != null) {
                        String lower = line.toLowerCase(Locale.ENGLISH);
                        if (lower.startsWith("from: ")) {
                                from = line.substring(6).trim();
                        } else if (lower.startsWith("subject: ")) {
                                subject = line.substring(9).trim();
                        }
                }

                System.out.println(Integer.toString(id) + " From: " + from + "  Subject: " + subject);
        }

        /**
         * gets all current messages from the mail server and returns return
         * String is the body of each message
         */
        private static int fetchMail(String contains) {
                /*if (args.length < 3)
                 {
                 System.err.println(
                 "Usage: POP3Mail <pop3 server hostname> <username> <password> [TLS [true=implicit]]");
                 System.exit(1);
                 }*/
                Properties properties = TckPublisher.getProperties();

                String server = properties.getProperty("mail.host");
                String username = properties.getProperty("mail.username");
                String password = properties.getProperty("mail.password");

                String proto = properties.getProperty("mail.secureProtocol");
                boolean implicit = false;
                try {
                        implicit = Boolean.parseBoolean(properties.getProperty("mail.secureProtocol"));
                } catch (Exception ex) {
                }
                POP3Client pop3;

                if (proto != null) {
                        System.out.println("Using secure protocol: " + proto);
                        pop3 = new POP3SClient(proto, implicit);
                } else {
                        pop3 = new POP3Client();
                }
                System.out.println("Connecting to server " + server + " on " + pop3.getDefaultPort());

                // We want to timeout if a response takes longer than 60 seconds
                pop3.setDefaultTimeout(60000);

                // suppress login details
                pop3.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

                try {
                        pop3.connect(server);
                } catch (IOException e) {
                        logger.error(e);
                        Assert.fail("Could not connect to mail server." + e.getMessage());
                }

                try {
                        if (!pop3.login(username, password)) {
                                System.err.println("Could not login to server.  Check password.");
                                pop3.disconnect();

                                Assert.fail("Could not connect to mail server. check password");
                        }

                        POP3MessageInfo[] messages = pop3.listMessages();

                        if (messages == null) {
                                logger.warn("Could not retrieve message list.");
                                pop3.disconnect();
                                return 0;
                        } else if (messages.length == 0) {
                                logger.info("No messages");
                                pop3.logout();
                                pop3.disconnect();
                                return 0;
                        }

                        int ret = 0;
                        for (POP3MessageInfo msginfo : messages) {
                                /*BufferedReader reader = (BufferedReader) pop3.retrieveMessageTop(msginfo.number, 0);

                                 if (reader == null) {
                                 logger.error("Could not retrieve message header.");
                                 pop3.disconnect();
                                 return 0;
                                 }*/
                                //printMessageInfo(reader, msginfo.number);
                                BufferedReader reader = (BufferedReader) pop3.retrieveMessage(msginfo.number);
                                String line = "";
                                StringBuilder sb = new StringBuilder();
                                while ((line = reader.readLine()) != null) {
                                        String lower = line.toLowerCase(Locale.ENGLISH);
                                        sb.append(lower);
                                }


                                if (serialize) {
                                        logger.info("Email contents: " + sb.toString());
                                }
                                if (sb.toString().contains(contains.toLowerCase())) {
                                        ret++;
                                        pop3.deleteMessage(msginfo.number);
                                }
                        }

                        pop3.logout();
                        pop3.disconnect();
                        return ret;
                } catch (IOException e) {
                        logger.error(e);
                        return 0;
                }
        }

        @Test
        public void joePublisherUpdateService_SMTP_FIND_SERVICE() {
                Assume.assumeNotNull(email);
                logger.info("joePublisherUpdateService_SMTP_FIND_SERVICE");
                try {
                        removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);

                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the SMTP Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_EXTERNAL_XML, 0, email);
                        //Saving the SMTP Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_SMTP_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Updating Service ********** ");
                        tckBusinessServiceJoe.updateJoePublisherService(authInfoJoe, "foo");

                        //waiting up to 100 seconds for the listener to notice the change.
                        boolean received = false;
                        for (int i = 0; i < 200; i++) {
                                Thread.sleep(500);
                                System.out.print(".");
                                if (fetchMail("Service One") > 0) {
                                        logger.info("Received Email Notification");
                                        received = true;
                                        break;
                                }
                        }
                        Assert.assertTrue("No email was received", received);

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_SMTP_KEY);
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                }
        }

        //   @Test
        public void joePublisherUpdateBusiness_HTTP_FIND_BUSINESS() {
                logger.info("joePublisherUpdateBusiness_HTTP_FIND_BUSINESS");
                try {
                        removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                        DumpAllBusinesses();
                        Thread.sleep(5000);
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        logger.info("Saving Joe's callback endpoint ********** ");
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, "uddi_data/subscriptionnotifier/listenerService.xml", httpPort, hostname);
                        //Saving the Subscription
                        logger.info("Saving Joe's subscription********** ");
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, "uddi_data/subscriptionnotifier/subscription2.xml");
                        //Changing the service we subscribed to "JoePublisherService"
                        DumpAllBusinesses();
                        logger.info("Clearing the inbox********** ");
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        Thread.sleep(2000);
                        logger.info("Saving Mary's Business ********** ");
                        tckBusinessMary.saveMaryPublisherBusiness(authInfoMary);
                        DumpAllBusinesses();
                        //waiting up to 10 seconds for the listener to notice the change.
                        String test = "";
                        for (int i = 0; i < 20; i++) {
                                Thread.sleep(500);
                                System.out.print(".");
                                if (UDDISubscriptionListenerImpl.notificationCount > 0) {
                                        //logger.info("Received Notification");
                                        //break;
                                } else {
                                        System.out.print(test);
                                }
                        }
                        logger.info("RX " + UDDISubscriptionListenerImpl.notificationCount + " notifications");
                        Iterator<String> it = UDDISubscriptionListenerImpl.notifcationMap.values().iterator();
                        while (it.hasNext()) {
                                logger.info("Notification: " + it.next());
                        }
                        DumpAllBusinesses();
                        if (UDDISubscriptionListenerImpl.notificationCount == 0) {
                                Assert.fail("No Notification was sent");
                        }
                        if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("uddi:uddi.marypublisher.com:marybusinessone")) {
                                Assert.fail("Notification does not contain the correct service");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, "uddi:uddi.joepublisher.com:subscriptionone");
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                }
        }

        private static void DumpAllBusinesses() {
                logger.warn("Dumping the business/service list for debugging");
                FindService fs = new FindService();
                fs.setFindQualifiers(new FindQualifiers());
                fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
                fs.getName().add(new Name("%", null));
                try {
                        ServiceList findService = inquiryJoe.findService(fs);
                        if (findService.getServiceInfos() == null) {
                                logger.warn("NO SERVICES RETURNED!");
                        } else {
                                for (int i = 0; i < findService.getServiceInfos().getServiceInfo().size(); i++) {
                                        logger.warn(findService.getServiceInfos().getServiceInfo().get(i).getName().get(0).getValue() + " lang="
                                                + findService.getServiceInfos().getServiceInfo().get(i).getName().get(0).getLang() + " "
                                                + findService.getServiceInfos().getServiceInfo().get(i).getServiceKey() + " "
                                                + findService.getServiceInfos().getServiceInfo().get(i).getBusinessKey());
                                }
                        }
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
        }

        //@Test
        public void joePublisherUpdateBusiness_SMTP_FIND_BUSINESS() {
                Assume.assumeNotNull(email);
                logger.info("joePublisherUpdateBusiness_SMTP_FIND_BUSINESS");
                try {
                        removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_EXTERNAL_XML, 0, email);
                        //tckSubscriptionListener.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort);
                        //Saving the Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION2_SMTP_XML);
                        //tckSubscriptionListener.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_XML);

                        Thread.sleep(3000);
                        logger.info("Saving Mary's Business ********** ");
                        tckBusinessMary.saveBusiness(authInfoMary, MARY_BUSINESS_XML, "uddi:uddi.marypublisher.com:marybusinessone");

                        boolean received = false;
                        for (int i = 0; i < 200; i++) {
                                Thread.sleep(500);
                                System.out.print(".");
                                if (fetchMail("uddi:uddi.marypublisher.com:marybusinessone") > 0) {
                                        logger.info("Received Email Notification");
                                        received = true;
                                        break;
                                }
                        }
                        Assert.assertTrue("No email was received", received);

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_SMTP_KEY);
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        //      tckTModel.deleteJoePublisherTmodel(authInfoJoe);
                        tckBusinessMary.deleteMaryPublisherBusiness(authInfoMary);
                }
        }

        //tmodel tests
        //@Test
        public void joePublisherUpdateBusiness_HTTP_FIND_TMODEL() {
                logger.info("joePublisherUpdateBusiness_HTTP_FIND_TMODEL");
                removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                try {
                        UDDISubscriptionListenerImpl.notifcationMap.clear();
                        UDDISubscriptionListenerImpl.notificationCount = 0;
                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_HTTP_SERVICE_XML, httpPort, hostname);
                        //Saving the Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Deleting tModel ********** ");
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY);

                        //waiting up to 100 seconds for the listener to notice the change.
                        String test = "";
                        for (int i = 0; i < 200; i++) {
                                Thread.sleep(500);
                                System.out.print(".");
                                if (UDDISubscriptionListenerImpl.notificationCount > 0) {
                                        logger.info("Received Notification");
                                        break;
                                } else {
                                        System.out.print(test);
                                }
                        }
                        if (UDDISubscriptionListenerImpl.notificationCount == 0) {
                                Assert.fail("No Notification was sent");
                        }
                        if (!UDDISubscriptionListenerImpl.notifcationMap.get(0).contains("<name xml:lang=\"en\">tModel One</name>")) {
                                Assert.fail("Notification does not contain the correct service");
                        }

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_KEY);
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                }
        }

        //@Test
        public void joePublisherUpdateBusiness_SMTP_FIND_TMODEL() {
                Assume.assumeNotNull(email);
                logger.info("joePublisherUpdateBusiness_SMTP_FIND_TMODEL");
                removeAllExistingSubscriptions(authInfoJoe, subscriptionJoe);
                try {

                        tckTModelJoe.saveJoePublisherTmodel(authInfoJoe);
                        tckTModelJoe.saveTModels(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                        tckBusinessJoe.saveJoePublisherBusiness(authInfoJoe);
                        tckBusinessServiceJoe.saveJoePublisherService(authInfoJoe);
                        //Saving the Listener Service
                        tckSubscriptionListenerJoe.saveService(authInfoJoe, TckSubscriptionListener.LISTENER_SMTP_SERVICE_EXTERNAL_XML, 0, email);
                        //Saving the Subscription
                        tckSubscriptionListenerJoe.saveNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION3_SMTP_XML);
                        //Changing the service we subscribed to "JoePublisherService"
                        Thread.sleep(1000);
                        logger.info("Deleting tModel ********** ");
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY);

                        boolean received = false;
                        for (int i = 0; i < 200; i++) {
                                Thread.sleep(500);
                                System.out.print(".");
                                if (fetchMail("tModel One") > 0) {
                                        logger.info("Received Email Notification");
                                        received = true;
                                        break;
                                }
                        }
                        Assert.assertTrue("No email was received", received);

                } catch (Exception e) {
                        logger.error("No exceptions please.");
                        e.printStackTrace();

                        Assert.fail();
                } finally {
                        tckSubscriptionListenerJoe.deleteNotifierSubscription(authInfoJoe, TckSubscriptionListener.SUBSCRIPTION_SMTP_KEY);
                        tckBusinessServiceJoe.deleteJoePublisherService(authInfoJoe);
                        tckBusinessJoe.deleteJoePublisherBusiness(authInfoJoe);
                        tckTModelJoe.deleteTModel(authInfoJoe, TckTModel.JOE_PUBLISHER_TMODEL_SUBSCRIPTION3_TMODEL_KEY, TckTModel.JOE_PUBLISHER_TMODEL_XML_SUBSCRIPTION3);
                        tckTModelJoe.deleteJoePublisherTmodel(authInfoJoe);
                }
        }
        //TODO If a subscriber specifies a maximum number of entries to be returned with a subscription and the amount of data to be returned exceeds this limit, or if the node determines based on its policy that there are too many entries to be returned in a single group, then the node SHOULD provide a chunkToken with results.  
        //TODO  If no more results are pending, the value of the chunkToken MUST be "0".
}