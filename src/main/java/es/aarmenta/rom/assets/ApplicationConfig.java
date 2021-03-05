package es.aarmenta.rom.assets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
	
		
	@Value("${source.roms.directory}")
	protected String romsDir;
	
	@Value("${source.assets.directory}")
	protected String assetsDir;
	
	@Value("${dest.roms.directory}")
	protected String destRomsDir;
	
	@Value("${dest.assets.directory}")
	protected String destAssetsDir;
}
