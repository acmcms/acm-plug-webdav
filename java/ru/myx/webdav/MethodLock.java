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
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseNativeObject;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.xml.Xml;

/**
 * The PropFindMethod class encapsulates the Commands necessary to get the names
 * and values of properties defined on a resource.
 * 
 * @see Method
 * @author Jonathan Shapiro, Marc Eaddy
 * @version 2.0, 14 Dec 1997
 */
@SuppressWarnings("javadoc")
class MethodLock extends Method {
	
	private final StorageManager storageManager;
	
	private final Depth depth;
	
	private final boolean incorrect;
	
	/**
	 * Create and initialize the PropFindMethod.
	 * 
	 * @param storageManager
	 * 			
	 * @param uri
	 *            - Location of the resource file
	 * @param query
	 */
	MethodLock(final StorageManager storageManager, final Uri uri, final ServeRequest query) {
		super(uri, query);
		this.storageManager = storageManager;
		this.depth = new Depth(Base.getString(query.getAttributes(), "Depth", null));
		this.incorrect = !Method.checkParseXmlParameters("WEBDAV:LOCK", query);
	}
	
	@Override
	protected ReplyAnswer run() {
		
		if (this.incorrect) {
			return Reply.empty("DAV", this.query).setCode(Reply.CD_BADQUERY);
		}
		// {lockscope={exclusive=}, locktype={write=},
		// owner={href=http://www.apple.com/webdav_fs/}}
		final BaseObject parameters = this.query.getParameters();
		final BaseObject lockData = parameters;
		Report.info("WEBDAV:LOCK", "LOCK: " + lockData + ", uri=" + this.uri + ", atrs=" + this.query.getAttributes());
		final DavLock lock = this.storageManager.getLock(this.uri, lockData);
		if (!Base.getString(lock.getLockData(), "owner", "").equals(Base.getString(parameters, "owner", ""))) {
			Report.info(
					"WEBDAV:LOCK",
					"LOCK FAIL:\r\n" + String.valueOf(lock.getLockData().baseGet("owner", BaseObject.UNDEFINED)) + "\r\n"
							+ String.valueOf(parameters.baseGet("owner", BaseObject.UNDEFINED)));
			return Reply.string("DAV", this.query, "Locked already!").setCode(Reply.CD_LOCKED).setContentType("text/plain");
		}
		
		final BaseNativeObject response = new BaseNativeObject();
		final BaseNativeObject lockdiscovery = new BaseNativeObject();
		final BaseNativeObject activelock = new BaseNativeObject();
		final BaseNativeObject locktoken = new BaseNativeObject();
		locktoken.putAppend("href", lock.getLockToken());
		activelock.baseDefineImportAllEnumerable(lockData);
		activelock.putAppend("depth", this.depth.toString());
		activelock.putAppend("timeout", "Infinite");
		activelock.putAppend("locktoken", locktoken);
		lockdiscovery.putAppend("activelock", activelock);
		response.putAppend("lockdiscovery", lockdiscovery);
		
		Report.info("WEBDAV:LOCK", "LOCK REZ:\r\n" + Xml.toXmlString("prop", response, false));
		
		return Reply
				.string(
						"WEBDAV:LOCK", //
						this.query,
						"<?xml version=\"1.0\" encoding=\"utf-8\"?>" + Xml.toXmlString("prop", response, false))//
				.setAttribute("Lock-Token", lock.getLockToken())//
				.setCode(Reply.CD_OK)//
				.setContentType("text/xml");
	}
}
