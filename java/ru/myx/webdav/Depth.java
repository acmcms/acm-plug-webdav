/*
 * Copyright 1997-1998 by Marc Eaddy, Jonathan Shapiro, Shao Rong; ALL RIGHTS
 * RESERVED
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for research and educational purpose and without fee is hereby
 * granted, provided that the above copyright notice appear in all copies and
 * that both that the copyright notice and warranty disclaimer appear in
 * supporting documentation, and that the names of the copyright holders or any
 * of their entities not be used in advertising or publicity pertaining to
 * distribution of the software without specific, written prior permission. Use
 * of this software in whole or in parts for direct commercial advantage
 * requires explicit prior permission.
 * 
 * The copyright holders disclaim all warranties with regard to this software,
 * including all implied warranties of merchantability and fitness. In no event
 * shall the copyright holders be liable for any special, indirect or
 * consequential damages or any damages whatsoever resulting from loss of use,
 * data or profits, whether in an action of contract, negligence or other
 * tortuous action, arising out of or in connection with the use or performance
 * of this software.
 */
package ru.myx.webdav;

/**
 * Represents the value of a Depth HTTP header. After initializing, you must
 * call isValid() to make sure the Depth object is valid.
 * 
 * @author Marc Eaddy
 * @version 1.1, 14 Dec 1997
 */
public class Depth {
	static final int	INVALID		= -2;
	
	static final int	ZERO		= 0;
	
	static final int	ONE			= 1;
	
	static final int	NOROOT		= 2;
	
	static final int	INFINITY	= -1;
	
	private int			nDepth		= Depth.INVALID;
	
	/**
	 * Initialize a Depth object with an integer.
	 * 
	 * @param nDepth
	 *            - must be 0 or 1
	 */
	public Depth(final int nDepth) {
		if (nDepth == 0 || nDepth == 1) {
			this.nDepth = nDepth;
		}
	}
	
	/**
	 * Initialize a Depth object with the value of the Depth HTTP header.
	 * 
	 * @param strDepthHeader
	 *            - Must be "infinity", "0", "1" or "1,noroot"
	 */
	public Depth(final String strDepthHeader) {
		// First see if the Depth Header is either not set or
		// its value is "infinity"
		if (null == strDepthHeader || strDepthHeader.equalsIgnoreCase( "infinity" )) {
			this.nDepth = Depth.INFINITY;
		} else //
		if (strDepthHeader.equalsIgnoreCase( "1,noroot" )) {
			this.nDepth = Depth.NOROOT;
		} else //
		if (strDepthHeader.equalsIgnoreCase( "0" )) {
			this.nDepth = Depth.ZERO;
		} else //
		if (strDepthHeader.equalsIgnoreCase( "1" )) {
			this.nDepth = Depth.ONE;
		} else //
		{
			this.nDepth = Depth.INVALID;
		}
	}
	
	/**
	 * Returns the Depth.
	 * 
	 * @return one of { Depth.INVALID, Depth.ZERO, Depth.ONE, Depth.NOROOT,
	 *         Depth.INFINITY }
	 */
	public int getValue() {
		return this.nDepth;
	}
	
	/**
	 * @return true if the Depth is valid, otherwise false
	 */
	public boolean isValid() {
		return this.nDepth != Depth.INVALID;
	}
	
	/**
	 * Returns the Depth string.
	 * 
	 * @return one of { "invalid", "0", "1", "1,noroot", "infinity" }
	 */
	@Override
	public String toString() {
		if (this.nDepth == Depth.INVALID) {
			return "invalid";
		}
		if (this.nDepth == Depth.INFINITY) {
			return "infinity";
		}
		if (this.nDepth == Depth.NOROOT) {
			return "1,noroot";
		}
		return Integer.toString( this.nDepth );
	}
}
