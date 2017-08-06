/**
 * Copyright 2015 Comcast Cable Communications Management, LLC
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.comcast.viper.hlsparserj.v12;

import java.util.List;

import com.comcast.viper.hlsparserj.MediaPlaylist;
import com.comcast.viper.hlsparserj.PlaylistVersion;
import com.comcast.viper.hlsparserj.tags.UnparsedTag;

/**
 * Concrete class implementation for a V12 media playlist.
 */
public class MediaPlaylistV12 extends MediaPlaylist {

    /**
     * Constructor.
     * @param tags list of tags in this playlist
     */
    public MediaPlaylistV12(final List<UnparsedTag> tags) {
        super(PlaylistVersion.TWELVE, tags);
    }
}
