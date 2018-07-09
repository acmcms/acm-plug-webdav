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

import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.serve.ServeRequest;

/**
 * The InvalidMethod class represents an HTTP or WebDAV method that is either
 * not understood or not implemented.
 * 
 * @see Method
 * @author Marc Eaddy
 * @version 1.1, 14 Dec 1997
 */
@SuppressWarnings("javadoc")
class MethodInvalid extends Method {
	/**
	 * @param query
	 * @see Method
	 */
	MethodInvalid(final ServeRequest query) {
		super( null, query );
	}
	
	@Override
	protected ReplyAnswer run() {
		return Reply.string( "DAV", this.query, "Unknown" ).setCode( Reply.CD_BADMETHOD );
	}
}
