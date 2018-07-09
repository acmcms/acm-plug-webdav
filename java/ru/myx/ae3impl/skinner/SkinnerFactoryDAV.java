package ru.myx.ae3impl.skinner;

import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.produce.ObjectFactory;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.skinner.Skinner;
import ru.myx.ae3.vfs.Entry;

/**
 * @author myx
 *
 */
public class SkinnerFactoryDAV implements ObjectFactory<Entry, Skinner> {
	
	private static final Class<?>[] TARGETS = {
			Skinner.class
	};

	private static final Class<?>[] SOURCES = {
			Entry.class
	};

	private static final String[] VARIETY = {
			"ae1:WEBDAV", "WEBDAV", "DAV"
	};

	@Override
	public boolean accepts(final String variant, final BaseObject attributes, final Class<?> source) {
		
		return true;
	}

	@Override
	public Skinner produce(final String variant, final BaseObject attributes, final Entry source) {
		
		Report.info("FACTORY/WEBDAV", "Production request: type=" + variant);
		return new SkinnerDAV(source);
	}

	@Override
	public Class<?>[] sources() {
		
		return SkinnerFactoryDAV.SOURCES;
	}

	@Override
	public Class<?>[] targets() {
		
		return SkinnerFactoryDAV.TARGETS;
	}

	@Override
	public String[] variety() {
		
		return SkinnerFactoryDAV.VARIETY;
	}
}
