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
package com.comcast.viper.hlsparserj.tags.media;

import com.comcast.viper.hlsparserj.tags.Tag;

/**
 * <pre>
 * Represents the media sequence tag.
 *
 * Each media segment in a Playlist has a unique integer sequence
 * number.  The sequence number of a segment is equal to the sequence
 * number of the segment that preceded it plus one.  The EXT-X-MEDIA-
 * SEQUENCE tag indicates the sequence number of the first segment that
 * appears in a Playlist file.  Its format is:
 *
 *   #EXT-X-MEDIA-SEQUENCE:&lt;number&gt;
 *
 * where number is a decimal-integer.  The sequence number MUST NOT
 * decrease.
 *
 * A Media Playlist file MUST NOT contain more than one EXT-X-MEDIA-
 * SEQUENCE tag.  If the Media Playlist file does not contain an EXT-X-
 * MEDIA-SEQUENCE tag then the sequence number of the first segment in
 * the playlist SHALL be considered to be 0.  A client MUST NOT assume
 * that segments with the same sequence number in different Media
 * Playlists contain matching content.
 *
 * A media URI is not required to contain its sequence number.
 *
 * See Section 6.2.1, Section 6.3.2 and Section 6.3.5 for information on
 * handling the EXT-X-MEDIA-SEQUENCE tag.
 *
 * The EXT-X-MEDIA-SEQUENCE tag MUST NOT appear in a Master Playlist.
 * </pre>
 */
public class MediaSequence extends Tag {

    /**
     * Returns the sequence number attribute.
     * @return sequence number attribute
     */
    public int getSequenceNumber() {
        return Integer.valueOf(tag.getAttributes().get(UNNAMEDATTR0));
    }
}
