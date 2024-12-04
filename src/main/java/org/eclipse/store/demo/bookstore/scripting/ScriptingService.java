package org.eclipse.store.demo.bookstore.scripting;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlFeatures;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.introspection.JexlPermissions;
import org.eclipse.store.demo.bookstore.BookStoreDemo;
import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

@Service
public class ScriptingService
{
	private final JexlEngine jexl = new JexlBuilder()
		.permissions(JexlPermissions.UNRESTRICTED) // expose all fields and methods, do not use in production!
		.features(JexlFeatures.createAll())        // we use all features
		.cache(128)
		.strict(true)
		.safe(false)
		.silent(false)
		.create()
	;
	
	
	/**
	 * Executes the JEXL script and returns the result.
	 * 
	 * @param script the script to execute
	 * @return the script's result
	 */
	Object runScript(final String script)
	{
		return this.jexl.createScript(script)
			.execute(this.createContext())
		;
	}


	private MapContext createContext()
	{
		final MapContext context = new MapContext();
		// expose the data root object
		context.set("data", BookStoreDemo.getInstance().data());
		// expose the demo instance
		context.set("demo", BookStoreDemo.getInstance());
		// expose a faker instance
		context.set("faker", Faker.instance());
		return context;
	}
}
