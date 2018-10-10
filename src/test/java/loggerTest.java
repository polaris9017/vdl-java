import kr.projectn.vdl.util.ExceptionCollector;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

public class loggerTest {
    @Test
    public void loggerTest1() {
        Logger logger = LogManager.getRootLogger();
        logger.info("Info log test");
        logger.debug("Debug log test");
        logger.warn("Warn log test");
        /*logger.error("Error log test");
        ExceptionCollector.collect(new TestException());
        ExceptionCollector.collect("test");*/
    }

    private class TestException extends Exception {
        @Override
        public String getMessage() {
            return "Test exception.";
        }
    }
}
