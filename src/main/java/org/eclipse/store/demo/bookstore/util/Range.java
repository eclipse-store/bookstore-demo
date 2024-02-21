package org.eclipse.store.demo.bookstore.util;

import java.util.Objects;

/*-
 * #%L
 * EclipseStore BookStore Demo
 * %%
 * Copyright (C) 2023 MicroStream Software
 * %%
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * #L%
 */

public class Range
{
	private final int lowerBound;
	private final int upperBound;
	
	public Range(final int lowerBound, final int upperBound)
	{
		super();
		
		if(lowerBound > upperBound)
		{
			throw new IllegalArgumentException("lowerBound cannot be greater than upper bound, " + lowerBound + " > " + upperBound);
		}
		
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	public int lowerBound()
	{
		return this.lowerBound;
	}
	
	public int upperBound()
	{
		return this.upperBound;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.lowerBound, this.upperBound);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if(this == obj)
		{
			return true;
		}
		if(obj == null)
		{
			return false;
		}
		if(this.getClass() != obj.getClass())
		{
			return false;
		}
		final Range other = (Range)obj;
		return this.lowerBound == other.lowerBound && this.upperBound == other.upperBound;
	}

	@Override
	public String toString()
	{
		return "Range [" + this.lowerBound + " : " + this.upperBound + "]";
	}
	
}
