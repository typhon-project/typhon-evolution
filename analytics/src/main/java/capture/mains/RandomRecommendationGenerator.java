package capture.mains;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.w3c.dom.Document;

import recommendations.AndRecommendation;
import recommendations.IndexRecommendation;
import recommendations.MergeEntitiesRecommendation;
import recommendations.MigrateEntityRecommendation;
import recommendations.Recommendation;
import recommendations.XorRecommendation;
import typhonml.Cardinality;
import typhonml.Collection;
import typhonml.Column;
import typhonml.Database;
import typhonml.Entity;
import typhonml.EntityAttributeKind;
import typhonml.FunctionalTag;
import typhonml.GraphNode;
import typhonml.KeyValueElement;
import typhonml.NFunctionalTag;
import typhonml.Relation;
import typhonml.Table;

public class RandomRecommendationGenerator {
	private static final int INDEX = 0;
	private static final int MERGE = 1;
	private static final int MIGRATE = 2;
	private static final int XOR = 3;
	private static final int AND = 4;
	
	
//	public static void main(String[] args) throws ParserConfigurationException, TransformerException {
//		RandomRecommendationGenerator g = new RandomRecommendationGenerator();
//		Recommendation[] list = g.randomRecommendationList(0);
//		List<Recommendation> r = Arrays.asList(list);
//		Document document = ConsumePostEvents.getHtml(r);
//		TransformerFactory transformerFactory = TransformerFactory.newInstance();
//		Transformer transformer = transformerFactory.newTransformer();
//		DOMSource domSource = new DOMSource(document);
//		StreamResult streamResult = new StreamResult(new File("C:\\Users\\lmeurice\\Desktop\\test.txtok"));
//
//		// If you use
//		// StreamResult result = new StreamResult(System.out);
//		// the output will be pushed to the standard output ...
//		// You can use that for debugging
//
//		transformer.transform(domSource, streamResult);
//	}
	
	public Recommendation randomRecommendation(int depth) {
		int type = randomType(depth);
		
		switch(type) {
		case INDEX:
			return randomIndex();
		case MERGE:
			return randomMerge();
		case MIGRATE:
			return randomMigrate();
		case XOR:
			return randomXor(depth);
		case AND:
			return randomAnd(depth);
		}
		System.err.println("problem");
		if(true)
			System.exit(0);
		return null;
		
	}
	
	private Recommendation randomAnd(int depth) {
		Recommendation[] list = randomRecommendationList(depth + 1);
		AndRecommendation res = new AndRecommendation(list);
		return res;
	}

	private Recommendation[] randomRecommendationList(int depth) {
		int nb = 2;
		Recommendation[] res = new Recommendation[nb];
		for(int i = 0; i < nb; i++) {
			res[i] = randomRecommendation(depth);
		}
		
		return res;
	}

	private Recommendation randomXor(int depth) {
		Recommendation[] list = randomRecommendationList(depth + 1);
		XorRecommendation res = new XorRecommendation(list);
		return res;
	}

