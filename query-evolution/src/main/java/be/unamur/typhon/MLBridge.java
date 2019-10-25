package be.unamur.typhon;

import io.usethesource.vallang.IValueFactory;
import io.usethesource.vallang.type.TypeStore;

import org.rascalmpl.interpreter.utils.RuntimeExceptionFactory;
import org.rascalmpl.uri.URIResolverRegistry;

import io.usethesource.vallang.IConstructor;
import io.usethesource.vallang.ISourceLocation;
import io.usethesource.vallang.IValue;
import io.usethesource.vallang.type.Type;


import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import typhonml.TyphonmlPackage;
import typhonml.Model;


public class MLBridge {
	
	private final IValueFactory vf;
	
	static ResourceSet resourceSet = new ResourceSetImpl();

    /**
     * Method needed before use TyphonML classes. It register all the needed resources.
     */
    public static void typhonMLPackageRegistering() {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        //Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("tml", new TyphonMLStandaloneSetup().createInjectorAndDoEMFRegistration().getInstance(XtextResourceSet.class));
        resourceSet.getPackageRegistry().put(TyphonmlPackage.eINSTANCE.getNsURI(), TyphonmlPackage.eINSTANCE);
    }
    
    public static String normalizeURI(String uri) {
		uri = uri.replaceAll("project://", "platform:/resource/");
		boolean hasFrag = uri.indexOf("#") > -1;
		return hasFrag ? uri.substring(0, uri.indexOf("#")) : uri;
	}


	
	public MLBridge(IValueFactory vf) {
		this.vf = vf;
	}


	public IValue createMLOperators() {
		// TODO
		return prim2value("TO DO");
	}
	
	public IValue HelloRascal() {
		return prim2value("Hello Rascal, It is Java");
	}
	
	/*
	public IConstructor get_change_operator(IValue typeOfTyphonML, ISourceLocation path) {
		//Connections.boot();

		TypeStore ts = new TypeStore(); // start afresh
		
		//Resource r = xtextRS.getResource(URI.createURI("file:///Users/tvdstorm/CWI/typhonql/src/newmydb4.xmi"), true);
		final ISourceLocation mydb = vf.sourceLocation("file:///Users/tvdstorm/CWI/typhonql/src/newmydb4.xmi");
		
		try {
			Resource r = loadResource(mydb);
			typhonml.Model m = (typhonml.Model)r.getContents().get(0);
			Type rt = tr.valueToType((IConstructor) typeOfTyphonML, ts);
			Convert.declareRefType(ts);
			Convert.declareMaybeType(ts);
			return (IConstructor) Convert.obj2value(m, rt, vf, ts, mydb);
		} catch (IOException e) {
			throw RuntimeExceptionFactory.io(vf.string(e.getMessage()), null, null);
		}
	}
	
	private  Resource loadResource(ISourceLocation uri) throws IOException {
		ResourceSet rs = new ResourceSetImpl();
		java.net.URI x = uri.getURI();
		URI bla = URI.createURI(normalizeURI(x.toString()));
		Resource res = rs.getResource(bla, true);
		URIResolverRegistry reg = URIResolverRegistry.getInstance();
		res.load(reg.getInputStream(uri), Collections.emptyMap());
		return res;
	}
	
	*/
	
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
