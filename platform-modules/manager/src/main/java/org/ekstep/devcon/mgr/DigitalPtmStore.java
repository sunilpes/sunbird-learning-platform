package org.ekstep.devcon.mgr;

import com.datastax.driver.core.Row;
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
        insert(request.get("identifier"), data);
    }

    private Map<String,Object> getInsertData(Map<String, Object> request) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("school", request.get("school"));
        data.put("grade", request.get("grade"));
        data.put("subject", request.get("subject"));
        data.put("teacher", request.get("teacher"));
        data.put("student", request.get("student"));
        data.put("notes", request.get("notes"));
        return data;
    }


    public Map<String, Object> read(String periodId) {
        List<Row> rows = read("identifier", periodId);
        Row row = rows.get(0);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("school", row.getString("school"));
        data.put("grade", row.getString("grade"));
        data.put("subject", row.getString("subject"));
        data.put("teacher", row.getString("teacher"));
        data.put("student", row.getString("student"));
        data.put("notes", row.getString("notes"));
        return data;

    }


}
