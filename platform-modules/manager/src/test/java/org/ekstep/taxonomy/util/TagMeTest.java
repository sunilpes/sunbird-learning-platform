package org.ekstep.taxonomy.util;

import org.ekstep.common.dto.Response;
import org.ekstep.common.util.HttpRestUtil;
import org.ekstep.common.util.YouTubeUrlUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({ "classpath:servlet-context.xml" })
public class TagMeTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testTagMe() throws Exception {
       String tagMeApiUrlPrefix = "https://tagme.d4science.org/tagme/tag?lang=en&gcube-token=1e1f2881-62ec-4b3e-9036-9efe89347991-843339462&text=";
       String tagMeApiUrlSuffix = "&include_abstarction=True&epsilon=0.6";
        String text = "I_am_Kumar_Gauraw";
        String uri = tagMeApiUrlPrefix+text+tagMeApiUrlSuffix;
       Response resp = HttpRestUtil.callTagMeApi(uri, null, new HashMap<String, String>());
        System.out.println("Response Code : "+resp.getResponseCode());
        System.out.println("resp :::: "+resp.getResult());
    }
}
