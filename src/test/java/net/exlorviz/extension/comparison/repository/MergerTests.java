package net.exlorviz.extension.comparison.repository;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.TestCase;

@RunWith(Suite.class)
@SuiteClasses({ ClassMergerTest.class, ComponentMergerTest.class, CommunicationMergerTest.class })
public class MergerTests extends TestCase {
	//
	// public static Test suite() {
	// final TestSuite suite = new TestSuite(MergerTests.class.getName());
	// // $JUnit-BEGIN$
	//
	// // $JUnit-END$
	// return suite;
	// }

}
