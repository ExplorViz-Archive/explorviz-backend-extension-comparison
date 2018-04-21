package net.exlorviz.extension.comparison.repository;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.model.application.ClazzCommunication;

/**
 * This class holds tests for all states a {@link ClazzCommunication} can adopt,
 * refer {@link Status.ORIGINAL}, {@link Status.ADDED}, {@link Status.EDITED}
 * and {@link Status.DELETED}. The merged application is taken from
 * {@link MergerTest}.
 *
 * @author josw
 *
 */
public class CommunicationMergerTest extends MergerTest {
	//
	// @Test
	// public void testAppMergeOriginalCommunication() {
	// // communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
	// // FromSub2ToDemo()
	// final ClazzCommunication originalCommunication = mergedCommunication.stream()
	// .filter(c1 ->
	// c1.getOperationName().equals("FromDemoToSub2()")).findFirst().get();
	// assertEquals(originalCommunication.getOperationName() + "is not ORIGINAL.",
	// Status.ORIGINAL,
	// originalCommunication.getExtensionAttributes().get(PrepareForMerger.STATUS));
	// }
	//
	// @Test
	// public void testAppMergeAddedCommunication() {
	// // communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
	// // FromSub2ToDemo()
	// final ClazzCommunication addedCommunication = mergedCommunication.stream()
	// .filter(c1 ->
	// c1.getOperationName().equals("FromSub2ToDemo()")).findFirst().get();
	// assertEquals(addedCommunication.getOperationName() + "is not ADDED.",
	// Status.ADDED,
	// addedCommunication.getExtensionAttributes().get(PrepareForMerger.STATUS));
	// }
	//
	// @Test
	// public void testAppMergeEditedCommunication() {
	// // communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
	// // FromSub2ToDemo()
	// final ClazzCommunication editedCommunication = mergedCommunication.stream()
	// .filter(c1 ->
	// c1.getOperationName().equals("FromDemoToSubEdited()")).findFirst().get();
	// assertEquals(editedCommunication.getOperationName() + "is not EDITED.",
	// Status.EDITED,
	// editedCommunication.getExtensionAttributes().get(PrepareForMerger.STATUS));
	//
	// }
	//
	// @Test
	// public void testAppMergeDeletedCommunication() {
	// final ClazzCommunication deletedCommunication = mergedCommunication.stream()
	// .filter(c1 ->
	// c1.getOperationName().equals("FromSub1ToSub3()")).findFirst().get();
	// assertEquals(deletedCommunication.getOperationName() + "is not DELETED.",
	// Status.DELETED,
	// deletedCommunication.getExtensionAttributes().get(PrepareForMerger.STATUS));
	// }

}
