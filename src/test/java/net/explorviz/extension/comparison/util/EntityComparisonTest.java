package net.explorviz.extension.comparison.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.explorviz.model.Clazz;
import net.explorviz.model.Component;

public class EntityComparisonTest {

	private final EntityComparison entityComparison = new EntityComparison();

	// TODO mocken?
	private final Clazz clazz1a = new Clazz();
	private final Clazz clazz1b = new Clazz();
	private final Clazz clazz2 = new Clazz();
	private final Clazz clazz3 = new Clazz();
	private final Clazz clazz4 = new Clazz();
	private final Component clazzComp2 = new Component();

	private final Component comp1a = new Component();
	private final Component comp1b = new Component();
	private final Component comp2a = new Component();
	private final Component comp2b = new Component();
	private final Component comp2c = new Component();
	private final Component comp3a = new Component();
	private final Component comp3b = new Component();
	private final Component comp3c = new Component();

	private final List<Clazz> clazzes1a23 = new ArrayList<>();
	private final List<Clazz> clazzes1b23 = new ArrayList<>();
	private final List<Clazz> clazzes1a2 = new ArrayList<>();
	private final List<Clazz> clazzes1a34 = new ArrayList<>();

	private final List<Component> children1a = new ArrayList<>();
	private final List<Component> children1b = new ArrayList<>();
	private final List<Component> children1c = new ArrayList<>();

	@Before
	public void setUpElements() {
		clazz1a.setFullQualifiedName("clazz1");
		clazz1b.setFullQualifiedName("clazz1");
		clazz2.setFullQualifiedName("clazz2");
		clazz3.setFullQualifiedName("clazz3");
		clazz3.setFullQualifiedName("clazz4");

		clazzComp2.setFullQualifiedName("clazz2");

		comp1a.setFullQualifiedName("comp1");
		comp1b.setFullQualifiedName("comp1");
		// with clazzes
		comp2a.setFullQualifiedName("comp2");
		comp2a.setClazzes(clazzes1a23);
		comp2b.setFullQualifiedName("comp2");
		comp2b.setClazzes(clazzes1b23);
		comp2c.setFullQualifiedName("comp2");
		comp2c.setClazzes(clazzes1a34);
		// with children
		comp3a.setFullQualifiedName("comp3");
		comp3a.setChildren(children1a);
		comp3b.setFullQualifiedName("comp3");
		comp3b.setChildren(children1b);
		comp3c.setFullQualifiedName("comp3");
		comp3c.setChildren(children1c);

		clazzes1a23.add(clazz1a);
		clazzes1a23.add(clazz2);
		clazzes1a23.add(clazz3);

		clazzes1b23.add(clazz1b);
		clazzes1b23.add(clazz2);
		clazzes1b23.add(clazz3);

		clazzes1a2.add(clazz1a);
		clazzes1a2.add(clazz2);

		clazzes1a34.add(clazz1a);
		clazzes1a34.add(clazz3);
		clazzes1a34.add(clazz4);

		children1a.add(comp1a);
		children1a.add(comp2a);
		children1b.add(comp1b);
		children1b.add(comp2b);
		children1c.add(comp1a);
		children1c.add(comp2c);
	}

	@Test
	public void testComponentsEqual() {
		final boolean simpleCompEqual = entityComparison.componentsIdentical(comp1a, comp1b);
		final boolean compWithClazzesEqual = entityComparison.componentsIdentical(comp2a, comp2b);
		final boolean compWithSubCompsEqual = entityComparison.componentsIdentical(comp3a, comp3b);

		assertTrue(simpleCompEqual);
		assertTrue(compWithClazzesEqual);
		assertTrue(compWithSubCompsEqual);
	}

	@Test
	public void testComponentsNotEqual() {
		final boolean simpleCompUnequal = entityComparison.componentsIdentical(comp1a, clazzComp2);
		final boolean compWithClazzesUnequal = entityComparison.componentsIdentical(comp2a, comp2c);
		final boolean compWithSubCompsUnequal = entityComparison.componentsIdentical(comp3a, comp3c);

		assertFalse(simpleCompUnequal);
		assertFalse(compWithClazzesUnequal);
		assertFalse(compWithSubCompsUnequal);
	}

	@Test
	public void testClazzesEqual() {
		final boolean clazzesEqual = entityComparison.clazzesEqual(clazzes1a23, clazzes1b23);

		assertTrue(clazzesEqual);

	}

	@Test
	public void testClazzesNotEqual() {
		final boolean clazzesUnequal = entityComparison.clazzesEqual(clazzes1a23, clazzes1a34);

		assertFalse(clazzesUnequal);
	}

	@Test
	public void testElemEqual() {
		final boolean clazzEqual = entityComparison.elemEqual(clazz1a, clazz1b);

		assertTrue(clazzEqual);
	}

	@Test
	public void testElemNotEqual() {
		final boolean clazzUnequal = entityComparison.elemEqual(clazz2, clazz3);
		final boolean clazzCompUnequal = entityComparison.elemEqual(clazz2, clazzComp2);

		assertFalse(clazzUnequal);
		assertFalse(clazzCompUnequal);
	}

}
