/**
 * Cobertura - http://cobertura.sourceforge.net/
 *
 * Copyright (C) 2003 jcoverage ltd.
 * Copyright (C) 2005 Mark Doliner <thekingant@users.sourceforge.net>
 *
 * Cobertura is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * Cobertura is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cobertura; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package simpleMavenProject;

import static org.junit.Assert.*;

import org.junit.Assert.*;
import org.junit.*;

import java.util.Collection;
import java.util.LinkedList;

public class SimpleTest
{

	final Simple simple = new Simple();

	@Test
	public void testSquare()
	{
		assertEquals(1, simple.square(1));
		assertEquals(1, simple.square(-1));
	}

	@Test
	public void testF()
	{
		assertEquals(1, simple.f(-1));
		assertEquals(12, simple.f(6));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSum()
	{
		@SuppressWarnings("rawtypes")
		Collection c = new LinkedList();
		c.add(new Integer(3));
		c.add(new Integer(5));
		c.add(new Integer(8));
		assertEquals(16, simple.sum(c));
	}
}