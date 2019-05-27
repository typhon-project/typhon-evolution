package com.typhon.evolutiontool.utils;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import typhonml.Model;
import typhonml.TyphonmlPackage;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class TyphonMLUtils {

    static ResourceSet resourceSet = new ResourceSetImpl();

    /**
     * Method needed before use TyphonML classes. It register all the needed resources.
     */
    public static void typhonMLPackageRegistering() {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
        resourceSet.getPackageRegistry().put(TyphonmlPackage.eINSTANCE.getNsURI(), TyphonmlPackage.eINSTANCE);
    }

    /**
     * Used to load model from path. It will load as Ecore Resource
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
}
