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
 * Used as a generic superclass for return code classes used by the subsystems.
 * At the Executive level, all we need to know is what the return code and
 * message are and a way to get their HTTP representation. Abstracts the
 * Executive level away from the details of a particular subsystem's error
 * codes.
 * <P>
 * 
 * Error codes should be consistent:
 * <UL>
 * <LI><B>< 0</B> => Error condition
 * <LI><B>= 0</B> => Success
 * <LI><B>> 0</B> => Success with info
 * </UL>
 * 
 * @author Marc Eaddy
 * @version 1.0, 3 Nov 1997
 */
class WebDavRetCode {
	/** General success */
	public static final int	WEBDAV_SUCCESS			= 0;
	
	/** General success with info */
	public static final int	WEBDAV_SUCCESS_W_INFO	= +1;
	
	/** General failure */
	public static final int	WEBDAV_FAILURE			= -1;
}
