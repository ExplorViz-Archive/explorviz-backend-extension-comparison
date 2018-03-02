package net.exlorviz.extension.comparison.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.model.CommunicationClazz;

public class CommunicationMergerTest extends MergerTest {

	@Test
	public void testAppMergeOriginalCommunication() {
		// communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
		// FromSub2ToDemo()
		final CommunicationClazz originalCommunication = mergedApplication.getCommunications().stream()
				.filter(c1 -> c1.getMethodName().equals("FromDemoToSub2()")).findFirst().get();
		assertEquals(Status.ORIGINAL, originalCommunication.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeAddedCommunication() {
		// communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
		// FromSub2ToDemo()
		final CommunicationClazz addedCommunication = mergedApplication.getCommunications().stream()
				.filter(c1 -> c1.getMethodName().equals("FromSub2ToDemo()")).findFirst().get();
		assertEquals(Status.ADDED, addedCommunication.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeEditedCommunication() {
		// communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
		// FromSub2ToDemo()
		final CommunicationClazz editedCommunication = mergedApplication.getCommunications().stream()
				.filter(c1 -> c1.getMethodName().equals("FromDemoToSubEdited()")).findFirst().get();
		assertEquals(Status.EDITED, editedCommunication.getExtensionAttributes().get("status"));

	}

	@Test
	public void testAppMergeDeletedCommunication() {
		final CommunicationClazz deletedCommunication = mergedApplication.getCommunications().stream()
				.filter(c1 -> c1.getMethodName().equals("FromSub1ToSub3()")).findFirst().get();
		assertEquals(Status.DELETED, deletedCommunication.getExtensionAttributes().get("status"));
	}

}
