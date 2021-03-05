package es.aarmenta.rom.assets;

import java.io.File;

public class IsFileFilter implements java.io.FileFilter {

	@Override
	public boolean accept(File pathname) {		
		return pathname.isFile();
	}

}