	private Recommendation randomMigrate() {
		Entity ent = new Entity() {
			
			@Override
			public void eSetDeliver(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void eNotify(Notification arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean eDeliver() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public EList<Adapter> eAdapters() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void eUnset(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void eSet(EStructuralFeature arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Resource eResource() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean eIsSet(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean eIsProxy() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Object eInvoke(EOperation arg0, EList<?> arg1) throws InvocationTargetException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object eGet(EStructuralFeature arg0, boolean arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object eGet(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<EObject> eCrossReferences() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<EObject> eContents() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EReference eContainmentFeature() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EStructuralFeature eContainingFeature() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EObject eContainer() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EClass eClass() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public TreeIterator<EObject> eAllContents() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void setName(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setImportedNamespace(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String getName() {
				return "E";
			}
			
			@Override
			public String getImportedNamespace() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<Table> getTables() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<Relation> getRelations() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<NFunctionalTag> getNfunctionalTags() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<KeyValueElement> getKeyValueElements() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<GraphNode> getGraphNodes() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<FunctionalTag> getFunctionalTags() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<Column> getColumns() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<Collection> getCollections() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<EntityAttributeKind> getAttributes() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		
		Database db = new Database() {
			
			@Override
			public void eSetDeliver(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void eNotify(Notification arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean eDeliver() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public EList<Adapter> eAdapters() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void eUnset(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void eSet(EStructuralFeature arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Resource eResource() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean eIsSet(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean eIsProxy() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Object eInvoke(EOperation arg0, EList<?> arg1) throws InvocationTargetException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object eGet(EStructuralFeature arg0, boolean arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object eGet(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<EObject> eCrossReferences() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<EObject> eContents() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EReference eContainmentFeature() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EStructuralFeature eContainingFeature() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EObject eContainer() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EClass eClass() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public TreeIterator<EObject> eAllContents() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void setName(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setImportedNamespace(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String getName() {
				return "db";
			}
			
			@Override
			public String getImportedNamespace() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		MigrateEntityRecommendation res = new MigrateEntityRecommendation(ent, db);
		return res;
	}

	private Recommendation randomMerge() {
		Entity e1 = new Entity() {
			
			@Override
			public void eSetDeliver(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void eNotify(Notification arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean eDeliver() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public EList<Adapter> eAdapters() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void eUnset(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void eSet(EStructuralFeature arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Resource eResource() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean eIsSet(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean eIsProxy() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Object eInvoke(EOperation arg0, EList<?> arg1) throws InvocationTargetException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object eGet(EStructuralFeature arg0, boolean arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object eGet(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<EObject> eCrossReferences() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<EObject> eContents() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EReference eContainmentFeature() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EStructuralFeature eContainingFeature() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EObject eContainer() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EClass eClass() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public TreeIterator<EObject> eAllContents() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void setName(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setImportedNamespace(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String getName() {
				return "E1";
			}
			
			@Override
			public String getImportedNamespace() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<Table> getTables() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<Relation> getRelations() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<NFunctionalTag> getNfunctionalTags() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<KeyValueElement> getKeyValueElements() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<GraphNode> getGraphNodes() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<FunctionalTag> getFunctionalTags() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<Column> getColumns() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<Collection> getCollections() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<EntityAttributeKind> getAttributes() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		Entity e2 = new Entity() {
			
			@Override
			public void eSetDeliver(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void eNotify(Notification arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean eDeliver() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public EList<Adapter> eAdapters() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void eUnset(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void eSet(EStructuralFeature arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Resource eResource() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean eIsSet(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean eIsProxy() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Object eInvoke(EOperation arg0, EList<?> arg1) throws InvocationTargetException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object eGet(EStructuralFeature arg0, boolean arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object eGet(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<EObject> eCrossReferences() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<EObject> eContents() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EReference eContainmentFeature() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EStructuralFeature eContainingFeature() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EObject eContainer() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EClass eClass() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public TreeIterator<EObject> eAllContents() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void setName(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setImportedNamespace(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String getName() {
				return "E2";
			}
			
			@Override
			public String getImportedNamespace() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<Table> getTables() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<Relation> getRelations() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<NFunctionalTag> getNfunctionalTags() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<KeyValueElement> getKeyValueElements() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<GraphNode> getGraphNodes() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<FunctionalTag> getFunctionalTags() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<Column> getColumns() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<Collection> getCollections() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<EntityAttributeKind> getAttributes() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		Relation relation = new Relation() {
			
			@Override
			public void eSetDeliver(boolean arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void eNotify(Notification arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean eDeliver() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public EList<Adapter> eAdapters() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void eUnset(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void eSet(EStructuralFeature arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Resource eResource() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean eIsSet(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean eIsProxy() {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public Object eInvoke(EOperation arg0, EList<?> arg1) throws InvocationTargetException {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object eGet(EStructuralFeature arg0, boolean arg1) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Object eGet(EStructuralFeature arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<EObject> eCrossReferences() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EList<EObject> eContents() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EReference eContainmentFeature() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EStructuralFeature eContainingFeature() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EObject eContainer() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public EClass eClass() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public TreeIterator<EObject> eAllContents() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void setName(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setImportedNamespace(String arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public String getName() {
				return "rel";
			}
			
			@Override
			public String getImportedNamespace() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public void setType(Entity arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setOpposite(Relation arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setIsContainment(Boolean arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void setCardinality(Cardinality arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Entity getType() {
				return e2;
			}
			
			@Override
			public Relation getOpposite() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Boolean getIsContainment() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Cardinality getCardinality() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		MergeEntitiesRecommendation res = new MergeEntitiesRecommendation(e1, e2, relation);
		return res;
	}

	private Recommendation randomIndex() {
		IndexRecommendation res = new IndexRecommendation("dbName", "tableName", "entity", "attribute");
		return res;
	}

	private static int randomType(int depth) {
		return ThreadLocalRandom.current().nextInt(0, (depth > 1) ? 1 : 5);
	}
	

}
