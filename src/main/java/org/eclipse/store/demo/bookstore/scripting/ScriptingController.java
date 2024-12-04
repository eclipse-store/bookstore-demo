package org.eclipse.store.demo.bookstore.scripting;

import org.rapidpm.dependencies.core.logger.HasLogger;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


/**
 * REST controller which exposes POST /script/run/ to execute arbitrary scripts.
 */

@RestController
@RequestMapping("/script")
public class ScriptingController implements HasLogger
{
	private final ScriptingService scriptingService;
	
	ScriptingController(final ScriptingService scriptingService)
	{
		super();
		this.scriptingService = scriptingService;
	}

	@PostMapping(
		value    = "/run",
		produces = MediaType.APPLICATION_JSON_VALUE
	)
	Object run(@RequestBody final String script)
	{
		this.logger().info("Scripting request: " + script);
		
		try
		{
			return ResponseEntity.ok(
				this.scriptingService.runScript(script)
			);
		}
		catch(final Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	/**
	 * Custom Jackson object mapper for JSON serialization.
	 * 
	 * @return custom object mapper
	 */
	@Bean
	ObjectMapper registerObjectMapper()
	{
		 final ObjectMapper mapper = new ObjectMapper();
		 mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		 mapper.registerModule(new JavaTimeModule());
		 return mapper;
	}
	
}
