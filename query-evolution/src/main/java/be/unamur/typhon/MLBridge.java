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

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;


import typhonml.TyphonmlPackage;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.IEditorDescriptor;
import com.typhon.evolutiontool.utils.TyphonMLUtils;
import typhonml.Model;
import typhonml.ChangeOperator;

public class MLBridge {
	
	private final IValueFactory vf;
	
	static ResourceSet resourceSet = new ResourceSetImpl();
	
	public MLBridge(IValueFactory vf) {
		this.vf = vf;
	}


    
    public static String normalizeURI(String uri) {
		uri = uri.replaceAll("project://", "platform:/resource/");
		boolean hasFrag = uri.indexOf("#") > -1;
		return hasFrag ? uri.substring(0, uri.indexOf("#")) : uri;
	}

    public IValue load_xmi() {
        Model sourceModel = TyphonMLUtils.loadModelTyphonML("src/complexModelWithChangeOperators.xmi");
        List<ChangeOperator> changeOperator = sourceModel.getChangeOperators();
        
        
        
        return prim2value("Yeah");
    }
	
	

	public IValue get_xmi_loc(String uri) {
		return prim2value("TO DO");
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
	
	public IValue getIDEID() {
		IEditorRegistry r = PlatformUI.getWorkbench().getEditorRegistry();
		IEditorDescriptor[] editors = r.getEditors("complexModelWithChangeOperators.xmi");
		
		String all_desc = "";
		
		for(IEditorDescriptor ied : editors) {
			all_desc = all_desc + "  " + ied.getId();
		}
		
		
		return prim2value(all_desc);
	}
}
