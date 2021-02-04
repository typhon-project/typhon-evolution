package com.typhon.evolutiontool.test;

import com.typhon.evolutiontool.utils.TyphonMLUtils;
import it.univaq.disim.typhon.acceleo.services.Services;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.Test;
import typhonml.AddIndex;
import typhonml.MergeEntity;
import typhonml.MigrateEntity;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.Base64;

public class XMIAndTMLTests extends InitialTest {

    private static final String LOCALHOST_URL = "http://localhost:8080/";
    private static final String GET_ML_MODEL_URL = "api/model/ml/";

    private static final String authStringEnc = Base64.getEncoder().encodeToString(("admin:admin1@").getBytes());
    private static final JerseyClient restClient = JerseyClientBuilder.createClient();
    private static WebTarget webTarget = restClient.target(LOCALHOST_URL);


    @Test
    public void testModelManipulation() {
        webTarget = webTarget.path(GET_ML_MODEL_URL + -1);
        String result = webTarget
                .request(MediaType.APPLICATION_OCTET_STREAM)
                .header("Authorization", "Basic " + authStringEnc)
                .get(String.class);
        System.out.println("Result: " + result);
        try (PrintWriter out = new PrintWriter("src/test/resources/xmi.xmi")) {
            out.println(result);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        TyphonMLUtils.typhonMLPackageRegistering();
        sourceModel = TyphonMLUtils.loadModelTyphonML("src/test/resources/xmi.xmi");
        Services.serializeTML(sourceModel, "src/test/resources/tml.tml");
        String tmlContent = null;
        String changeOperators = null;
        try {
            BufferedReader tmlReader = new BufferedReader(new FileReader("src/test/resources/tml.tml"));
            StringBuilder tmlReaderStringBuilder = new StringBuilder();
            String line;
            while ((line = tmlReader.readLine()) != null) {
                tmlReaderStringBuilder.append(line);
            }
            tmlReader.close();
            tmlContent = tmlReaderStringBuilder.toString();
            BufferedReader changeOperatorsReader = new BufferedReader(new FileReader("src/test/resources/tml_changeoperators.tmp"));
            StringBuilder changeOperatorsStringBuilder = new StringBuilder();
            while ((line = changeOperatorsReader.readLine()) != null) {
                changeOperatorsStringBuilder.append(line);
            }
            changeOperatorsReader.close();
            changeOperators = changeOperatorsStringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(tmlContent);
        System.out.println(changeOperators);
        assert tmlContent != null;
        assert changeOperators != null;
        try (PrintWriter out = new PrintWriter("src/test/resources/tml.tml")) {
            out.print(tmlContent + " " + changeOperators);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        sourceModel = Services.loadXtextModel("src/test/resources/tml.tml");
        Services.serialize(sourceModel, "src/test/resources/xmi.xmi");

        assert ((MigrateEntity) sourceModel.getChangeOperators().get(0)).getEntity().getName().equals("Customers");
//        assert ((AddIndex) sourceModel.getChangeOperators().get(0)).getTable().getName().equals("AddressDB");
//        assert !((AddIndex) sourceModel.getChangeOperators().get(0)).getAttributes().isEmpty();
//        assert ((MergeEntity) sourceModel.getChangeOperators().get(1)).getFirstEntityToMerge().getName().equals("User");
//        assert ((MergeEntity) sourceModel.getChangeOperators().get(1)).getSecondEntityToMerge().getName().equals("Address");
//        assert ((MergeEntity) sourceModel.getChangeOperators().get(1)).getNewEntityName().equals("User.address");
    }
}
