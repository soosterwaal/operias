package operias.output.html;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import operias.diff.SourceDiffState;

import org.junit.Test;

public class HTMLOverviewTest {

	/**
	 * Test the generation of the bar html when the coverage increase
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testCoverageBarHTMLIncreased() {
		
		HTMLOverview overview = new HTMLOverview(null, new ArrayList<String> ());
		
		Class[] parameterTypes = new Class[3];
		parameterTypes[0] = double.class;
		parameterTypes[1] = double.class;
		parameterTypes[2] = SourceDiffState.class;
		
		try {
			Method barHTMLMethod = overview.getClass().getDeclaredMethod("generateCoverageBarHTML", parameterTypes);
			
			barHTMLMethod.setAccessible(true);
			
			Object[] args = new Object[3];
			args[0] = (double)0.0;
			args[1] = (double)0.5525;
			args[2] = SourceDiffState.CHANGED;
			
			String barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage increased from 0.0% to 55.25%'><div class='originalCoverage' style='width:0.0%'> </div><div class='increasedCoverage'  style='width:55.0%'> </div><div class='originalNotCoverage' style='width:45.0%'> </div></div>", barHTML);
		
			
			args = new Object[3];
			args[0] = (double)0.0;
			args[1] = (double)0.5575;
			args[2] = SourceDiffState.CHANGED;
			
			barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage increased from 0.0% to 55.75%'><div class='originalCoverage' style='width:0.0%'> </div><div class='increasedCoverage'  style='width:56.0%'> </div><div class='originalNotCoverage' style='width:44.0%'> </div></div>", barHTML);
		
			args = new Object[3];
			args[0] = (double)0.5525;
			args[1] = (double)0.5575;
			args[2] = SourceDiffState.CHANGED;
			
			barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage increased from 55.25% to 55.75%'><div class='originalCoverage' style='width:55.0%'> </div><div class='increasedCoverage'  style='width:1.0%'> </div><div class='originalNotCoverage' style='width:44.0%'> </div></div>", barHTML);
	
			args = new Object[3];
			args[0] = (double)0.5555;
			args[1] = (double)0.5575;
			args[2] = SourceDiffState.CHANGED;
			
			barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage increased from 55.55% to 55.75%'><div class='originalCoverage' style='width:55.0%'> </div><div class='increasedCoverage'  style='width:1.0%'> </div><div class='originalNotCoverage' style='width:44.0%'> </div></div>", barHTML);
		
		
		} catch (SecurityException e) {
			fail(e.getMessage());
		} catch (NoSuchMethodException e) {
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test the generation of bar html when the coverage stays the same
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testCoverageBarHTMLSame() {
		
		HTMLOverview overview = new HTMLOverview(null, new ArrayList<String> ());
		
		Class[] parameterTypes = new Class[3];
		parameterTypes[0] = double.class;
		parameterTypes[1] = double.class;
		parameterTypes[2] = SourceDiffState.class;
		
		try {
			Method barHTMLMethod = overview.getClass().getDeclaredMethod("generateCoverageBarHTML", parameterTypes);
			
			barHTMLMethod.setAccessible(true);
			
			Object[] args = new Object[3];
			args[0] = (double)0.5525;
			args[1] = (double)0.5525;
			args[2] = SourceDiffState.CHANGED;
			
			String barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage stayed the same at 55.25%'><div class='originalCoverage' style='width:55.0%'> </div><div class='originalNotCoverage' style='width:45.0%'> </div></div>", barHTML);
		
			args = new Object[3];
			args[0] = (double)0.001;
			args[1] = (double)0.001;
			args[2] = SourceDiffState.CHANGED;
			
			barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage stayed the same at 0.1%'><div class='originalCoverage' style='width:1.0%'> </div><div class='originalNotCoverage' style='width:99.0%'> </div></div>", barHTML);


		
		} catch (SecurityException e) {
			fail(e.getMessage());
		} catch (NoSuchMethodException e) {
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test the generation of bar html for a new file
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testCoverageBarHTMLNew() {
		
		HTMLOverview overview = new HTMLOverview(null, new ArrayList<String> ());
		
		Class[] parameterTypes = new Class[3];
		parameterTypes[0] = double.class;
		parameterTypes[1] = double.class;
		parameterTypes[2] = SourceDiffState.class;
		
		try {
			Method barHTMLMethod = overview.getClass().getDeclaredMethod("generateCoverageBarHTML", parameterTypes);
			
			barHTMLMethod.setAccessible(true);
			
			Object[] args = new Object[3];
			args[0] = (double)0.0;
			args[1] = (double)0.5525;
			args[2] = SourceDiffState.NEW;
			
			String barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage is 55.25%'><div class='increasedCoverage' style='width:55.0%'> </div><div class='decreasedCoverage' style='width:45.0%'> </div></div>", barHTML);
		
			args = new Object[3];
			args[0] = (double)0.0;
			args[1] = (double)0.001;
			args[2] = SourceDiffState.NEW;
			
			barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage is 0.1%'><div class='increasedCoverage' style='width:1.0%'> </div><div class='decreasedCoverage' style='width:99.0%'> </div></div>", barHTML);


		
		} catch (SecurityException e) {
			fail(e.getMessage());
		} catch (NoSuchMethodException e) {
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Test the generation of bar html when the coverage stays the same
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testCoverageBarHTMLDeleted() {
		
		HTMLOverview overview = new HTMLOverview(null, new ArrayList<String> ());
		
		Class[] parameterTypes = new Class[3];
		parameterTypes[0] = double.class;
		parameterTypes[1] = double.class;
		parameterTypes[2] = SourceDiffState.class;
		
		try {
			Method barHTMLMethod = overview.getClass().getDeclaredMethod("generateCoverageBarHTML", parameterTypes);
			
			barHTMLMethod.setAccessible(true);
			
			Object[] args = new Object[3];
			args[0] = (double)0.5525;
			args[1] = (double)0.0;
			args[2] = SourceDiffState.DELETED;
			
			String barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage was 55.25%'><div class='originalCoverage' style='width:55.0%'> </div><div class='originalNotCoverage' style='width:45.0%'> </div></div>", barHTML);
		
			args = new Object[3];
			args[0] = (double)0.001;
			args[1] = (double)0.0;
			args[2] = SourceDiffState.DELETED;
			
			barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage was 0.1%'><div class='originalCoverage' style='width:1.0%'> </div><div class='originalNotCoverage' style='width:99.0%'> </div></div>", barHTML);


		
		} catch (SecurityException e) {
			fail(e.getMessage());
		} catch (NoSuchMethodException e) {
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test the html generation of the bar html for a decrease in coverage, fixing issue #65
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testCoverageBarsHTMLPercentage() {
		
		HTMLOverview overview = new HTMLOverview(null, new ArrayList<String> ());
		
		Class[] parameterTypes = new Class[3];
		parameterTypes[0] = double.class;
		parameterTypes[1] = double.class;
		parameterTypes[2] = SourceDiffState.class;
		
		try {
			Method barHTMLMethod = overview.getClass().getDeclaredMethod("generateCoverageBarsHTML", parameterTypes);

			Object[] args = new Object[3];
			args[0] = (double)0.5525;
			args[1] = (double)0.6576;
			args[2] = SourceDiffState.CHANGED;
			
			String barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<td><div class='coverageChangeBar' title='Coverage increased from 55.25% to 65.76%'><div class='originalCoverage' style='width:55.0%'> </div><div class='increasedCoverage'  style='width:11.0%'> </div><div class='originalNotCoverage' style='width:34.0%'> </div></div><span class='inceasedText'>+10.51%</span></td>", barHTML);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test the html generation of the bar html for a decrease in coverage
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testCoverageBarHTMLDecreased() {
		
		HTMLOverview overview = new HTMLOverview(null, new ArrayList<String> ());
		
		Class[] parameterTypes = new Class[3];
		parameterTypes[0] = double.class;
		parameterTypes[1] = double.class;
		parameterTypes[2] = SourceDiffState.class;
		
		try {
			Method barHTMLMethod = overview.getClass().getDeclaredMethod("generateCoverageBarHTML", parameterTypes);
			
			barHTMLMethod.setAccessible(true);
			
			Object[] args = new Object[3];
			args[0] = (double)0.5525;
			args[1] = (double)0.0;
			args[2] = SourceDiffState.CHANGED;
			
			String barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage decreased from 55.25% to 0.0%'><div class='originalCoverage' style='width:0.0%'> </div><div class='decreasedCoverage'  style='width:55.0%'> </div><div class='originalNotCoverage' style='width:45.0%'> </div></div>", barHTML);
		
			
			args = new Object[3];
			args[0] = (double)0.5575;
			args[1] = (double)0.0;
			args[2] = SourceDiffState.CHANGED;
			
			barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage decreased from 55.75% to 0.0%'><div class='originalCoverage' style='width:0.0%'> </div><div class='decreasedCoverage'  style='width:56.0%'> </div><div class='originalNotCoverage' style='width:44.0%'> </div></div>", barHTML);
		
			args = new Object[3];
			args[0] = (double)0.5575;
			args[1] = (double)0.5525;
			args[2] = SourceDiffState.CHANGED;
			
			barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage decreased from 55.75% to 55.25%'><div class='originalCoverage' style='width:55.0%'> </div><div class='decreasedCoverage'  style='width:1.0%'> </div><div class='originalNotCoverage' style='width:44.0%'> </div></div>", barHTML);
	
			args = new Object[3];
			args[0] = (double)0.5575;
			args[1] = (double)0.5555;
			args[2] = SourceDiffState.CHANGED;
			
			barHTML = (String)barHTMLMethod.invoke(overview, args);
			
			assertEquals("<div class='coverageChangeBar' title='Coverage decreased from 55.75% to 55.55%'><div class='originalCoverage' style='width:55.0%'> </div><div class='decreasedCoverage'  style='width:1.0%'> </div><div class='originalNotCoverage' style='width:44.0%'> </div></div>", barHTML);
		
		
		} catch (SecurityException e) {
			fail(e.getMessage());
		} catch (NoSuchMethodException e) {
			fail(e.getMessage());
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (IllegalAccessException e) {
			fail(e.getMessage());
		} catch (InvocationTargetException e) {
			fail(e.getMessage());
		}
	}
}
