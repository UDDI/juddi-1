package org.apache.juddi.test;

import java.util.List;
import javax.xml.bind.JAXBException;

import org.apache.juddi.api.impl.UDDIPublicationImpl;
import org.apache.juddi.api.impl.UDDIInquiryImpl;
import org.testng.Assert;
import org.testng.annotations.*;
import static junit.framework.Assert.assertEquals;

import org.apache.juddi.api.datatype.*;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class PublisherTest {
	private UDDIPublicationImpl publish = new UDDIPublicationImpl();
	private UDDIInquiryImpl inquiry = new UDDIInquiryImpl();
	
	@Parameters({ "publisherFile", "publisherId" })
	@Test
	public void savePublisher(String publisherFile, String publisherId) {
		try {
			SavePublisher sp = new SavePublisher();
			Publisher pubIn = (Publisher)UDDIApiTestHelper.buildEntityFromDoc(publisherFile, "org.apache.juddi.api.datatype");
			sp.getPublisher().add(pubIn);
			publish.savePublisher(sp);
	
			// Now get the entity and check the values
			GetPublisherDetail gp = new GetPublisherDetail();
			gp.getPublisherId().add(publisherId);
			PublisherDetail pd = inquiry.getPublisherDetail(gp);
			List<Publisher> pubOutList = pd.getPublisher();
			Publisher pubOut = pubOutList.get(0);

			assertEquals(pubIn.getPublisherId(), pubOut.getPublisherId());
			assertEquals(pubIn.getPublisherName(), pubOut.getPublisherName());
			assertEquals(pubIn.getEmailAddress(), pubOut.getEmailAddress());
			assertEquals(pubIn.getIsAdmin(), pubOut.getIsAdmin());
			assertEquals(pubIn.getIsEnabled(), pubOut.getIsEnabled());
			assertEquals(pubIn.getMaxBindingsPerService(), pubOut.getMaxBindingsPerService());
			assertEquals(pubIn.getMaxBusinesses(), pubOut.getMaxBusinesses());
			assertEquals(pubIn.getMaxServicePerBusiness(), pubOut.getMaxServicePerBusiness());
			assertEquals(pubIn.getMaxTModels(), pubOut.getMaxTModels());
			
		}
		catch(DispositionReportFaultMessage dr) {
			Assert.fail("No exception should be thrown", dr);
		}
		catch(JAXBException je) {
			Assert.fail("No exception should be thrown", je);
		}

	}

	@Parameters({ "publisherId" })
	@Test
	public void deletePublisher(String publisherId) {
		try {
			// Delete the entity and make sure it is removed
			DeletePublisher dp = new DeletePublisher();
			dp.getPublisherId().add(publisherId);
			publish.deletePublisher(dp);
		}
		catch(DispositionReportFaultMessage dr) {
			Assert.fail("No exception should be thrown", dr);
		}
		
	}

}
