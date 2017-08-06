package kr.projectn.vdl.module;

import kr.projectn.vdl.module.vlive.*;
import kr.projectn.vdl.module.daum.*;
import kr.projectn.vdl.module.naver.*;
import kr.projectn.vdl.module.Facebook.*;
import kr.projectn.vdl.module.Instagram.*;
import kr.projectn.vdl.utils.UrlUtil.ModuleMagic;

/**
 * Module loader class
 *
 * Created by qscx9512 on 2017-05-03.
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
            case ModuleMagic.MODULE_DAUMKAKAO:
                mod = new kakao();
                return mod.Run(url.replace(ModuleMagic.URL_DAUMKAKAO, ModuleMagic.URL_KAKAO));
            case ModuleMagic.MODULE_FACEBOOK:
                mod = new Facebook();
                return mod.Run(url);
            case ModuleMagic.MODULE_INSTAGRAM:
                mod = new Instagram();
                return mod.Run(url);
        }
        return false;
    }

    public static void printModuleVersion() {
        new VOD().getVersionString();
        new Channel().getVersionString();
        new tvcast().getVersionString();
        new tvpot().getVersionString();
        new kakao().getVersionString();
        new Facebook().getVersionString();
        new Instagram().getVersionString();
    }
}
