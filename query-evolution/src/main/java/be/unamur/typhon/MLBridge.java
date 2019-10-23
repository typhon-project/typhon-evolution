package be.unamur.typhon;

import io.usethesource.vallang.IValueFactory;

public class MLBridge {
	
	private final IValueFactory vf;

	public MLBridge(IValueFactory vf) {
		this.vf = vf;
	}
	
	public String HelloBridge() {
		System.out.println("Test");
		return "Hello Bridge";
	}
}
