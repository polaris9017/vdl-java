package kr.projectn.vdl.module;

import kr.projectn.vdl.module.vlive.*;
import kr.projectn.vdl.module.daum.*;
import kr.projectn.vdl.module.naver.*;
import kr.projectn.vdl.module.Facebook.*;
import kr.projectn.vdl.module.Instagram.*;
import kr.projectn.vdl.utils.UrlUtil.ModuleMagic;

/**
 * Created by Kim.K on 2017-05-03.
 */
public class ModuleLoader {
    private int magic;
    private String url;

    public ModuleLoader(String url, int magic) {
        this.magic = magic;
        this.url = url;
    }

    public boolean loadModule() {

        ModuleInterface mod;
        switch(magic){
            case ModuleMagic.MODULE_VLIVE:
                mod = new VOD();
                return mod.Run(url);
            case ModuleMagic.MODULE_VLIVE_CHANNEL:
                mod = new Channel();
                return mod.Run(url);
            case ModuleMagic.MODULE_NAVER:
                mod = new tvcast();
                return mod.Run(url);
            case ModuleMagic.MODULE_DAUM:
                mod = new tvpot();
                return mod.Run(url);
            case ModuleMagic.MODULE_KAKAO:
                mod = new kakao();
                return mod.Run(url);
            case ModuleMagic.MODULE_FACEBOOK:
                mod = new Facebook();
                return mod.Run(url);
            case ModuleMagic.MODULE_INSTAGRAM:
                mod = new Instagram();
                return mod.Run(url);
        }
        return false;
    }
}
