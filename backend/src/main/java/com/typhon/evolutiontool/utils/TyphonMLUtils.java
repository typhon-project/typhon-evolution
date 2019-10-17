package main.java.com.typhon.evolutiontool.utils;

import main.java.com.typhon.evolutiontool.entities.SMO;
import it.univaq.disim.typhon.TyphonMLStandaloneSetup;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.xtext.resource.XtextResourceSet;
import typhonml.ChangeOperator;
import typhonml.Model;
import typhonml.TyphonmlPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TyphonMLUtils {

    static ResourceSet resourceSet = new ResourceSetImpl();

    /**
     * Method needed before use TyphonML classes. It register all the needed resources.
     */
    public static void typhonMLPackageRegistering() {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("tml", new TyphonMLStandaloneSetup().createInjectorAndDoEMFRegistration().getInstance(XtextResourceSet.class));
        resourceSet.getPackageRegistry().put(TyphonmlPackage.eINSTANCE.getNsURI(), TyphonmlPackage.eINSTANCE);
    }

    /**
     * Used to load model from path. It will load as Ecore Resource
     *
     * @param modelPath
     * @return
     */
    public static Resource loadModel(String modelPath) {
        ResourceSet resourceSet = new ResourceSetImpl();
        URI uri = URI.createFileURI(modelPath);
        Resource resource = resourceSet.getResource(uri, true);
        return resource;
    }

    /**
     * Used to load model from path. It will load as typhonml.Model
     *
     * @param modelPath
     * @return
     */
    public static Model loadModelTyphonML(String modelPath) {
        ResourceSet resourceSet = new ResourceSetImpl();
        URI uri = URI.createFileURI(modelPath);
        Resource resource = resourceSet.getResource(uri, true);
        Model model = (Model) resource.getContents().get(0);
        return model;
    }


    public static void saveModel(Model typhonmlModel, String path) {
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put("xmi", new XMIResourceFactoryImpl());

        Resource resource = resourceSet.createResource(URI.createURI(path));
        resource.getContents().add(typhonmlModel);

        try {
            resource.save(Collections.EMPTY_MAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<SMO> getListSMOFromChangeOperators(Model model) {
        List<SMO> smos = new ArrayList<>();
        List<ChangeOperator> changeOperatorList = model.getChangeOperators();
        for (ChangeOperator changeOperator : changeOperatorList) {
            smos.add(SMOFactory.createSMOAdapterFromChangeOperator(changeOperator));
        }
        return smos;
    }

    public static void removeChangeOperators(Model model) {
        model.getChangeOperators().clear();
    }
}
