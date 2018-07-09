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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import ru.myx.ae3.help.Dom;

/**
 * Define the Generic DAV XML Elements from section 12 of the WebDAV spec
 * <draft-ietf-webdav-protocol-05>
 * 
 * @author Jonathan Shapiro
 * @version 1.0, 23 Nov 1997
 */
class WebdavXML {
	private static Document	doc;
	
	static final Element createElement(final String name) {
		if (WebdavXML.doc == null) {
			synchronized (WebdavXML.class) {
				if (WebdavXML.doc == null) {
					WebdavXML.doc = Dom.createDocument();
				}
			}
		}
		return WebdavXML.doc.createElement( name );
	}
	
	static final Text createTextNode(final String data) {
		if (WebdavXML.doc == null) {
			synchronized (WebdavXML.class) {
				if (WebdavXML.doc == null) {
					WebdavXML.doc = Dom.createDocument();
				}
			}
		}
		return WebdavXML.doc.createTextNode( data );
	}
}
