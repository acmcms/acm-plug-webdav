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
 * A Command is a class representation for a subsystem operation that can be
 * done and undone. It is modelled after the Command Design Pattern in "Design
 * Patterns", by Gamma, et al. You must subclass the Command class and implement
 * the DoImpl() and UndoImpl() functions, which do the actual work.
 * 
 * <P>
 * When Command's are created, you must specify whether or not they will Undo()
 * themselves if requested. Some Command's do not need to be able to UnDo()
 * themselves (e.g., ValidateUri) whereas some need to be able to Undo()
 * themselves (e.g., PutResource).
 * 
 * @author Marc Eaddy
 * @version 2.0, 16 Nov 1997
 */
abstract class Command {
	// DATA
	/**
	 * Stores the last return code from the Do() or UnDo() function.
	 */
	protected int	nNativeReturnCode		= WebDavRetCode.WEBDAV_SUCCESS;
	
	protected int	nNativeUndoReturnCode	= WebDavRetCode.WEBDAV_SUCCESS;
	
	/**
	 * Command subclasses implement this function to execute the required
	 * command's functionality.
	 * 
	 * @return Returns <0 for error, 0 for success, >0 for success with info.
	 */
	protected abstract int doImpl();
	
	// MANIPULATORS
	/**
	 * Execute this command.
	 * 
	 * @return Returns <0 for error, 0 for success, >0 for success with info
	 */
	final int execute() {
		this.nNativeReturnCode = this.doImpl();
		return this.nNativeReturnCode;
	}
	
	/**
	 * Rollback this command.
	 * 
	 * @return Returns <0 for error, 0 for success, >0 for success with info.
	 */
	final int executeUndo() {
		// Don't UnDo() if the original Do() failed
		if (this.nNativeReturnCode >= WebDavRetCode.WEBDAV_SUCCESS) {
			// Remember the status code because someone will ask for
			// it later...
			this.nNativeUndoReturnCode = this.undoImpl();
			return this.nNativeUndoReturnCode;
		}
		// We should really distinguish between return codes for
		// Do() and UnDo() so we can provide more detailed error
		// information.
		return WebDavRetCode.WEBDAV_SUCCESS;
	}
	
	// ACCESSORS
	/**
	 * Returns the status code received from the subsystem that is used to
	 * implement the Command subclass.
	 * 
	 * @return Returns <0 for error, 0 for success, >0 for success with info.
	 */
	int getReturnCode() {
		return this.nNativeReturnCode;
	}
	
	int getUndoReturnCode() {
		return this.nNativeUndoReturnCode;
	}
	
	/**
	 * Command subclasses implement this function to provide rollback
	 * 
	 * @return Returns <0 for error, 0 for success, >0 for success with info.
	 */
	protected abstract int undoImpl();
}
