package org.example;

import org.rusherhack.client.api.RusherHackAPI;
import org.rusherhack.client.api.plugin.Plugin;

/**
 * Example rusherhack plugin
 *
 * @author John200410
 */
public class ExamplePlugin extends Plugin {
	
	@Override
	public void onLoad() {
		
		//logger
		this.getLogger().info("Hello World!");
		
		//creating and registering a new module
		final ExampleModule exampleModule = new ExampleModule();
		RusherHackAPI.getModuleManager().registerFeature(exampleModule);
	}
	
	@Override
	public void onUnload() {
		this.getLogger().info("Example plugin unloaded!");
	}
	
}