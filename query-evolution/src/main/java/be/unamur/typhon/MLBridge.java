package be.unamur.typhon;

import io.usethesource.vallang.IValueFactory;

import org.rascalmpl.interpreter.utils.RuntimeExceptionFactory;

import io.usethesource.vallang.IValue;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
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


	
	public MLBridge(IValueFactory vf) {
		this.vf = vf;
	}


	public IValue createMLOperators() {
		return prim2value(loadMlModel());
	}
	
	public IValue HelloRascal() {
		return prim2value("Hello Rascal, It is Java");
	}
	
	
	private String loadMlModel() {
		ResourceSet resourceSet = new ResourceSetImpl();
		URI uri = URI.createFileURI("src/complexModelWithChangeOperators.xmi");
		
		Resource resource = resourceSet.getResource(uri, true);
        Model model = (Model) resource.getContents().get(0);
        
        System.out.println(model.getChangeOperators());


		return "success";
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
