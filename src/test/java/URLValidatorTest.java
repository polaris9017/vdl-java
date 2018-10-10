import kr.projectn.vdl.util.URLValidator;
import org.junit.Test;

import static org.junit.Assert.*;

public class URLValidatorTest {

    @Test
    public void validate() {
        assertTrue(URLValidator.hasList("https://channels.vlive.tv/FA1A3/home"));
    }

    @Test
    public void validateFilePath() {
        assertFalse(URLValidator.validateFilePath("te?st.TXT"));
    }

    @Test
    public void getSocialPostCode() {
        assertEquals(URLValidator.getSocialPostCode("https://scontent-icn1-1.cdninstagram.com/vp/8e257bff92d4c4e57065717b55a273d4/5C6290A0/t51.2885-15/e35/41172566_1177498955734431_7232330601604125236_n.jpg"),
                "41172566_1177498955734431_7232330601604125236_n.jpg");
    }
}