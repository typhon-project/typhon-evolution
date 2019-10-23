package be.unamur.typhon;

import io.usethesource.vallang.IValueFactory;

import org.rascalmpl.interpreter.utils.RuntimeExceptionFactory;

import io.usethesource.vallang.IValue;

public class MLBridge {
	
	private final IValueFactory vf;

	public MLBridge(IValueFactory vf) {
		this.vf = vf;
	}
	
	public IValue HelloBridge() {
		System.out.println("Test");
		return prim2value("Hello Bridge");
	}
	
	private IValue prim2value(Object obj) {
		assert obj != null;
		
		if (obj instanceof String) {
			return vf.string((String)obj);
		}
		if (obj instanceof Integer) {
			return vf.integer(((Integer)obj).longValue());
		}
		if (obj instanceof Boolean) {
			return vf.bool(((Boolean)obj).booleanValue());
		}
		if (obj instanceof Double) {
			return vf.real(((Double)obj).doubleValue());
		}
		if (obj instanceof Float) {
			return vf.real(((Float)obj).doubleValue());
		}

		
		throw RuntimeExceptionFactory.illegalArgument(vf.string(obj.getClass().getName()), null, null, 
				"Cannot convert Java object to Rascal value");
	}
}
