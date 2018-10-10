package kr.projectn.vdl.event;

import com.google.common.base.Objects;
import kr.projectn.vdl.core.event.SubmoduleEvent;
import kr.projectn.vdl.core.event.SubmoduleEventListener;
import kr.projectn.vdl.util.ExceptionCollector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventListener extends SubmoduleEventListener {
    Logger logger = LogManager.getLogger(this.getClass());

    @Override
    public void onInitPageLoaded(SubmoduleEvent e) {
        logger.info("[" + e.getSubmodule() + "] Loading page");
    }

    @Override
    public void onPageParsed(SubmoduleEvent e) {
        logger.info("[" + e.getSubmodule() + "] Parsing page");
    }

    @Override
    public void onFetchedVideoList(SubmoduleEvent e) {
        logger.info("[" + e.getSubmodule() + "] Fetching video list from channel");
    }

    @Override
    public void onRetrievedMediaSpec(SubmoduleEvent e) {
        logger.info("[" + e.getSubmodule() + "] Retrieving video URL and metadata");
    }

    @Override
    public void onStoredMediaSpec(SubmoduleEvent e) {
        logger.info("[" + e.getSubmodule() + "] Loaded video URL and metadata");
    }

    @Override
    public void onError(SubmoduleEvent e) {
        if(Objects.equal(e.getExceptionInstance(), null)) {
            ExceptionCollector.collect(e.getErrorMessage());
        } else {
            ExceptionCollector.collect(e.getExceptionInstance());
        }
    }
}
