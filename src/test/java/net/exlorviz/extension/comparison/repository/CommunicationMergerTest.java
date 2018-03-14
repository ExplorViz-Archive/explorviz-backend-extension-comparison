package net.exlorviz.extension.comparison.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.model.CommunicationClazz;

/**
 * This class holds tests for all states a {@link CommunicationClazz} can adopt,
 * refer {@link Status.ORIGINAL}, {@link Status.ADDED}, {@link Status.EDITED}
 * and {@link Status.DELETED}. The merged application is taken from
 * {@link MergerTest}.
 *
 * @author josw
 *
 */
public class CommunicationMergerTest extends MergerTest {

	@Test
	public void testAppMergeOriginalCommunication() {
		// communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
		// FromSub2ToDemo()
		final CommunicationClazz originalCommunication = mergedApplication.getCommunications().stream()
				.filter(c1 -> c1.getMethodName().equals("FromDemoToSub2()")).findFirst().get();
		assertEquals(originalCommunication.getMethodName() + "is not ORIGINAL.", Status.ORIGINAL,
				originalCommunication.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeAddedCommunication() {
		// communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
		// FromSub2ToDemo()
		final CommunicationClazz addedCommunication = mergedApplication.getCommunications().stream()
				.filter(c1 -> c1.getMethodName().equals("FromSub2ToDemo()")).findFirst().get();
		assertEquals(addedCommunication.getMethodName() + "is not ADDED.", Status.ADDED,
				addedCommunication.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeEditedCommunication() {
		// communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
		// FromSub2ToDemo()
		final CommunicationClazz editedCommunication = mergedApplication.getCommunications().stream()
				.filter(c1 -> c1.getMethodName().equals("FromDemoToSubEdited()")).findFirst().get();
		assertEquals(editedCommunication.getMethodName() + "is not EDITED.", Status.EDITED,
				editedCommunication.getExtensionAttributes().get("status"));

	}

	@Test
	public void testAppMergeDeletedCommunication() {
		final CommunicationClazz deletedCommunication = mergedApplication.getCommunications().stream()
				.filter(c1 -> c1.getMethodName().equals("FromSub1ToSub3()")).findFirst().get();
		assertEquals(deletedCommunication.getMethodName() + "is not DELETED.", Status.DELETED,
				deletedCommunication.getExtensionAttributes().get("status"));
	}

}
