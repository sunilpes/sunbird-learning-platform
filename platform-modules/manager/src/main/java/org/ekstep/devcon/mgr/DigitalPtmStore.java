package org.ekstep.devcon.mgr;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import org.apache.commons.collections.CollectionUtils;
import org.ekstep.cassandra.connector.util.CassandraConnector;
import org.ekstep.cassandra.store.CassandraStore;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DigitalPtmStore extends CassandraStore {

    public DigitalPtmStore() {
        super();
        String keyspace = "devcon";
        String table = "digital_ptm";
        boolean index = false;
        String objectType = "PTM";
        initialise(keyspace, table, objectType, index);
    }

    public void upsert(Map<String, Object> request)
            throws Exception {
        Map<String, Object> data = getInsertData(request);
        insert(request.get("visitor"), data);
    }

    private Map<String,Object> getInsertData(Map<String, Object> request) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("visitor", request.get("visitor"));
        data.put("period", request.get("period"));
        data.put("school", request.get("school"));
        data.put("grade", request.get("grade"));
        data.put("subject", request.get("subject"));
        data.put("teacher", request.get("teacher"));
        data.put("student", request.get("student"));
        data.put("notes", request.get("notes"));
        return data;
    }


    public Map<String, Object> readData(String periodId, String visitorId) {
        String query = "SELECT * from devcon.digital_ptm where period=? and visitor=? Allow Filtering";
        Session session = CassandraConnector.getSession();
        PreparedStatement statement = session.prepare(query);
        BoundStatement boundStatement = new BoundStatement(statement);
        ResultSet resutls = session.execute(boundStatement.bind(periodId, visitorId));
        List<Row> rows = resutls.all();
        if(CollectionUtils.isEmpty(rows)){
            return new HashMap<>();
        }
        Row row = rows.get(0);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("visitor", row.getString("visitor"));
        data.put("period", row.getString("period"));
        data.put("school", row.getString("school"));
        data.put("grade", row.getString("grade"));
        data.put("subject", row.getString("subject"));
        data.put("teacher", row.getString("teacher"));
        data.put("student", row.getString("student"));
        data.put("notes", row.getString("notes"));
        return data;

    }


}
