package ru.myx.ae3impl.server;

import ru.myx.ae1.know.Server;
import ru.myx.ae3.Engine;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.produce.ObjectFactory;
import ru.myx.ae3.report.Report;

/**
 * @author myx
 *
 */
public class ServerFactoryDAV implements ObjectFactory<Object, Server> {
	
	private static final Class<?>[] TARGETS = {
			Server.class
	};

	private static final Class<?>[] SOURCES = null;

	private static final String[] VARIETY = {
			"ae1:WEBDAV"
	};

	@Override
	public boolean accepts(final String variant, final BaseObject attributes, final Class<?> source) {
		
		return true;
	}

	@Override
	public Server produce(final String variant, final BaseObject attributes, final Object source) {
		
		Report.info("FACTORY/WEBDAV", "Production request: type=" + variant);
		final String id = Base.getString(attributes, "id", Engine.createGuid());
		return new ServerDAV(id, attributes);
	}

	@Override
	public Class<?>[] sources() {
		
		return ServerFactoryDAV.SOURCES;
	}

	@Override
	public Class<?>[] targets() {
		
		return ServerFactoryDAV.TARGETS;
	}

	@Override
	public String[] variety() {
		
		return ServerFactoryDAV.VARIETY;
	}
}
