package net.explorviz.extension.comparison.repository;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.extension.comparison.util.MergerHelper;
import net.explorviz.extension.comparison.util.PrepareForMerger;
import net.explorviz.model.application.AggregatedClazzCommunication;
import net.explorviz.model.application.ClazzCommunication;

/**
 * This class holds tests for all states a {@link ClazzCommunication} can adopt,
 * refer {@link Status.ORIGINAL}, {@link Status.ADDED} and
 * {@link Status.DELETED}. The merged application is taken from
 * {@link MergerTest}.
 *
 * @author josw
 *
 */
public class CommunicationMergerTest extends MergerTest {

	List<ClazzCommunication> flatClazzCommunications = new ArrayList<>();

	@Before
	public void createFlatClazzCommunication() {
		flatClazzCommunications = MergerHelper
				.createFlatClazzCommunications(mergedApplication.getAggregatedOutgoingClazzCommunications());
	}

	@Test
	public void testAppMergeOriginalCommunication() {
		final ClazzCommunication originalCommunication = flatClazzCommunications.stream()
				.filter(c1 -> "fromClazz1ToSubClazz1()".equals(c1.getOperationName())).findAny().get();
		assertEquals(originalCommunication.getOperationName() + "is not ORIGINAL.", Status.ORIGINAL,
				originalCommunication.getExtensionAttributes().get(PrepareForMerger.STATUS));
	}

	@Test
	public void testAppMergeAddedCommunication() {
		final ClazzCommunication addedCommunication = flatClazzCommunications.stream()
				.filter(c1 -> "fromSubOrgClazzV2ToClazz1()".equals(c1.getOperationName())).findAny().get();
		assertEquals(addedCommunication.getOperationName() + "is not ADDED.", Status.ADDED,
				addedCommunication.getExtensionAttributes().get(PrepareForMerger.STATUS));
	}

	@Test
	public void testAppMergeAddedAggregatedCommunication() {
		final AggregatedClazzCommunication addedAggregatedCommunication = mergedApplication
				.getAggregatedOutgoingClazzCommunications().stream()
				.filter(ac -> "orgV1.subOrg1V1.clazz2V2".equals(ac.getSourceClazz().getFullQualifiedName())).findAny()
				.get();
		assertEquals("Aggregated is not ADDED", Status.ADDED,
				addedAggregatedCommunication.getExtensionAttributes().get(PrepareForMerger.STATUS));

	}

	@Test
	public void testAppMergeDeletedCommunication() {
		final ClazzCommunication deletedCommunication = flatClazzCommunications.stream()
				.filter(c -> "fromSubClazz1ToSubClazz3".equals(c.getOperationName())).findAny().get();
		assertEquals(deletedCommunication.getOperationName() + "is not DELETED", Status.DELETED,
				deletedCommunication.getExtensionAttributes().get(PrepareForMerger.STATUS));
	}

	@Test
	public void testAppMergeDeletedAggregatedCommunication() {
		final AggregatedClazzCommunication deletedAggregatedCommunication = mergedApplication
				.getAggregatedOutgoingClazzCommunications().stream()
				.filter(ac -> "org1V1.subOrg1V1.subClazz3V1".equals(ac.getTargetClazz().getFullQualifiedName()))
				.findAny().get();

		assertEquals("Aggregated is not DELETED", Status.DELETED,
				deletedAggregatedCommunication.getExtensionAttributes().get(PrepareForMerger.STATUS));
	}

	@Test
	public void testAppMergeEditedAggregatedCommunication() {
		final AggregatedClazzCommunication editedAggregatedCommunication = mergedApplication
				.getAggregatedOutgoingClazzCommunications().stream()
				.filter(ac -> "org1V1.clazz1V1".equals(ac.getSourceClazz().getFullQualifiedName())).findAny().get();

		assertEquals("Aggregated is not EDITED", Status.EDITED,
				editedAggregatedCommunication.getExtensionAttributes().get(PrepareForMerger.STATUS));
	}

}
